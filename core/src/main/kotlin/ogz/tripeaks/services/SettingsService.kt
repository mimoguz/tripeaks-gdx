package ogz.tripeaks.services

import com.badlogic.gdx.assets.AssetManager
import ktx.inject.Context
import ogz.tripeaks.assets.FontAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.graphics.AnimationSet
import ogz.tripeaks.graphics.Animations
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.Settings
import ogz.tripeaks.models.create
import ogz.tripeaks.models.get
import ogz.tripeaks.screens.Constants
import ogz.tripeaks.screens.GameScreen
import ogz.tripeaks.services.Message.Companion as Msg

class SettingsService {
    private var settings: Settings? = null
    private lateinit var persistence: PersistenceService
    private lateinit var assets: AssetManager
    private lateinit var context: Context
    private lateinit var messageBox: MessageBox
    private lateinit var _animationSet: AnimationSet
    private lateinit var _spriteSet: SpriteSet
    private lateinit var _skin: UiSkin

     val animationSet: AnimationSet
        get() = _animationSet

    val spriteSet: SpriteSet
        get() = _spriteSet

    val skin: UiSkin
        get() = _skin

    fun initialize(context: Context) {
        this.context = context
        persistence = context.inject()
        assets = context.inject()
        messageBox = context.inject()
        settings = persistence.loadSettings() ?: Settings()
        settings!!.apply {
            _skin = createSkin(darkTheme)
            _spriteSet = SpriteSet(darkTheme, backDesign, assets)
            _animationSet = Animations.ALL.find { it.name == animation.tag } ?: Animations.BLINK
            Animations.setTheme(darkTheme)
        }
    }

    fun paused() {
        settings?.let { persistence.saveSettings(it) }
    }

    fun resumed() {
        settings = settings ?: persistence.loadSettings() ?: Settings()
    }

    fun get(): Settings = settings!!.clone()

    fun update(settings: Settings) {
        val newSettings = settings.clone()
        val current = this.settings!!
        val themeChanged = newSettings.darkTheme != current.darkTheme
        val backDesignChanged = newSettings.backDesign !=  current.backDesign
        val spritesChanged = themeChanged || backDesignChanged
        val animationSetChanged = newSettings.animation != current.animation
        val showAllChanged =  current.showAll != newSettings.showAll
        this.settings = newSettings

        if (themeChanged) {
            _skin = createSkin(newSettings.darkTheme)
            Animations.setTheme(newSettings.darkTheme)
            messageBox.send(Msg.SkinChanged(_skin))
        }

        if (spritesChanged) {
            _spriteSet = SpriteSet(newSettings.darkTheme , newSettings.backDesign, assets)
            messageBox.send(Msg.SpriteSetChanged(_spriteSet))
        }

        if (animationSetChanged) {
            _animationSet = newSettings.animation.get()
            messageBox.send(Msg.AnimationSetChanged(_animationSet))
        }

        if (showAllChanged) {
            messageBox.send(Msg.ShowAllChanged(newSettings.showAll))
        }
    }

    fun getNewGame(): GameState = settings!!.let { settings ->
        GameState.startNew(settings.layout.create(), settings.emptyDiscard)
    }

    private fun createSkin(darkTheme: Boolean) =
        if (darkTheme)
            UiSkin(
                assets[TextureAtlasAssets.Ui],
                assets[FontAssets.GamePixels],
                Constants.DARK_UI_TEXT,
                Constants.DARK_UI_EMPHASIS,
                "dark"
            )
        else
            UiSkin(
                assets[TextureAtlasAssets.Ui],
                assets[FontAssets.GamePixels],
                Constants.LIGHT_UI_TEXT,
                Constants.LIGHT_UI_EMPHASIS,
                "light"
            )
}