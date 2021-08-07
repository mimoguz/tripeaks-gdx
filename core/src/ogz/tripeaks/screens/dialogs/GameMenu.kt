package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ogz.tripeaks.Const
import ogz.tripeaks.game.layout.Layout
import ogz.tripeaks.screens.controls.MyMenuItem
import ogz.tripeaks.util.GamePreferences
import ogz.tripeaks.util.SkinData

class GameMenu(
    skinData: SkinData,
    preferences: GamePreferences,
    layouts: List<Layout>,
    res: I18NBundle,
    attached: Actor
) :
    Window("", skinData.skin, preferences.themeKey) {

    var onThemeChanged: ((useDarkTheme: Boolean) -> Unit)? = null
    var onOptionsDialogShown: (() -> Unit)? = null

    val newGameButton = MyMenuItem(res.get("newGameShort"), skinData, preferences.themeKey)
    val exitButton = MyMenuItem(res.get("exit"), skinData, preferences.themeKey)
    val optionsButton = MyMenuItem(res.get("options"), skinData, preferences.themeKey).apply {
        setAction {
            val optionsDialog = OptionsDialog(
                skinData,
                preferences.themeKey,
                preferences,
                layouts,
                res
            )
            optionsDialog.onThemeChanged = { useDarkTheme -> onThemeChanged?.invoke(useDarkTheme) }
            onOptionsDialogShown?.invoke()
            optionsDialog.show(this@GameMenu.stage)
        }
    }

    init {
        isModal = false
        isVisible = false
        val layout = HorizontalGroup().apply {
            defaults()
                .space(0f)
                .align(Align.left)
                .pad(0f)
            add(newGameButton).width(100f).height(Const.BUTTON_HEIGHT)
            row()
            add(optionsButton).width(100f).height(Const.BUTTON_HEIGHT)
            row()
            add(exitButton).width(100f).height(Const.BUTTON_HEIGHT)
            pad(0f)
        }
        add(layout)
        pad(4f, 4f, 6f, 4f)
        width = 108f
        height = 82f
        setPosition(
            attached.x + attached.width,
            attached.y,
            Align.topRight
        )
    }
}