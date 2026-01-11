package plus.yumeyuka.yumemow

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import plus.yumeyuka.yumemow.theme.AppTheme
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Like
import top.yukonga.miuix.kmp.icon.icons.useful.Settings
import top.yukonga.miuix.kmp.theme.MiuixTheme
import yumemow.composeapp.generated.resources.Res
import yumemow.composeapp.generated.resources.YumeLira

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    application {
        AppTheme {
            DecoratedWindow(
                onCloseRequest = { exitApplication() },
                title = "YumeMow",
                icon = painterResource(Res.drawable.YumeLira),
                content = {
                    TitleBar {
                        Row(
                            Modifier.align(Alignment.Start).padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.YumeLira),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("YumeMow")
                        }
                    }
                    App()
                })
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    var currentPage by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    MiuixTheme {
        var navVisible by remember { mutableStateOf(true) }
        val hazeState = remember { HazeState() }

        Scaffold(
            bottomBar = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AnimatedVisibility(
                        visible = navVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(200, easing = LinearEasing)),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(280, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(180, easing = LinearEasing)),
                        label = "BottomBarVisibility"
                    ) {
                        val items = listOf(
                            NavigationItem("首页", MiuixIcons.Useful.Like),
                            NavigationItem("关于", MiuixIcons.Useful.Settings)
                        )

                        NavigationBar(
                            modifier = Modifier.hazeEffect(hazeState),
                            items = items,
                            selected = currentPage,
                            onClick = { currentPage = it }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .haze(state = hazeState)
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                when (currentPage) {
                    0 -> {
                        IcoGridView(
                            modifier = Modifier.fillMaxSize(),
                            onFoldersDropped = { folderPaths, icoPath ->
                                try {
                                    println("[设置图标] 开始")
                                    println("  文件夹数量: ${folderPaths.size}")
                                    println("  图标: $icoPath")

                                    var successCount = 0
                                    var failCount = 0

                                    folderPaths.forEach { folderPath ->
                                        println("  处理: $folderPath")
                                        val result = FolderIconJni.setFolderIcon(folderPath, icoPath)
                                        if (result == FolderIconError.Success) {
                                            println("    ✓ 成功")
                                            successCount++
                                        } else {
                                            println("    ✗ 失败: $result")
                                            failCount++
                                        }
                                    }

                                    message = when {
                                        failCount == 0 -> "✓ 已成功设置 $successCount 个文件夹"
                                        successCount == 0 -> "✗ 所有文件夹设置失败"
                                        else -> "部分成功: 成功 $successCount 个，失败 $failCount 个"
                                    }
                                    isError = failCount > 0

                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(1000)
                                        message = null
                                    }
                                } catch (e: Exception) {
                                    println("[设置图标] ✗ 异常: ${e.message}")
                                    e.printStackTrace()
                                    message = "✗ ${e.message}"
                                    isError = true

                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(1000)
                                        message = null
                                    }
                                }
                            },
                            onScrollDirection = { isDown ->
                                navVisible = !isDown
                            }
                        )
                    }

                    1 -> {
                        AboutPage(
                            modifier = Modifier.fillMaxSize(),
                            onMessage = { msg, error ->
                                message = msg
                                isError = error

                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(1000)
                                    message = null
                                }
                            }
                        )
                    }
                }

                message?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter),
                        insideMargin = PaddingValues(16.dp)
                    ) {
                        Text(
                            text = msg,
                            color = if (isError) MiuixTheme.colorScheme.error else MiuixTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
