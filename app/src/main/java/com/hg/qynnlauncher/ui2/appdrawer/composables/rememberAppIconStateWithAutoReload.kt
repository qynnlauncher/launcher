package com.hg.qynnlauncher.ui2.appdrawer.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import com.hg.qynnlauncher.services.apps.InstalledApp
import com.hg.qynnlauncher.services.iconpacks.IconPack

typealias AppIconGetIconFunc = suspend (iconPack: IconPack?, app: InstalledApp) -> ImageBitmap

val emptyGetIconFunc: AppIconGetIconFunc = { _, _  -> throw NotImplementedError() }

@Composable
fun rememberAppIconStateWithAutoReload(
    iconPack: IconPack?,
    app: InstalledApp,
    getIconFunc: AppIconGetIconFunc,
): State<ImageBitmap?>
{
    val bitmapState = remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(app.lastModifiedNanoTime) {
        bitmapState.value = getIconFunc(iconPack, app)
    }

    return bitmapState
}