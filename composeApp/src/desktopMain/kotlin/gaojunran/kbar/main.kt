package gaojunran.kbar

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay

fun main() = application {
    var isVisible  by  remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    Window(
        onCloseRequest = { isVisible = false },
        visible = isVisible,
        undecorated = true,
        transparent = true,
        state = WindowState(position = WindowPosition.Aligned(Alignment.Center)),
        onKeyEvent = {
            when {
                it.type == KeyEventType.KeyDown && it.key == Key.Escape -> {
                    isVisible = false
                    return@Window true
                }
                else -> {
                    return@Window true
                }
            }
        },
    ) {

        App(focusRequester)

        LaunchedEffect(Unit){
            registerKeyLambda("alt SPACE") {
                isVisible = !isVisible
                focusRequester.requestFocus()
            }
        }
    }
}