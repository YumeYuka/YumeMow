package plus.yumeyuka.yumemow

import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.jewel.ui.component.Text
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.SearchBar

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IcoGridView(
    modifier: Modifier = Modifier,
    onFoldersDropped: (folderPaths: List<String>, icoPath: String) -> Unit,
    onScrollDirection: ((Boolean) -> Unit)? = null
) {
    var allIcoList by remember { mutableStateOf<List<IcoData>>(emptyList()) }
    var filteredIcoList by remember { mutableStateOf<List<IcoData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchText by remember { mutableStateOf("") }
    var currentDragOverPath by remember { mutableStateOf<String?>(null) }
    var draggedFolders by remember { mutableStateOf<List<java.io.File>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState) {
        var prevIndex = 0
        var prevOffset = 0
        snapshotFlow {
            Pair(
                gridState.firstVisibleItemIndex,
                gridState.firstVisibleItemScrollOffset
            )
        }.collect { (index, offset) ->
            val isDown = if (index > prevIndex) {
                true
            } else if (index < prevIndex) {
                false
            } else {
                offset > prevOffset
            }
            onScrollDirection?.invoke(isDown)
            prevIndex = index
            prevOffset = offset
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        allIcoList = withContext(Dispatchers.IO) {
            scanIcoFiles(getDefaultIcoPackDir()).sortedBy { it.name.lowercase() }
        }
        filteredIcoList = allIcoList
        isLoading = false
    }

    LaunchedEffect(searchText, allIcoList) {
        filteredIcoList = if (searchText.isBlank()) {
            allIcoList
        } else {
            allIcoList.filter { it.name.contains(searchText, ignoreCase = true) }
        }
    }

    Box(modifier = modifier.fillMaxSize().dragAndDropTarget(shouldStartDragAndDrop = { event ->
        event.awtTransferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)
    }, target = object : DragAndDropTarget {
        override fun onStarted(event: DragAndDropEvent) {
            val files =
                event.awtTransferable.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor) as? List<*>
            draggedFolders = files?.filterIsInstance<java.io.File>()?.filter { it.isDirectory } ?: emptyList()
        }

        override fun onMoved(event: DragAndDropEvent) {
        }

        override fun onEnded(event: DragAndDropEvent) {
            if (currentDragOverPath != null && draggedFolders.isNotEmpty()) {
                val folderPaths = draggedFolders.map { it.absolutePath }
                println("[设置图标] 文件夹=${folderPaths.joinToString()}, 图标=$currentDragOverPath")
                onFoldersDropped(folderPaths, currentDragOverPath!!)
            }
            currentDragOverPath = null
            draggedFolders = emptyList()
        }

        override fun onDrop(event: DragAndDropEvent): Boolean {
            return false
        }
    })) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("正在加载图标包...")
                }
            }

            allIcoList.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "未下载图标包", style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "请在关于页面下载图标包",
                        style = top.yukonga.miuix.kmp.theme.MiuixTheme.textStyles.body2,
                        color = top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchBar(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        inputField = {
                            InputField(
                                query = searchText,
                                onQueryChange = { searchText = it },
                                onSearch = { expanded = false },
                                expanded = expanded,
                                onExpandedChange = { expanded = it })
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it }) {}

                    if (filteredIcoList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("未找到匹配的图标")
                        }
                    } else {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Adaptive(minSize = 110.dp),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredIcoList, key = { it.path }) { icoData ->
                                IcoCard(
                                    icoData = icoData,
                                    isDragOver = currentDragOverPath == icoData.path,
                                    size = 96.dp,
                                    onDragEnter = { currentDragOverPath = icoData.path },
                                    onDragExit = {
                                        if (currentDragOverPath == icoData.path) currentDragOverPath = null
                                    },
                                    onClick = {})
                            }
                        }
                    }
                }
            }
        }
    }
}
