package com.hg.qynnlauncher.ui2.home.qynnmenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hg.qynnlauncher.R
import com.hg.qynnlauncher.ui2.home.qynnmenu.IQYNNMenuElement.Button
import com.hg.qynnlauncher.ui2.home.qynnmenu.IQYNNMenuElement.Divider
import com.hg.qynnlauncher.ui2.shared.ResIcon
import com.hg.qynnlauncher.ui2.theme.QYNNLauncherThemeStateless
import com.hg.qynnlauncher.utils.addAll
import com.hg.qynnlauncher.utils.tryStartAndroidHomeSettingsActivity
import com.hg.qynnlauncher.utils.tryStartQYNNAppDrawerActivity
import com.hg.qynnlauncher.utils.tryStartQYNNSettingsActivity
import com.hg.qynnlauncher.utils.tryStartDevConsoleActivity

@Composable
fun QYNNMenu(
    state: QYNNMenuState,
    actions: QYNNMenuActions,
    modifier: Modifier = Modifier,
)
{
    if (!state.isShown) return

    val entriesToDisplay = mutableListOf<IQYNNMenuElement>()
    val context = LocalContext.current

    if (state.isExpanded)
    {
        entriesToDisplay.addAll(
            Button(R.drawable.ic_refresh, "Refresh WebView", actions.onWebViewRefreshRequest),
            Button(R.drawable.ic_dev_console, "Developer console") { context.tryStartDevConsoleActivity() },
            Divider,
            Button(R.drawable.ic_switch_launchers, "Switch away from QYNN") { context.tryStartAndroidHomeSettingsActivity() },
            Button(R.drawable.ic_settings, "QYNN settings") { context.tryStartQYNNSettingsActivity() },
            Button(R.drawable.ic_hide, "Hide QYNN button", actions.onHideQYNNButtonRequest),
        )
    }

    if (state.isExpanded || state.showAppDrawerButtonWhenCollapsed)
    {
        entriesToDisplay.addAll(
            Button(R.drawable.ic_apps, "Built-in app drawer") { context.tryStartQYNNAppDrawerActivity() },
            Divider,
        )
    }

    entriesToDisplay.addAll(
        Button(
            R.drawable.ic_qynn,
            if (state.isExpanded) "Collapse this menu" else "Expand the QYNN Menu"
        )
        {
            actions.onRequestIsExpandedChange(!state.isExpanded)
        },
    )

    Row(
        modifier = modifier
            .wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    )
    {
        // label column
        if (state.isExpanded)
        {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            )
            {
                for (entry in entriesToDisplay)
                {
                    if (entry is Button)
                    {
                        QYNNMenuButtonLabel(entry)
                    }
                    else
                    {
                        assert(entry is Divider)
                        Spacer(Modifier.size(1.dp))
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .wrapContentSize(),
            color = MaterialTheme.colors.surface,
            shape = MaterialTheme.shapes.large,
            elevation = 4.dp,
        )
        {
            // button column
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.End,
            )
            {
                for (entry in entriesToDisplay)
                {
                    if (entry is Button)
                    {
                        QYNNMenuButton(entry)
                    }
                    else
                    {
                        assert(entry is Divider)
                        QYNNMenuDivider()
                    }
                }
            }

        }
    }
}

@Composable
fun QYNNMenuWithEmptyActions(state: QYNNMenuState) = QYNNMenu(
    state = state,
    actions = QYNNMenuActions.empty(),
)

@Composable
fun QYNNMenuDivider()
{
    androidx.compose.material.Divider()
}

@Composable
fun QYNNMenuButtonLabel(button: Button)
{
    Box(
        modifier = Modifier
            .height(56.dp)
            .wrapContentWidth(),
        contentAlignment = Alignment.CenterEnd,
    )
    {
        Surface(
            shape = MaterialTheme.shapes.small,
            elevation = 4.dp
        )
        {
            Text(
                text = button.text,
                modifier = Modifier
                    .padding(16.dp, 8.dp),
            )
        }
    }
}

@Composable
fun QYNNMenuButton(button: Button)
{
    Box(
        modifier = Modifier
            .clickable(onClick = button.action)
            .size(56.dp),
        contentAlignment = Alignment.Center,
    )
    {
        ResIcon(iconResId = button.iconResId)
    }
}

@Preview
@Composable
fun QYNNMenuPreviewFullyCollapsed()
{
    QYNNLauncherThemeStateless(useDarkTheme = false) {
        QYNNMenuWithEmptyActions(
            QYNNMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = false
            )
        )
    }
}

@Preview
@Composable
fun QYNNMenuPreviewCollapsedWithAppDrawerButton()
{
    QYNNLauncherThemeStateless(useDarkTheme = false) {
        QYNNMenuWithEmptyActions(
            QYNNMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = true
            )
        )
    }
}

@Preview
@Composable
fun QYNNMenuPreviewExpandedLight()
{
    QYNNLauncherThemeStateless(useDarkTheme = false) {
        QYNNMenuWithEmptyActions(
            QYNNMenuState(
                isShown = true,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            )
        )
    }
}

@Preview
@Composable
fun QYNNMenuPreviewExpandedDark()
{
    QYNNLauncherThemeStateless(useDarkTheme = true) {
        QYNNMenuWithEmptyActions(
            QYNNMenuState(
                isShown = true,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            )
        )
    }
}