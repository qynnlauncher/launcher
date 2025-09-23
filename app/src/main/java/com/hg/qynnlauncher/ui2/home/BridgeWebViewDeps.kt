package com.hg.qynnlauncher.ui2.home

import android.webkit.WebView
import androidx.compose.runtime.State
import com.hg.qynnlauncher.api2.webview.QYNNWebChromeClient
import com.hg.qynnlauncher.api2.webview.QYNNWebViewClient

data class QYNNWebViewDeps(
    val webViewClient: QYNNWebViewClient,
    val chromeClient: QYNNWebChromeClient,
    val onCreated: (webView: WebView) -> Unit,
    val onDispose: (webView: WebView) -> Unit,
    val drawOverscrollEffects: State<Boolean>,
)