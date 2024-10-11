package gaojunran.kbar

import gaojunran.kbar.States.Companion.isVisible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import javax.swing.KeyStroke

sealed class Action(
    var content: String,
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
//                    content.displayInDialog("Unknown Action Type $type")
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
                7 -> SendHotkey(content)
                -1 -> Debug(content)
                else -> Lambda {
//                    content.displayInDialog("Unknown Action Type $type")
                }
            }
        }


    }

    abstract suspend fun actionInvoke()
    suspend fun invoke() {
//        content = contentState?.value?.let {
//            content.replace("{}", it)
//        } ?: content
        actionInvoke()
    }

    fun toDynamicAction(replacer: String): Action {
        this.content = this.content.replace("{}", replacer)
        return this
    }

    class BrowseUrl(url: String) : Action(url, 1, "browse") {
        companion object {
            val desktop: Desktop = Desktop.getDesktop()
        }

        override suspend fun actionInvoke() {
            desktop.browse(URI(content))
        }

    }

    class ExecuteCommand(command: String) :
        Action(command, 2, "command") {
        override suspend fun actionInvoke() {
            executeCommandOutput(content).displayInDialog()
        }
    }

    class OpenFile(path: String) : Action(path, 3, "file") {
        override suspend fun actionInvoke() {
            TODO("Not yet implemented")
        }

    }

    class OpenFolder(path: String) : Action(path, 4, "folder") {
        override suspend fun actionInvoke() {
            TODO("Not yet implemented")
        }

    }

    class PutToClipboard(text: String) :
        Action(text, 5, "clipboard") {
        companion object {
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        }

        override suspend fun actionInvoke() {
            val selection = StringSelection(content)
            clipboard.setContents(selection, null)
        }
    }


    /**
     *
     */
    class RunPythonScript(path: String) :
        Action(path, 6, "python") {
        override suspend fun actionInvoke() {
            ExecuteCommand("python3 $content").actionInvoke()
        }
    }


    class SendHotkey(
        hotkey: String,
    ) : Action(hotkey, 7, "hotkey") {

        companion object {
            val robot = Robot()
        }

        override suspend fun actionInvoke() {

            isVisible = false

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

    class Debug(message: String) :
        Action(message, -1, "debug") {
        override suspend fun actionInvoke() {
            content.displayInDialog()
        }
    }

    class Lambda(val action: () -> Unit) : Action("", 0, "lambda") {
        override suspend fun actionInvoke() {
            action()
        }
    }
}


fun String.replaceANSI(): String {
    return this.replace("\u001B\\[[;\\d]*m".toRegex(), "")
}

fun executeCommand(command: String): Process {
    return ProcessBuilder(*command.split(" ").toTypedArray()).redirectErrorStream(true).start()
}

fun executeCommandStream(command: String) {
}

suspend fun executeCommandOutput(command: String, maxLines: Int = 100): String = coroutineScope {
    try {
        val process: Process = executeCommand(command)
        val output = StringBuilder()
        val error = StringBuilder()

        // Read output stream
        withContext(Dispatchers.IO) {
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String? = reader.readLine()
                var counter = 0
                while (line != null && counter < maxLines) {
                    output.append(line).append("\n")
                    line = reader.readLine()
                    counter++
                }
            }
        }

        // Read error stream
        withContext(Dispatchers.IO) {
            BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    error.append(line).append("\n")
                    line = reader.readLine()
                }
            }
        }
        process.waitFor()
        (output.toString() + "\n" + error.toString()).replaceANSI()
    } catch (e: Exception) {
        "Error executing command: ${e.message}".replaceANSI()
    }
}