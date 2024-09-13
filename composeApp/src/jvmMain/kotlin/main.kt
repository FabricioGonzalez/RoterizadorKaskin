import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Mode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import br.com.kaskin.roteirizador.App
import br.com.kaskin.roteirizador.di.initKoin
import dev.datlag.kcef.KCEF
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarStyle
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme
import java.awt.Dimension
import java.io.File

fun main() {
    application {
        val scope = rememberCoroutineScope()

        val koin = initKoin {}

        LaunchedEffect(Unit) {
            val localPath = File(System.getenv("APPDATA"), "RoteirizadorKaskin")

            if (!localPath.exists()) {
                localPath.mkdir()
            }

            KCEF.init(builder = {
                installDir(File(localPath, "kcef-bundle"))
                progress {
                    /* onDownloading {
                             downloading = max(it, 0F)
                         }
                         onInitialized {
                             initialized = true
                         }*/
                }
                settings {
                    val cache = File(localPath, "cache")
                    if (!cache.exists()) {
                        cache.mkdir()
                    }
                    cachePath = cache.absolutePath
                }
            }, onError = {
                it?.printStackTrace()
            }, onRestartRequired = {
            })
        }
        DisposableEffect(Unit) {
            onDispose {
                KCEF.disposeBlocking()
            }
        }

        IntUiTheme(
            JewelTheme.darkThemeDefinition(),
            ComponentStyling.decoratedWindow(
                titleBarStyle = TitleBarStyle.dark()
            ),
        ) {
            DecoratedWindow(
                onCloseRequest = {
                    exitApplication()
                },
                title = "Roterizador Kaskin",
                /*icon = painterResource(Res.drawable.ic_launcher_icon),*/
                style = DecoratedWindowStyle.dark()
            ) {

                window.minimumSize = Dimension(400, 450)

                var theme by remember { mutableStateOf(IntUiThemes.Dark) }
                val isDark by remember { derivedStateOf { theme == IntUiThemes.Dark } }

                TitleBarView(
                    theme = theme,
                    appBar = {

                    },
                    changeTheme = {
                        theme = when (theme) {
                            IntUiThemes.Light -> IntUiThemes.Dark
                            IntUiThemes.Dark, IntUiThemes.System -> IntUiThemes.Light
                        }
                    }
                )
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun DecoratedWindowScope.TitleBarView(
    theme: IntUiThemes,
    appBar: @Composable RowScope.() -> Unit,
    changeTheme: () -> Unit
) {
    TitleBar(Modifier.newFullscreenControls()) {
        Row(Modifier.fillMaxWidth()) {
            Text(title)
            appBar()

            Row(Modifier.align(Alignment.End)) {
                Tooltip({
                    when (theme) {
                        IntUiThemes.Light -> Text("Switch to light theme with light header")
                        IntUiThemes.Dark, IntUiThemes.System -> Text("Switch to light theme")
                    }
                }) {
                    IconButton(onClick = { changeTheme() }, Modifier.size(40.dp).padding(5.dp)) {
                        when (theme) {
                            IntUiThemes.Light -> Icon(
                                imageVector = Icons.Rounded.LightMode,
                                tint = Color.White,
                                contentDescription = "Themes",
                            )

                            IntUiThemes.Dark -> Icon(
                                imageVector = Icons.Outlined.ModeNight,
                                tint = Color.White,
                                contentDescription = "Themes",
                            )

                            IntUiThemes.System -> Icon(
                                imageVector = Icons.Rounded.Mode,
                                tint = Color.White,
                                contentDescription = "Themes",
                            )
                        }
                    }
                }
            }
        }
    }
}


enum class IntUiThemes {
    Light, Dark, System;

    fun isDark() = (if (this == System) fromSystemTheme(currentSystemTheme) else this) == Dark

    companion object {

        fun fromSystemTheme(systemTheme: SystemTheme) =
            if (systemTheme == SystemTheme.LIGHT) Light else Dark
    }
}