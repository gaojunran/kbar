package gaojunran.kbar

import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane

fun String.displayInDialog(title: String? = "Kbar Message"): Unit {
    val jFrame = JFrame()
    jFrame.isVisible = false
    val jDialog = JDialog(jFrame, title)
    jDialog.add(JLabel(this))
}