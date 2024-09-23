import androidx.compose.ui.window.ComposeUIViewController
import br.com.kaskin.roteirizador.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App(isDark) }
