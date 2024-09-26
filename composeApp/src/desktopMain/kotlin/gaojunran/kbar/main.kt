package gaojunran.kbar

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        undecorated = true,
        transparent = true,
        state = WindowState(position = WindowPosition.Aligned(Alignment.Center)),

    ) {
        App()
    }
}