package com.hg.qynnlauncher.ui2.dirpicker.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hg.qynnlauncher.R
import com.hg.qynnlauncher.ui2.shared.Btn
import com.hg.qynnlauncher.ui2.shared.ResIcon
import com.hg.qynnlauncher.ui2.theme.textSec

@Composable
fun DirectoryPickerStoragePermissionPrompt(
    supportsScopedStorage: Boolean,
    onRequestGrantPermission: () -> Unit,
    modifier: Modifier = Modifier,
)
{
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.CenterVertically),
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            ResIcon(R.drawable.ic_folder_open)
            Text("Storage permission needed")
            Text(
                "Access to directories and files on your device is needed to load projects and serve their files to the WebView.",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.textSec,
            )
        }

        // scoped storage
        if (supportsScopedStorage)
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            )
            {
                Text("Why not use Scoped Storage?")
                Text(
                    "Storage Access Framework (SAF) does not have feature parity with direct file system access. "
                            + "Examples include inability to watch a directory for changes and inability to access a subdirectory/file by path.",
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.textSec,
                )
            }
        }

        Btn(
            text = "Grant permission",
            outlined = true,
            onClick = onRequestGrantPermission
        )
    }
}