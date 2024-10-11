package gaojunran.kbar

import com.tulskiy.keymaster.common.Provider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Robot
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

val provider: Provider = Provider.getCurrentProvider(true)
val robot = Robot()


@OptIn(DelicateCoroutinesApi::class)
fun registerKeys(configs: List<HotkeyConfig>) {
    configs.forEach {
        provider.register(KeyStroke.getKeyStroke(it.key)) { _ ->
            GlobalScope.launch {
                Action.fromConfig(it.type, it.content).invoke()
            }
        }
    }
}

fun registerKeyLambda(key: String, action: () -> Unit) {
    provider.register(KeyStroke.getKeyStroke(key)) { action() }
}

