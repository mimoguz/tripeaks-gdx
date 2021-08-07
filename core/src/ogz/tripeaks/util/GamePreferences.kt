package ogz.tripeaks.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import ogz.tripeaks.Const
import ogz.tripeaks.game.layout.BasicLayout
import ogz.tripeaks.game.layout.DiamondsLayout
import ogz.tripeaks.game.layout.Inverted2ndLayout

class GamePreferences(
    var useDarkTheme: Boolean = false,
    var showAllCards: Boolean = false,
    var startWithEmptyDiscard: Boolean = false,
    var layout: String = BasicLayout.TAG
) {
    val themeKey: String
        get() = if (useDarkTheme) "dark" else "light"

    val backgroundColor: Color
        get() = if (useDarkTheme) Const.DARK_BACKGROUND else Const.LIGHT_BACKGROUND

    fun load(): GamePreferences {
        val preferences = Gdx.app.getPreferences(NAME)
        useDarkTheme = preferences.getBoolean(USE_DARK_THEME, false)
        showAllCards = preferences.getBoolean(SHOW_ALL, false)
        startWithEmptyDiscard = preferences.getBoolean(START_WITH_EMPTY_DISCARD, false)
        layout = preferences.getString(LAYOUT, BasicLayout.TAG)
        return this
    }

    fun save(): GamePreferences {
        val preferences = Gdx.app.getPreferences(NAME)
        preferences.putBoolean(USE_DARK_THEME, useDarkTheme)
        preferences.putBoolean(SHOW_ALL, showAllCards)
        preferences.putBoolean(START_WITH_EMPTY_DISCARD, startWithEmptyDiscard)
        preferences.putString(LAYOUT, layout)
        preferences.flush()
        return this
    }

    companion object {
        const val NAME = "gamePreferences"
        const val SHOW_ALL = "showAllCards"
        const val START_WITH_EMPTY_DISCARD = "startWithEmptyDiscard"
        const val USE_DARK_THEME = "darkTheme"
        const val LAYOUT = "layout"
    }
}