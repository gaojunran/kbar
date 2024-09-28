package gaojunran.kbar

import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.window.*

fun main() = application {
    val trayState = rememberTrayState()
    Window(
        onCloseRequest = ::exitApplication,
        undecorated = true,
        transparent = true,
        state = WindowState(position = WindowPosition.Aligned(Alignment.Center)),
        onKeyEvent = {
            when {
                it.type == KeyEventType.KeyDown && it.key == Key.Escape -> {
                    exitApplication()
                    return@Window true
                }
                else -> {
                    return@Window true
                }
            }
        },
    ) {
        App()
    }
}