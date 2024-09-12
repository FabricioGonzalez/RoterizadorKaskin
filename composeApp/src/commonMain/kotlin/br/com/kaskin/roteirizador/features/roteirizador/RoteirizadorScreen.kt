package br.com.kaskin.roteirizador.features.roteirizador

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun RoteirizadorScreen(modifier: Modifier = Modifier) {

    val webViewState = rememberWebViewState("https://wayds.net:8081/")

    WebView(modifier = modifier.fillMaxSize(), state =  webViewState)
}