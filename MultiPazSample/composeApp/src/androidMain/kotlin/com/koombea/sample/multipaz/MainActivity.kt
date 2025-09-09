package com.koombea.sample.multipaz

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import org.multipaz.context.initializeApplication
import org.multipaz.prompt.AndroidPromptModel

class MainActivity : FragmentActivity() {
    val androidPromptModel = AndroidPromptModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initializeApplication(this.applicationContext)

        setContent {
            App(androidPromptModel)
        }
    }
}
