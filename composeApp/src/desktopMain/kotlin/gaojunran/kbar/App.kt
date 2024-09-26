package gaojunran.kbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    var matchResult = listOf(
        mapOf("name" to "ls", "type" to "shell", "content" to "ls -l"),
        mapOf("name" to "pwd", "type" to "shell", "content" to "pwd"),
    )

    MaterialTheme {
        val text = remember { mutableStateOf("") }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            MainSearchBar(text)
            matchResult.forEach {
                MainSearchResultItem(it["name"]!!, it["content"]!!) { println(execuateCommand(it["content"]!!)) }
            }

        }

    }
}

@Composable
fun MainSearchBar(text: MutableState<String>) {
    OutlinedTextField(
        value = text.value,
        onValueChange = {
            text.value = it
            println("text: $text")
        },
        modifier = Modifier.padding(8.dp).height(100.dp).fillMaxWidth().background(Color.White),
        textStyle = MaterialTheme.typography.h3,
        singleLine = true,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MainSearchResultItem(title: String, description: String, onClick: () -> Unit) {
    var hoverActive by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(if (hoverActive) MaterialTheme.colors.primary else Color.White)
    val txtColor by animateColorAsState(if (hoverActive) Color.White else MaterialTheme.colors.primary)
    Card(modifier = Modifier.padding(0.dp).fillMaxWidth().padding(8.dp)
        .onClick { onClick() }
        .onPointerEvent(PointerEventType.Enter) { hoverActive = true }
        .onPointerEvent(PointerEventType.Exit) { hoverActive = false },
        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
        backgroundColor = bgColor
    ) {
        Text(
            title, style = MaterialTheme.typography.h3,
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