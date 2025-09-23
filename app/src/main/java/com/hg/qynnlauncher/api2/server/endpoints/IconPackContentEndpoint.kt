package com.hg.qynnlauncher.api2.server.endpoints

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.hg.qynnlauncher.api2.server.IQYNNServerEndpoint
import com.hg.qynnlauncher.services.iconpackcache.InstalledIconPacksHolder

class IconPackContentEndpoint(
    private val _iconPacks: InstalledIconPacksHolder,
) : IQYNNServerEndpoint
{
    override suspend fun handle(req: WebResourceRequest): WebResourceResponse
    {
        TODO("Not yet implemented")
    }

    companion object
    {
        const val QUERY_ICON_PACK_PACKAGE_NAME = "iconPackPackageName"
        const val QUERY_ITEM_NAME = "itemName"
        const val QUERY_DRAWABLE_NAME = "drawableName"
    }
}