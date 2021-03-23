package ogz.tripeaks.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import ogz.tripeaks.Const

class GamePreferences(var useDarkTheme: Boolean = false, var showAllCards: Boolean = false) {
    val themeKey: String
        get() = if (useDarkTheme) "dark" else "light"

    val backgroundColor: Color
        get() = if (useDarkTheme) Const.DARK_BACKGROUND else Const.LIGHT_BACKGROUND

    fun load(): GamePreferences {
        val preferences = Gdx.app.getPreferences(Const.PREFERENCES_NAME)
        useDarkTheme = preferences.getBoolean(Const.PREFERENCES_DARK_THEME, false)
        showAllCards = preferences.getBoolean(Const.PREFERENCES_SHOW_ALL, false)
        return this
    }

    fun save(): GamePreferences {
        val preferences = Gdx.app.getPreferences(Const.PREFERENCES_NAME)
        preferences.putBoolean(Const.PREFERENCES_DARK_THEME, useDarkTheme)
        preferences.putBoolean(Const.PREFERENCES_SHOW_ALL, showAllCards)
        preferences.flush()
        return this
    }
}
