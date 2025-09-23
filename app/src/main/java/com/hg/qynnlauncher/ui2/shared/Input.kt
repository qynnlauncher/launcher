package com.hg.qynnlauncher.ui2.shared

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hg.qynnlauncher.ui2.theme.borders
import com.hg.qynnlauncher.ui2.theme.checkedItemBg
import com.hg.qynnlauncher.ui2.theme.textSec

@Composable
fun CheckboxField(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    description: String = ""
)
{
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        color = if (isChecked)
            MaterialTheme.colors.checkedItemBg
        else
            Color.Transparent
    )
    {
        Row(
            modifier = Modifier
                .clickable { onCheckedChange(!isChecked) }
                .defaultMinSize(minHeight = 48.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(12.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        )
        {
            Checkbox(
                checked = isChecked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.onSurface,
                    uncheckedColor = MaterialTheme.colors.onSurface,
                )
            )
            Column()
            {
                Text(label)
                if (description.isNotEmpty())
                {
                    Text(description, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.textSec)
                }
            }
        }
    }
}

@Composable
fun <TOption> OptionsRow(label: String, options: Map<TOption, String>, selectedOption: TOption, onChange: (TOption) -> Unit)
{
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    )
    {
        Text(label, modifier = Modifier.padding(4.dp, 0.dp))

        Row(
            modifier = Modifier
                .border(MaterialTheme.borders.soft, RoundedCornerShape(9.dp))
                .padding(1.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        )
        {

            for (entry in options)
            {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp),
                    color = if (selectedOption == entry.key)
                        MaterialTheme.colors.checkedItemBg
                    else
                        Color.Transparent,
                    shape = MaterialTheme.shapes.medium,
                )
                {
                    Box(
                        modifier = Modifier
                            .clickable { onChange(entry.key) },
                        contentAlignment = Alignment.Center,
                    )
                    {
                        Text(entry.value)
                    }
                }
            }
        }
    }
}