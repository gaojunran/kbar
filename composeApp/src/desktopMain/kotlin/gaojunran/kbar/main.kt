package gaojunran.kbar

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*

fun main() = application {
    var isVisible by remember { mutableStateOf(true) }

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
        App()
    }
}