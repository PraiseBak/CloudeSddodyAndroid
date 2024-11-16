package com.example.test.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.study.sddodyandroid.ui.component.PostSurface
import com.study.sddodyandroid.ui.theme.SddodyTheme

@Composable

fun JetsnackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ButtonShape,
    border: BorderStroke? = null,
    backgroundGradient: List<Color> = SddodyTheme.colors.interactivePrimary,
    disabledBackgroundGradient: List<Color> = SddodyTheme.colors.interactiveSecondary,
    contentColor: Color = SddodyTheme.colors.textInteractive,
    disabledContentColor: Color = SddodyTheme.colors.textHelp,
    contentPadding: PaddingValues = androidx.compose.material3.ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    PostSurface(
        shape = shape,
        color = Color.Transparent,
        contentColor = if (enabled) contentColor else disabledContentColor,
        border = border,
        modifier = modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = if (enabled) backgroundGradient else disabledBackgroundGradient
                )
            )
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.bodyMedium
        ) {
            Row(
                Modifier
                    .defaultMinSize(
                        minWidth = androidx.compose.material3.ButtonDefaults.MinWidth,
                        minHeight = androidx.compose.material3.ButtonDefaults.MinHeight
                    )
                    .indication(interactionSource, rememberRipple())
                    .padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

private val ButtonShape = RoundedCornerShape(percent = 50)

@Preview("default", "round",apiLevel = 33)
@Preview("dark theme", "round", uiMode = Configuration.UI_MODE_NIGHT_YES,apiLevel = 33)
@Preview("large font", "round", fontScale = 2f,apiLevel = 33)
@Composable
private fun ButtonPreview() {
   SddodyTheme {
        JetsnackButton(onClick = {}) {
            androidx.compose.material3.Text(text = "Demo")
        }
    }
}

@Preview("default", "rectangle")
@Preview("dark theme", "rectangle", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", "rectangle", fontScale = 2f)
@Composable
private fun RectangleButtonPreview() {
    SddodyTheme {
        JetsnackButton(
            onClick = {}, shape = RectangleShape
        ) {
            androidx.compose.material3.Text(text = "Demo")
        }
    }
}
