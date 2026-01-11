package plus.yumeyuka.yumemow.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.intui.standalone.styling.Undecorated
import org.jetbrains.jewel.intui.standalone.styling.defaults
import org.jetbrains.jewel.intui.standalone.styling.light
import org.jetbrains.jewel.intui.standalone.styling.undecorated
import org.jetbrains.jewel.intui.window.DecoratedWindowIconKeys
import org.jetbrains.jewel.ui.component.styling.*
import org.jetbrains.jewel.window.styling.TitleBarColors
import org.jetbrains.jewel.window.styling.TitleBarIcons
import org.jetbrains.jewel.window.styling.TitleBarMetrics
import org.jetbrains.jewel.window.styling.TitleBarStyle

/**
 * 自定义白色标题栏样式
 */
@Composable
fun customWhiteTitleBarStyle(): TitleBarStyle {
    val colors = TitleBarColors(
        background = Color(0xFFF7F7F7), // 纯白背景
        inactiveBackground = Color(0xFFF7F7F7), // 非激活状态浅灰
        content = Color(0xFF000000), // 黑色文字
        border = Color(0xFFF7F7F7), // 浅灰边框
        fullscreenControlButtonsBackground = Color(0xFF7A7B80),
        titlePaneButtonHoveredBackground = Color(0x0A000000),
        titlePaneButtonPressedBackground = Color(0x15000000),
        titlePaneCloseButtonHoveredBackground = Color(0xFFE81123),
        titlePaneCloseButtonPressedBackground = Color(0xFFF1707A),
        iconButtonHoveredBackground = Color(0x0A000000),
        iconButtonPressedBackground = Color(0x15000000),
        dropdownHoveredBackground = Color(0x0A000000),
        dropdownPressedBackground = Color(0x15000000),
    )

    val metrics = TitleBarMetrics(
        height = 40.dp,
        gradientStartX = (-100).dp,
        gradientEndX = 400.dp,
        titlePaneButtonSize = DpSize(40.dp, 40.dp),
    )

    val icons = TitleBarIcons(
        minimizeButton = DecoratedWindowIconKeys.minimize,
        maximizeButton = DecoratedWindowIconKeys.maximize,
        restoreButton = DecoratedWindowIconKeys.restore,
        closeButton = DecoratedWindowIconKeys.close,
    )

    return TitleBarStyle(
        colors = colors,
        metrics = metrics,
        icons = icons,
        dropdownStyle = DropdownStyle.Undecorated.light(
            colors = DropdownColors.Undecorated.light(
                content = colors.content,
                contentFocused = colors.content,
                contentHovered = colors.content,
                contentPressed = colors.content,
                contentDisabled = Color(0xFFAAAAAA),
                backgroundHovered = colors.dropdownHoveredBackground,
                backgroundPressed = colors.dropdownPressedBackground,
            ),
            metrics = DropdownMetrics.undecorated(
                arrowMinSize = DpSize(20.dp, 24.dp),
                minSize = DpSize(60.dp, 30.dp),
                cornerSize = CornerSize(6.dp),
                contentPadding = PaddingValues(start = 10.dp, end = 0.dp, top = 3.dp, bottom = 3.dp),
            ),
            menuStyle = MenuStyle.light(),
        ),
        iconButtonStyle = IconButtonStyle(
            colors = IconButtonColors(
                foregroundSelectedActivated = Color.Unspecified,
                background = Color.Unspecified,
                backgroundDisabled = Color.Unspecified,
                backgroundSelected = Color.Unspecified,
                backgroundSelectedActivated = Color.Unspecified,
                backgroundFocused = Color.Unspecified,
                backgroundPressed = colors.iconButtonHoveredBackground,
                backgroundHovered = colors.iconButtonPressedBackground,
                border = Color.Unspecified,
                borderDisabled = Color.Unspecified,
                borderSelected = Color.Unspecified,
                borderSelectedActivated = Color.Unspecified,
                borderFocused = colors.iconButtonHoveredBackground,
                borderPressed = colors.iconButtonPressedBackground,
                borderHovered = Color.Unspecified,
            ),
            IconButtonMetrics.defaults(borderWidth = 0.dp),
        ),
        paneButtonStyle = IconButtonStyle(
            colors = IconButtonColors(
                foregroundSelectedActivated = Color.Unspecified,
                background = Color.Unspecified,
                backgroundDisabled = Color.Unspecified,
                backgroundSelected = Color.Unspecified,
                backgroundSelectedActivated = Color.Unspecified,
                backgroundFocused = Color.Unspecified,
                backgroundPressed = colors.titlePaneButtonHoveredBackground,
                backgroundHovered = colors.titlePaneButtonPressedBackground,
                border = Color.Unspecified,
                borderDisabled = Color.Unspecified,
                borderSelected = Color.Unspecified,
                borderSelectedActivated = Color.Unspecified,
                borderFocused = colors.titlePaneButtonHoveredBackground,
                borderPressed = colors.titlePaneButtonPressedBackground,
                borderHovered = Color.Unspecified,
            ),
            IconButtonMetrics.defaults(cornerSize = CornerSize(0.dp), borderWidth = 0.dp),
        ),
        paneCloseButtonStyle = IconButtonStyle(
            colors = IconButtonColors(
                foregroundSelectedActivated = Color.Unspecified,
                background = Color.Unspecified,
                backgroundDisabled = Color.Unspecified,
                backgroundSelected = Color.Unspecified,
                backgroundSelectedActivated = Color.Unspecified,
                backgroundFocused = Color.Unspecified,
                backgroundPressed = colors.titlePaneCloseButtonHoveredBackground,
                backgroundHovered = colors.titlePaneCloseButtonPressedBackground,
                border = Color.Unspecified,
                borderDisabled = Color.Unspecified,
                borderSelected = Color.Unspecified,
                borderSelectedActivated = Color.Unspecified,
                borderFocused = colors.titlePaneCloseButtonHoveredBackground,
                borderPressed = colors.titlePaneCloseButtonPressedBackground,
                borderHovered = Color.Unspecified,
            ),
            IconButtonMetrics.defaults(cornerSize = CornerSize(0.dp), borderWidth = 0.dp),
        ),
    )
}
