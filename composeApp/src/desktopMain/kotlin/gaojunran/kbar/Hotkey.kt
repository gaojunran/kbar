package gaojunran.kbar

import com.tulskiy.keymaster.common.Provider
import javax.swing.KeyStroke

val provider: Provider = Provider.getCurrentProvider(true)

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