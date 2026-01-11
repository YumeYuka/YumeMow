package plus.yumeyuka.yumemow

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Text
import top.yukonga.miuix.kmp.basic.Card
import org.jetbrains.skia.Image as SkiaImage

fun loadIcoImage(path: String): ImageBitmap? {
    return try {
        val file = java.io.File(path)
        if (!file.exists()) return null
        val bytes = file.readBytes()
        SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
    } catch (_: Exception) {
        null
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IcoCard(
    icoData: IcoData,
    isDragOver: Boolean,
    size: Dp = 96.dp,
    onDragEnter: () -> Unit,
    onDragExit: () -> Unit,
    onClick: () -> Unit
) {
    val icoImage = remember(icoData.path) { loadIcoImage(icoData.path) }

    val borderColor = if (isDragOver) Color(0xFF4A9EFF) else Color.Transparent

    Card(
        modifier = Modifier
            .size(size)
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.awtTransferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)
                },
                target = object : DragAndDropTarget {
                    override fun onEntered(event: DragAndDropEvent) {
                        onDragEnter()
                    }

                    override fun onMoved(event: DragAndDropEvent) {
                        onDragEnter()
                    }

                    override fun onExited(event: DragAndDropEvent) {
                        onDragExit()
                    }

                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        return false
                    }
                }
            )
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        onClick = { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (icoImage != null) {
                    Image(
                        bitmap = icoImage,
                        contentDescription = icoData.name,
                        modifier = Modifier.size(size / 2)
                    )
                } else {
                    Text("?", color = Color.Gray)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(icoData.name, maxLines = 1)
        }
    }
}
