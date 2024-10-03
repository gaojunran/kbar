package gaojunran.kbar

import java.awt.BorderLayout
import javax.swing.*

fun String.displayInDialog(title: String? = "Kbar Message", isMultipleLine: Boolean = false) {
    val jFrame = JFrame().apply {
        isVisible = false
    }
    val jLabel = JLabel(this).apply {
        font = this.font.deriveFont(25f)
        horizontalAlignment = JLabel.CENTER
    }

    val jTextArea = JTextArea(this).apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
        font = this.font.deriveFont(25f)
    }

    val jDialog = JDialog(jFrame, title).apply {
        layout = BorderLayout()
        if (!isMultipleLine) {
            add(jLabel)
            setSize(500, 300) // 设置对话框大小
        } else {
            add(jTextArea)
            setSize(700, 500) // 设置对话框大小
            add(JScrollPane(jTextArea), BorderLayout.CENTER) // 添加滚动面板
        }

        defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE // 设置关闭操作
        setLocationRelativeTo(null) // 居中显示对话框
        isVisible = true // 显示对话框
    }

}

fun main() {
    (1..100).toList().toString().displayInDialog(isMultipleLine = true)
}