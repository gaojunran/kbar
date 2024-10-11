package gaojunran.kbar

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import gaojunran.kbar.States.Companion.isVisible
import kotlinx.coroutines.delay

fun main() = application {
    val focusRequester = remember { FocusRequester() }

    Window(
        onCloseRequest = { isVisible = false },
        visible = isVisible,
        undecorated = true,
        transparent = true,
        title = "kbar",
        state = WindowState(position = WindowPosition.Aligned(Alignment.Center)),
        onKeyEvent = {
            when {
                it.type == KeyEventType.KeyDown && it.key == Key.Escape -> {
                    isVisible = false
                    return@Window true
                }
                it.type == KeyEventType.KeyDown && it.key == Key.Tab && it.isCtrlPressed && it.isShiftPressed -> {
                    focusRequester.requestFocus()
                    return@Window true
                }
                else -> {
                    return@Window true
                }
            }
        },
    ) {

        App(focusRequester)
    }
}

