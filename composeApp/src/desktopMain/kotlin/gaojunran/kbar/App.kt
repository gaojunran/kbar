package gaojunran.kbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState

import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    val matchResult = listOf(
        mapOf("name" to "ls", "type" to "shell", "content" to "ls -l"),
        mapOf("name" to "pwd", "type" to "shell", "content" to "pwd"),
    )
    val cursor = mutableStateOf(remember { 0 })

    MaterialTheme {
        val text = remember { mutableStateOf("") }
        Box(
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                .fillMaxSize().background(MyStyles.surColor)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        when (it.key) {
                            Key.DirectionDown -> {
                                cursor.value++
                                return@onPreviewKeyEvent true
                            }
                            Key.DirectionUp -> {
                                cursor.value--
                                return@onPreviewKeyEvent true
                            }
                            Key.Enter -> {
                                println("Enter")
                                return@onPreviewKeyEvent true
                            }
                            else -> return@onPreviewKeyEvent false
                        }
                    } else {
                        return@onPreviewKeyEvent false
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                MainSearchBar(text, cursor)
                Spacer(modifier = Modifier.height(32.dp))
                matchResult.forEachIndexed { index, item ->
                    MainSearchResultItem(item["name"] ?: "", item["content"] ?: "", cursor, index) {
                        println(execuateCommand(item["content"] ?: ""))
                    }
                }
            }
        }

    }
}


@Composable
fun MainSearchBar(text: MutableState<String>, cursor: MutableState<Int>) {
    OutlinedTextField(
        value = text.value,
        onValueChange = {
            text.value = it
            println("text: $text")
            cursor.value = 0
        },
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .height(80.dp)
            .fillMaxWidth()
            .background(MyStyles.textFieldColor)
            .border(2.dp, MyStyles.surColor),
        textStyle = MaterialTheme.typography.h4.copy(color = Color.White),
        singleLine = true,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MainSearchResultItem(
    title: String,
    description: String,
    cursor: MutableState<Int>,
    index: Int,
    onClick: () -> Unit
) {
//    var hoverActive by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(if (cursor.value == index) MaterialTheme.colors.primary else MyStyles.surColor)
    val txtColor by animateColorAsState(if (cursor.value == index) Color.White else Color.White)
    Card(modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .fillMaxWidth()
        .onClick { onClick() }
        .onPointerEvent(PointerEventType.Enter) { cursor.value = index }
        .onPointerEvent(PointerEventType.Exit) {  },
        backgroundColor = bgColor
    ) {
        Text(
            title, style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(16.dp),
            color = txtColor
        )
    }
}

fun execuateCommand(command: String): String {
    try {
        val process = ProcessBuilder(*command.split(" ").toTypedArray())
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        return output
    } catch (e: Exception) {
        return "Error executing command: ${e.message}"
    }
}