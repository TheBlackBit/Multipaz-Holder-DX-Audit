package com.koombea.sample.multipaz

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.encodeToByteString
import org.multipaz.asn1.ASN1Integer
import org.multipaz.compose.qrcode.generateQrCode
import org.multipaz.crypto.Algorithm
import org.multipaz.crypto.Crypto
import org.multipaz.crypto.EcCurve
import org.multipaz.crypto.X500Name
import org.multipaz.crypto.X509CertChain
import org.multipaz.document.buildDocumentStore
import org.multipaz.documenttype.knowntypes.DrivingLicense
import org.multipaz.mdoc.connectionmethod.MdocConnectionMethodBle
import org.multipaz.mdoc.engagement.EngagementGenerator
import org.multipaz.mdoc.util.MdocUtil
import org.multipaz.securearea.CreateKeySettings
import org.multipaz.securearea.SecureAreaRepository
import org.multipaz.util.Platform
import org.multipaz.util.UUID
import org.multipaz.util.toBase64Url
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
@Composable
fun MultiPazSample() {
    val scope = rememberCoroutineScope()
    val qrPayload = remember { mutableStateOf<ByteString?>(null) }

    LaunchedEffect(Unit) {
        val store = initializeDocumentStore()
        createSampleDocumentIfNeeded(store)
        qrPayload.value = generateQrPayload()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (qrPayload.value == null) {
            CircularProgressIndicator()
        } else {
            val qrCode = remember {
                generateQrCode(
                    "mdoc:" + qrPayload.value!!.toByteArray().toBase64Url()
                )
            }
            Text("Present this QR to verifier")
            Spacer(Modifier.height(8.dp))
            Image(
                bitmap = qrCode,
                contentDescription = "QR Code",
                contentScale = ContentScale.FillWidth
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { scope.launch { qrPayload.value = null } }) { Text("Reset") }
        }
    }
}


@OptIn(ExperimentalTime::class)
private suspend fun initializeDocumentStore(): org.multipaz.document.DocumentStore {
    val storage = Platform.nonBackedUpStorage
    val secureArea = Platform.getSecureArea()
    return buildDocumentStore(
        storage,
        SecureAreaRepository.Builder().add(secureArea).build()
    ) {}
}

@OptIn(ExperimentalTime::class)
private suspend fun createSampleDocumentIfNeeded(store: org.multipaz.document.DocumentStore) {
    if (store.listDocuments().isNotEmpty()) return

    val doc = store.createDocument("Ever's Driving License", "Sample mDL")
    val now = Clock.System.now()
    val dsKey = Crypto.createEcPrivateKey(EcCurve.P256)
    val secureArea = Platform.getSecureArea()

    DrivingLicense.getDocumentType().createMdocCredentialWithSampleData(
        document = doc,
        secureArea = secureArea,
        createKeySettings = CreateKeySettings(
            Algorithm.ESP256,
            "Challenge".encodeToByteString(),
            true
        ),
        dsKey = dsKey,
        dsCertChain = createCertificateChain(dsKey, now),
        signedAt = now,
        validFrom = now,
        validUntil = now + 365.days
    )
}

@OptIn(ExperimentalTime::class)
private fun createCertificateChain(
    dsKey: org.multipaz.crypto.EcPrivateKey,
    now: Instant
): X509CertChain {
    return X509CertChain(
        listOf(
            MdocUtil.generateDsCertificate(
                iacaCert = MdocUtil.generateIacaCertificate(
                    iacaKey = Crypto.createEcPrivateKey(EcCurve.P256),
                    subject = X500Name.fromName("CN=IACA"),
                    serial = ASN1Integer.fromRandom(128),
                    validFrom = now,
                    validUntil = now + 365.days,
                    issuerAltNameUrl = "https://issuer.example.com",
                    crlUrl = "https://issuer.example.com/crl"
                ),
                iacaKey = Crypto.createEcPrivateKey(EcCurve.P256),
                dsKey = dsKey.publicKey,
                subject = X500Name.fromName("CN=DS"),
                serial = ASN1Integer.fromRandom(128),
                validFrom = now,
                validUntil = now + 365.days
            )
        )
    )
}

private fun generateQrPayload(): ByteString {
    val engagement = EngagementGenerator(Crypto.createEcPrivateKey(EcCurve.P256).publicKey, "1.0")
    engagement.addConnectionMethods(
        listOf(
            MdocConnectionMethodBle(
                supportsPeripheralServerMode = false,
                supportsCentralClientMode = true,
                peripheralServerModeUuid = null,
                centralClientModeUuid = UUID.randomUUID()
            )
        )
    )
    return ByteString(engagement.generate())
}