package gaojunran.kbar

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.net.URI
import javax.swing.KeyStroke

sealed class Action(
    var content: String,
    private val contentState: MutableState<String>? = null,
    val typeValue: Int,
    val typeName: String
) {

    companion object {
        fun fromConfig(type: String, content: String): Action {
            return when (type) {
                "browse" -> BrowseUrl(content)
                "command" -> ExecuteCommand(content)
                "file" -> OpenFile(content)
                "folder" -> OpenFolder(content)
                "clipboard" -> PutToClipboard(content)
                "python" -> RunPythonScript(content)
                "hotkey" -> SendHotkey(content)
                "debug" -> Debug(content)
                else -> Lambda {
                    content.displayInDialog("Unknown Action Type $type")
                }
            }
        }

        fun fromTable(type: Int, content: String): Action {
            return when (type) {
                1 -> BrowseUrl(content)
                2 -> ExecuteCommand(content)
                3 -> OpenFile(content)
                4 -> OpenFolder(content)
                5 -> PutToClipboard(content)
                6 -> RunPythonScript(content)
//                7 -> SendHotkey(content)
                -1 -> Debug(content)
                else -> Lambda {
                    content.displayInDialog("Unknown Action Type $type")
                }
            }
        }


    }

    abstract fun actionInvoke()
    fun invoke() {
//        content = contentState?.value?.let {
//            content.replace("{}", it)
//        } ?: content
        actionInvoke()
    }

    fun toDynamicAction(replacer: String): Action {
        this.content = this.content.replace("{}", replacer)
        return this
    }

    class BrowseUrl(url: String, urlState: MutableState<String>? = null) : Action(url, urlState, 1, "browse") {
        companion object {
            val desktop: Desktop = Desktop.getDesktop()
        }

        override fun actionInvoke() {
            desktop.browse(URI(content))
        }

    }

    class ExecuteCommand(command: String, commandState: MutableState<String>? = null,
                         private val isDisplayDialog: Boolean = true) :
        Action(command, commandState, 2, "command") {
        override fun actionInvoke() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Runtime.getRuntime().exec(content.split(" ").toTypedArray<String>()).apply {
                        val output = inputStream.bufferedReader().readText() + "\n" + errorStream.bufferedReader().readText()
                        waitFor()
                        if (isDisplayDialog) output.displayInDialog(isMultipleLine = true)
                    }
                } catch (e: Exception) {
                    "Error executing command: ${e.message}".displayInDialog(isMultipleLine = true)
                }
            }
        }
    }

    class OpenFile(path: String, pathState: MutableState<String>? = null) : Action(path, pathState, 3, "file") {
        override fun actionInvoke() {
            TODO("Not yet implemented")
        }

    }

    class OpenFolder(path: String, pathState: MutableState<String>? = null) : Action(path, pathState, 4, "folder") {
        override fun actionInvoke() {
            TODO("Not yet implemented")
        }

    }

    class PutToClipboard(text: String, textState: MutableState<String>? = null) :
        Action(text, textState, 5, "clipboard") {
        companion object {
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        }

        override fun actionInvoke() {
            val selection = StringSelection(content)
            clipboard.setContents(selection, null)
        }
    }


    /**
     *
     */
    class RunPythonScript(path: String, pathState: MutableState<String>? = null) :
        Action(path, pathState, 6, "python") {
        override fun actionInvoke() {
            ExecuteCommand("python3 $content").actionInvoke()
        }
    }



    class SendHotkey(
        hotkey: String,
        hotkeyState: MutableState<String>? = null,
        private val isVisible: MutableState<Boolean>? = null  // only pass when you need to hide the window
    ) : Action(hotkey, hotkeyState, 7, "hotkey") {

        companion object {
            val robot = Robot()
        }

        override fun actionInvoke() {

            isVisible?.let { it.value = false }

            val hotkey = KeyStroke.getKeyStroke(content)
            val keyCode = hotkey.keyCode
            val modifiers = hotkey.modifiers

            when {
                modifiers and java.awt.event.InputEvent.CTRL_DOWN_MASK != 0 -> {
                    robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL)
                    robot.keyPress(keyCode)
                    robot.keyRelease(keyCode)
                    robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL)
                }

                modifiers and java.awt.event.InputEvent.SHIFT_DOWN_MASK != 0 -> {
                    robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT)
                    robot.keyPress(keyCode)
                    robot.keyRelease(keyCode)
                    robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT)
                }

                modifiers and java.awt.event.InputEvent.ALT_DOWN_MASK != 0 -> {
                    robot.keyPress(java.awt.event.KeyEvent.VK_ALT)
                    robot.keyPress(keyCode)
                    robot.keyRelease(keyCode)
                    robot.keyRelease(java.awt.event.KeyEvent.VK_ALT)
                }

                else -> {
                    robot.keyPress(keyCode)
                    robot.keyRelease(keyCode)
                }
            }
        }
    }

    class Debug(message: String, messageState: MutableState<String>? = null) :
        Action(message, messageState, -1, "debug") {
        override fun actionInvoke() {
            content.displayInDialog("Debugging...", isMultipleLine = true)
        }
    }

    class Lambda(val action: () -> Unit) : Action("", null, 0, "lambda") {
        override fun actionInvoke() {
            action()
        }
    }


}