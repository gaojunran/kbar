package gaojunran.kbar

import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.net.URI

sealed class Action(val content: String) {
    companion object {
        fun fromConfig(type: String, content: String): Action {
            when (type) {
                "browse" -> return BrowseUrl(content)
                "command" -> return ExecuteCommand(content)
                "file" -> return OpenFile(content)
                "folder" -> return OpenFolder(content)
                "clipboard" -> return PutToClipboard(content)
                "python" -> return RunPythonScript(content)
            }
            return ExecuteCommand("")
        }
    }

    abstract fun actionInvoke()


    class BrowseUrl(private val url: String) : Action(url) {
        companion object{
            val desktop: Desktop = Desktop.getDesktop()
        }
        override fun actionInvoke() {
            desktop.browse(URI(url))
        }

    }

    class ExecuteCommand(private val command: String) : Action(command) {
        override fun actionInvoke() {
            try {
                val process = ProcessBuilder(*command.split(" ").toTypedArray())
                    .redirectErrorStream(true)
                    .start()
                val output = process.inputStream.bufferedReader().readText()
                process.waitFor()
                output.displayInDialog()
//                return output
            } catch (e: Exception) {
//                return "Error executing command: ${e.message}"
                "Error executing command: ${e.message}".displayInDialog()
            }
        }

    }

    class OpenFile(val path: String) : Action(path) {
        override fun actionInvoke() {
            TODO("Not yet implemented")
        }

    }

    class OpenFolder(val path: String) : Action(path) {
        override fun actionInvoke() {
            TODO("Not yet implemented")
        }

    }

    class PutToClipboard(val text: String) : Action(text) {
        companion object {
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        }

        override fun actionInvoke() {
            val selection = StringSelection(text)
            clipboard.setContents(selection, null)
        }
    }


    /**
     *
     */
    class RunPythonScript(val path: String) : Action(path) {
        override fun actionInvoke() {
            ExecuteCommand("python $path").actionInvoke()
        }
    }

    class Lambda(private val action: () -> Unit) : Action("lambda") {
        override fun actionInvoke() {
            action()
        }
    }


}