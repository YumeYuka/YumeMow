package plus.yumeyuka.yumemow

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme
import yumemow.composeapp.generated.resources.Res
import yumemow.composeapp.generated.resources.YumeLira
import java.awt.Desktop
import java.net.URI

@Composable
fun AboutPage(
    modifier: Modifier = Modifier,
    onMessage: (String, Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            painter = painterResource(Res.drawable.YumeLira),
            contentDescription = "App Icon",
            modifier = Modifier.size(120.dp).clip(RoundedCornerShape(24.dp)),
        )


        Text(
            text = "YumeMow", style = MiuixTheme.textStyles.title3
        )


        Text(
            text = "1.0.0 (100)",
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
        )

        Spacer(modifier = Modifier.height(32.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            SuperArrow(
                title = "下载图标包",
                onClick = {
                    scope.launch {
                        try {
                            onMessage("正在下载图标包...", false)
                            withContext(Dispatchers.IO) {
                                downloadAndUnzip(
                                    "https://github.com/icon11-community/Folder-Ico/archive/refs/heads/main.zip",
                                )
                            }
                            if (isActive) {
                                onMessage("图标包下载成功！", false)
                            }
                        } catch (e: Exception) {
                            if (isActive) {
                                onMessage("下载失败：${e.message}", true)
                            }
                        }
                    }
                }
            )

            SuperArrow(
                title = "在 GitHub 查看源码",
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        try {
                            Desktop.getDesktop().browse(URI("https://github.com/YumeYuka/YumeMow"))
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                onMessage("打开浏览器失败", true)
                            }
                        }
                    }
                })

            SuperArrow(
                title = "加入 Telegram 频道",
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        try {
                            Desktop.getDesktop().browse(URI("https://t.me/YumeLira"))
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                onMessage("打开浏览器失败", true)
                            }
                        }
                    }
                })
        }
    }
}
