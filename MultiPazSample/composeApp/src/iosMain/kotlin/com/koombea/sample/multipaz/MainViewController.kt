package com.koombea.sample.multipaz

import androidx.compose.ui.window.ComposeUIViewController
import org.multipaz.prompt.IosPromptModel

fun MainViewController() = ComposeUIViewController { App(IosPromptModel()) }