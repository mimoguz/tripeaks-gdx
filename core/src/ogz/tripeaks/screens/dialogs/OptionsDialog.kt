package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ktx.collections.toGdxArray
import ogz.tripeaks.game.layout.Layout
import ogz.tripeaks.screens.controls.MyTextButton
import ogz.tripeaks.util.GamePreferences
import ogz.tripeaks.util.SkinData

class OptionsDialog(
    skinData: SkinData,
    theme: String,
    gamePreferences: GamePreferences,
    layouts: List<Layout>,
    val res: I18NBundle,
) :
    Dialog("", skinData.skin, theme) {

    var onThemeChanged: ((useDarkTheme: Boolean) -> Unit)? = null
    var onGameLayoutChanged: ((layout: Layout) -> Unit)? = null

    private val useDarkTheme = CheckBox(res.get("darkTheme"), skinData.skin, gamePreferences.themeKey).apply {
        imageCell.padRight(4f)
    }
    private val showAllCards = CheckBox(res.get("showAll"), skinData.skin, gamePreferences.themeKey).apply {
        imageCell.padRight(4f)
    }
    private val startWithEmptyDiscard =
        CheckBox(res.get("emptyDiscard"), skinData.skin, gamePreferences.themeKey).apply {
            imageCell.padRight(4f)
        }
    private val newGameLayout = SelectBox<String>(skinData.skin, gamePreferences.themeKey).also {
        it.setAlignment(Align.center)
    }

    init {
        useDarkTheme.isChecked = gamePreferences.useDarkTheme
        showAllCards.isChecked = gamePreferences.showAllCards
        startWithEmptyDiscard.isChecked = gamePreferences.startWithEmptyDiscard

        val items = layouts.map { res.get(it.tag) }.toGdxArray()
        newGameLayout.items = items

        val selectedIndex = layouts.indexOfFirst { it.tag == gamePreferences.layout }
        newGameLayout.selectedIndex = if (selectedIndex >= 0) selectedIndex else 0

        val saveButton = MyTextButton(res.get("save"), skinData, theme).apply {
            setAction {
                if (gamePreferences.useDarkTheme != useDarkTheme.isChecked) {
                    gamePreferences.useDarkTheme = useDarkTheme.isChecked
                    onThemeChanged?.invoke(gamePreferences.useDarkTheme)
                }

                if (selectedIndex < 0 || selectedIndex != newGameLayout.selectedIndex) {
                    gamePreferences.layout = layouts[newGameLayout.selectedIndex].tag
                    onGameLayoutChanged?.invoke(layouts[newGameLayout.selectedIndex])
                }

                gamePreferences.showAllCards = showAllCards.isChecked
                gamePreferences.startWithEmptyDiscard = startWithEmptyDiscard.isChecked

                gamePreferences.save()
                hide()
            }
        }

        val cancelButton = MyTextButton(res.get("cancel"), skinData, theme).apply {
            setAction { hide() }
        }

        pad(16f, 24f, 16f, 24f)
        contentTable.apply {
            defaults()
                .align(Align.left)
                .space(0f, 0f, 2f, 0f)
                .pad(0f)

            pad(0f)

            add(Label(res.get("layout"), skinData.skin, gamePreferences.themeKey)).padLeft(1f)
            row()
            add(newGameLayout).width(220f)
            row()
            add(showAllCards)
            row()
            add(startWithEmptyDiscard)
            row()
            add(useDarkTheme).align(Align.left)
        }
        buttonTable.apply {
            pad(8f, 0f, 0f, 0f)
            defaults().width(110f).align(Align.center).space(2f).pad(0f)
            add(saveButton)
            add(cancelButton)
        }
    }
}