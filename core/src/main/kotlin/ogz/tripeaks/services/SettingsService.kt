package ogz.tripeaks.services

import com.badlogic.gdx.assets.AssetManager
import ktx.inject.Context
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.game.AnimationStrategy
import ogz.tripeaks.game.CardDrawingStrategy
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.Settings
import ogz.tripeaks.models.layout.BasicLayout
import ogz.tripeaks.models.layout.DiamondsLayout
import ogz.tripeaks.models.layout.Inverted2ndLayout
import ogz.tripeaks.models.layout.Layout
import ogz.tripeaks.services.Message.Companion as Msg

class SettingsService {
    private var settings: Settings? = null
    private lateinit var persistence: PersistenceService
    private lateinit var assets: AssetManager
    private lateinit var context: Context
    private lateinit var messageBox: MessageBox

    fun initialize(context: Context) {
        this.context = context
        persistence = context.inject()
        assets = context.inject()
        messageBox = context.inject()
        settings = persistence.loadSettings()?.create(assets) ?: SavedSettings().create(assets)
    }

    fun paused() {
        settings?.let { persistence.saveSettings(it) }
    }

    fun resumed() {
        settings =
            settings ?: persistence.loadSettings()?.create(assets) ?: SavedSettings().create(assets)
    }

    fun get(): Settings = settings!!

    fun getSaved(): SavedSettings = SavedSettings(settings!!)

    fun update(savedSettings: SavedSettings) {
        val current = this.settings!!
        val themeChanged = savedSettings.darkTheme != current.darkTheme
        val backDesignChanged = savedSettings.backDesign != current.backDesign
        val animationsChanged =
            savedSettings.animation != animationToVariant(current.animationStrategy)
        val drawingStrategyChanged =
            savedSettings.drawingStrategy != drawingToVariant(current.drawingStrategy)

        if (themeChanged || backDesignChanged || animationsChanged || drawingStrategyChanged) {
            val newSettings = savedSettings.create(assets)
            settings = newSettings
            if (themeChanged) messageBox.send(Msg.SkinChanged)
            if (themeChanged || backDesignChanged) messageBox.send(Msg.SpriteSetChanged)
            if (animationsChanged) messageBox.send(Msg.AnimationSetChanged)
            if (drawingStrategyChanged) messageBox.send(Msg.ShowAllChanged)
        }
    }

    fun getNewGame(): GameState = settings!!.let { settings ->
        GameState.startNew(settings.layout, settings.emptyDiscard)
    }
}

class SavedSettings(
    var darkTheme: Boolean,
    var backDesign: Int,
    var layout: Layouts,
    var animation: AnimationStrategies,
    var drawingStrategy: DrawingStrategies,
    var emptyDiscard: Boolean,
) {
    constructor() : this(
        darkTheme = false,
        backDesign = 0,
        layout = Layouts.Basic,
        animation = AnimationStrategies.Blink,
        drawingStrategy = DrawingStrategies.BackHidden,
        emptyDiscard = false
    )

    constructor(settings: Settings) : this(
        settings.darkTheme,
        settings.backDesign,
        tagToLayoutVariant(settings.layout.tag),
        animationToVariant(settings.animationStrategy),
        drawingToVariant(settings.drawingStrategy),
        settings.emptyDiscard
    )

    fun create(assets: AssetManager): Settings {
        val cjk = false // TODO
        return Settings(
            backDesign,
            layout.create(),
            animation.create(),
            drawingStrategy.create(),
            SpriteSet(darkTheme, backDesign, assets),
            UiSkin(assets, false, darkTheme),
            emptyDiscard
        )
    }
}

enum class Layouts {
    Basic,
    Diamonds,
    Inverted2nd
}

fun tagToLayoutVariant(tag: String): Layouts = when (tag) {
    BasicLayout.TAG -> Layouts.Basic
    DiamondsLayout.TAG -> Layouts.Diamonds
    else -> Layouts.Inverted2nd
}

fun Layouts.create(): Layout {
    return when (this) {
        Layouts.Basic -> BasicLayout()
        Layouts.Diamonds -> DiamondsLayout()
        Layouts.Inverted2nd -> Inverted2ndLayout()
    }
}

enum class AnimationStrategies {
    Blink,
    Dissolve
}

fun animationToVariant(s: AnimationStrategy): AnimationStrategies =
    if (s is AnimationStrategy.Strategies.Blink) {
        AnimationStrategies.Blink
    } else {
        AnimationStrategies.Dissolve
    }

fun AnimationStrategies.create(): AnimationStrategy {
    return when (this) {
        AnimationStrategies.Blink -> AnimationStrategy.Strategies.Blink()
        AnimationStrategies.Dissolve -> AnimationStrategy.Strategies.Dissolve()
    }
}

enum class DrawingStrategies {
    BackVisible,
    BackHidden
}

fun drawingToVariant(s: CardDrawingStrategy): DrawingStrategies =
    if (s is CardDrawingStrategy.Strategies.BackVisible) {
        DrawingStrategies.BackVisible
    } else {
        DrawingStrategies.BackHidden
    }

fun DrawingStrategies.create(): CardDrawingStrategy {
    return when (this) {
        DrawingStrategies.BackHidden -> CardDrawingStrategy.Strategies.BackHidden
        DrawingStrategies.BackVisible -> CardDrawingStrategy.Strategies.BackVisible
    }
}
