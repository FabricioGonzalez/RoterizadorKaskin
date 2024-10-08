import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import br.com.kaskin.roteirizador.App
import br.com.kaskin.roteirizador.di.initKoin
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.dark
import org.jetbrains.jewel.intui.standalone.styling.light
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.dark
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.light
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.intui.window.styling.lightWithLightHeader
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.component.styling.IconButtonStyle
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarStyle
import org.jetbrains.skia.Color
import org.jetbrains.skiko.SystemTheme
import roterizador_kaskin.composeapp.generated.resources.Res
import roterizador_kaskin.composeapp.generated.resources.ic_launcher_icon
import java.awt.Dimension
import java.io.File
import kotlin.math.max

fun main() {

    val koin = initKoin {}

    application {
        var restartRequired by remember { mutableStateOf(false) }
        var downloading by remember { mutableStateOf(0F) }
        var initialized by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val localPath = File(System.getenv("APPDATA"), "MediaApp")

                KCEF.init(builder = {
                    installDir(File(localPath, "kcef-bundle"))
                    progress {
                        onDownloading {
                            downloading = max(it, 0F)
                        }
                        onInitialized {
                            initialized = true
                        }
                    }
                    settings {
                        cachePath = File(localPath, "cache").absolutePath
                    }
                }, onError = {
                    it?.printStackTrace()
                }, onRestartRequired = {
                    restartRequired = true
                })
            }
        }

        var theme by remember { mutableStateOf(IntUiThemes.Dark) }

        IntUiTheme(
            theme = when {
                theme.isDark() -> JewelTheme.darkThemeDefinition()
                else -> JewelTheme.lightThemeDefinition()
            },
            styling = when (theme) {
                IntUiThemes.Light -> ComponentStyling.light()
                IntUiThemes.Dark -> ComponentStyling.dark()
            },
            swingCompatMode = true,
        ) {
            DecoratedWindow(
                onCloseRequest = {
                    exitApplication()
                },
                title = "Roterizador Kaskin",
                icon = painterResource(Res.drawable.ic_launcher_icon),
                content = {
                    TitleBarView(theme = theme,
                        appBar = {

                        },
                        changeTheme = { newTheme ->
                            theme = when (newTheme) {
                                IntUiThemes.Light -> IntUiThemes.Dark
                                IntUiThemes.Dark -> IntUiThemes.Light
                            }
                        })
                    App(theme.isDark())
                },
                style = when (theme) {
                    IntUiThemes.Light -> DecoratedWindowStyle.light()
                    IntUiThemes.Dark -> DecoratedWindowStyle.dark()
                }
            )
        }
        DisposableEffect(Unit) {
            onDispose {
                KCEF.disposeBlocking()
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App(false)
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DecoratedWindowScope.TitleBarView(
    theme: IntUiThemes,
    appBar: () -> Unit,
    changeTheme: (IntUiThemes) -> Unit
) {
    TitleBar(
        Modifier.newFullscreenControls(), style = when (theme) {
            IntUiThemes.Light -> TitleBarStyle.lightWithLightHeader()
            IntUiThemes.Dark -> TitleBarStyle.dark()
        }
    ) {
        Row(Modifier.align(Alignment.Start)) {
            appBar()
        }

        Text(title)

        Row(Modifier.align(Alignment.End)) {
            Tooltip({
                when (theme) {
                    IntUiThemes.Light -> Text("Switch to dark theme")
                    IntUiThemes.Dark -> Text("Switch to light theme")
                }
            }) {
                IconButton(
                    onClick = {
                        changeTheme(theme)
                    },
                    style = if (theme.isDark()) IconButtonStyle.dark() else IconButtonStyle.light(),
                    modifier = Modifier.size(40.dp).padding(5.dp),
                ) {
                    Icon(
                        tint = if (theme.isDark()) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Black,
                        imageVector = when (theme) {
                            IntUiThemes.Light ->
                                Icons.Rounded.LightMode

                            IntUiThemes.Dark

                                -> Icons.Rounded.DarkMode
                        },
                        contentDescription = when (theme) {
                            IntUiThemes.Light -> "Light"


                            IntUiThemes.Dark -> "Dark"
                        },
                    )
                }
            }
        }
    }
}


enum class IntUiThemes {
    Light, Dark/*, System*/;

    fun isDark() = (/*if (this == System) fromSystemTheme(currentSystemTheme) else*/ this) == Dark

    companion object {

        fun fromSystemTheme(systemTheme: SystemTheme) =
            if (systemTheme == SystemTheme.LIGHT) Light else Dark
    }
}