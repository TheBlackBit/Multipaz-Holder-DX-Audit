package com.koombea.sample.multipaz

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.multipaz.compose.prompt.PromptDialogs
import org.multipaz.prompt.PromptModel

@Composable
fun App(promptModel: PromptModel) {
    MaterialTheme {
        // Keeps all prompts to app´s style
        PromptDialogs(promptModel)
        MultiPazSample()
    }
}