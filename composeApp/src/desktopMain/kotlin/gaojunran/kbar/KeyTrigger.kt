package gaojunran.kbar

import com.tulskiy.keymaster.common.HotKeyListener
import com.tulskiy.keymaster.common.Provider
import javax.swing.JOptionPane
import javax.swing.KeyStroke

val provider = Provider.getCurrentProvider(true)

val listener: HotKeyListener = HotKeyListener {
    JOptionPane.showMessageDialog(null, "Hello World!")
}

fun registerKey(keyStrokeString: String) {
    provider.register(KeyStroke.getKeyStroke("ctrl alt shift F"), listener)
}

fun main() {
    registerKey("ctrl alt shift F")
}