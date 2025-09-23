package com.hg.qynnlauncher.ui2.devconsole.composables

import android.webkit.ConsoleMessage.MessageLevel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.hg.qynnlauncher.R
import com.hg.qynnlauncher.ui2.devconsole.IConsoleMessage
import com.hg.qynnlauncher.ui2.devconsole.TestConsoleMessages
import com.hg.qynnlauncher.ui2.devconsole.getSourceAndLineString
import com.hg.qynnlauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.hg.qynnlauncher.ui2.shared.ResIcon
import com.hg.qynnlauncher.ui2.theme.mono
import com.hg.qynnlauncher.ui2.theme.textSec
import com.hg.qynnlauncher.ui2.theme.warning

@Composable
fun ConsoleMessageListItem(
    msg: IConsoleMessage,
    modifier: Modifier = Modifier,
    click: () -> Unit,
)
{
    Surface(
        color = when (msg.messageLevel)
        {
            MessageLevel.WARNING -> MaterialTheme.colors.warning.copy(alpha = 0.15f)
            MessageLevel.ERROR -> MaterialTheme.colors.error.copy(alpha = 0.15f)
            else -> Color.Transparent
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    )
    {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { click() }
                .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        )
        {
            Text(
                text = msg.getSourceAndLineString(),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.textSec,
            )

            val textColor = when (msg.messageLevel)
            {
                MessageLevel.WARNING -> MaterialTheme.colors.warning
                MessageLevel.ERROR -> MaterialTheme.colors.error
                else -> Color.Unspecified
            }

            val prefixId = "prefix"
            val inlineContent = when (msg.messageLevel)
            {
                MessageLevel.WARNING,
                MessageLevel.ERROR,
                ->
                    mapOf(
                        prefixId to InlineTextContent(
                            Placeholder(
                                MaterialTheme.typography.body1.fontSize * 1.3,
                                MaterialTheme.typography.body1.fontSize,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                            )
                        )
                        {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.CenterStart,
                            )
                            {
                                val icon = when (msg.messageLevel)
                                {
                                    MessageLevel.WARNING -> R.drawable.ic_warning_filled
                                    MessageLevel.ERROR -> R.drawable.ic_error
                                    else -> throw NotImplementedError()
                                }
                                    ResIcon(
                                        icon,
                                        color = textColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                            }
                        }
                    )

                else -> null
            }

            Text(
                text = buildAnnotatedString()
                {
                    if (inlineContent != null)
                    {
                        appendInlineContent(prefixId, "Prefix icon")
                    }

                    append(msg.message)
                },
                color = textColor,
                style = MaterialTheme.typography.mono,
                inlineContent = inlineContent ?: mapOf(),
            )
        }
    }
}


// PREVIEW

@Composable
fun ConsoleMessageListItemPreview(
    level: MessageLevel,
    message: String = TestConsoleMessages.LONG_MESSAGE,
)
{
    PreviewWithSurfaceAndPadding {
        ConsoleMessageListItem(
            msg = TestConsoleMessages.msg(message, level),
            click = { }
        )
    }
}

@Composable
@PreviewLightDark
fun ConsoleMessageListItemPreview_Log()
{
    ConsoleMessageListItemPreview(level = MessageLevel.LOG)
}

@Composable
@PreviewLightDark
fun ConsoleMessageListItemPreview_Warn()
{
    ConsoleMessageListItemPreview(level = MessageLevel.WARNING)
}

@Composable
@PreviewLightDark
fun ConsoleMessageListItemPreview_Error()
{
    ConsoleMessageListItemPreview(level = MessageLevel.ERROR)
}
