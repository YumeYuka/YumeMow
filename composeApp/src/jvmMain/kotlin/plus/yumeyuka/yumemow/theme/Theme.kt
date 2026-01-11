package plus.yumeyuka.yumemow.theme

import androidx.compose.runtime.Composable
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val textStyle = JewelTheme.createDefaultTextStyle()
    val themeDefinition = JewelTheme.lightThemeDefinition(
        defaultTextStyle = textStyle
    )

    IntUiTheme(
        theme = themeDefinition,
        styling = ComponentStyling.default().decoratedWindow(
            titleBarStyle = customWhiteTitleBarStyle()
        ),
        swingCompatMode = false,
    ) {
        content()
    }
}
