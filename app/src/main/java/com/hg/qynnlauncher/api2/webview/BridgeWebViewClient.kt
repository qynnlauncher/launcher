package com.hg.qynnlauncher.api2.webview

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.hg.qynnlauncher.api2.server.QYNNServer
import com.hg.qynnlauncher.webview.AccompanistWebViewClient
import kotlinx.coroutines.runBlocking

private const val TAG = "QYNNWebViewClient"

class QYNNWebViewClient(
    private val _qynnServer: QYNNServer,
) : AccompanistWebViewClient()
{
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse?
    {
        return request?.let {
            runBlocking { _qynnServer.handle(request) }
        }
    }

    override fun onPageFinished(view: WebView, url: String?)
    {
        Log.d(TAG, "onPageFinished: $url")
        super.onPageFinished(view, url)
    }
}