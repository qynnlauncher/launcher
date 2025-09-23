package com.hg.qynnlauncher.api2.server

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse

interface IQYNNServerEndpoint
{
    suspend fun handle(req: WebResourceRequest): WebResourceResponse
}
