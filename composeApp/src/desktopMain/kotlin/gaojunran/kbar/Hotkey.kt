package gaojunran.kbar

import com.tulskiy.keymaster.common.Provider
import kotlinx.coroutines.delay
import java.awt.Robot
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

val provider: Provider = Provider.getCurrentProvider(true)
val robot = Robot()


fun registerKeys(configs: List<HotkeyConfig>) {
    configs.forEach {
        provider.register(KeyStroke.getKeyStroke(it.key)) { _ ->
            Action.fromConfig(it.type, it.content).actionInvoke()
        }
    }
}

fun registerKeyLambda(key: String, action: () -> Unit) {
    provider.register(KeyStroke.getKeyStroke(key)) { action() }
}

fun sendFocusKey() {
    // 按下 Ctrl 键
    robot.keyPress(KeyEvent.VK_CONTROL);
    // 按下 Shift 键
    robot.keyPress(KeyEvent.VK_SHIFT);
    // 按下 Tab 键
    robot.keyPress(KeyEvent.VK_TAB);
    Thread.sleep(200)
    // 释放 Tab 键
    robot.keyRelease(KeyEvent.VK_TAB);
    // 释放 Shift 键
    robot.keyRelease(KeyEvent.VK_SHIFT);
    // 释放 Ctrl 键
    robot.keyRelease(KeyEvent.VK_CONTROL);
}