package ogz.tripeaks.data

import com.badlogic.gdx.Gdx
import ogz.tripeaks.Const

class GamePreferences(var useDarkTheme: Boolean = false, var showAllCards: Boolean = false) {
    fun load() {
        val preferences = Gdx.app.getPreferences(Const.PREFERENCES_NAME)
        useDarkTheme = preferences.getBoolean(Const.PREFERENCES_DARK_THEME, false)
        showAllCards = preferences.getBoolean(Const.PREFERENCES_SHOW_ALL, false)
    }

    fun save() {
        val preferences = Gdx.app.getPreferences(Const.PREFERENCES_NAME)
        preferences.putBoolean(Const.PREFERENCES_DARK_THEME, useDarkTheme)
        preferences.putBoolean(Const.PREFERENCES_SHOW_ALL, showAllCards)
        preferences.flush()
    }
}
