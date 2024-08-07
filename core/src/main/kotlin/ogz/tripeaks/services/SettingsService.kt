package ogz.tripeaks.services

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.Disposable
import ktx.inject.Context
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.views.AnimationStrategy
import ogz.tripeaks.views.CardDrawingStrategy
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.Settings
import ogz.tripeaks.models.layout.BasicLayout
import ogz.tripeaks.models.layout.DiamondsLayout
import ogz.tripeaks.models.layout.Inverted2ndLayout
import ogz.tripeaks.models.layout.Layout

class SettingsService : Disposable {

    private var settings: Settings? = null
    private lateinit var persistence: PersistenceService
    private lateinit var assets: AssetManager
    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
        persistence = context.inject()
        assets = context.inject()
        settings = persistence.loadSettings()?.create(assets) ?: SettingsData().create(assets)
    }

    fun paused() {
        // TODO: Saving on update now, not needed.
        // settings?.let { persistence.saveSettings(SettingsData(it)) }
    }

    fun resumed() {
        settings = settings
            ?: persistence.loadSettings()?.create(assets)
                    ?: SettingsData().create(assets)
    }

    fun get(): Settings = settings!!

    fun getData(): SettingsData = SettingsData(settings!!)

    fun update(settingsData: SettingsData) {
        val current = this.settings!!
        val settingsChanged =
            settingsData.darkTheme != current.darkTheme
                    || settingsData.backDesign != current.backDesign
                    || settingsData.animation != current.animationStrategy.toVariant()
                    || settingsData.drawingStrategy != current.drawingStrategy.toVariant()
                    || settingsData.layout.tag != current.layout.tag
                    || settingsData.emptyDiscard != current.emptyDiscard

        if (settingsChanged) {
            val newSettings = settingsData.create(assets)
            settings?.animationStrategy?.also { if (it is Disposable) it.dispose() }
            settings = newSettings
            persistence.saveSettings(settingsData)
        }
    }

    fun getNewGame(): GameState = settings!!.let { settings ->
        GameState.startNew(settings.layout, settings.emptyDiscard)
    }

    override fun dispose() {
        settings?.animationStrategy?.also { if (it is Disposable) it.dispose() }
    }

}

class SettingsData(
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
        layout = Layouts.Diamonds,
        animation = AnimationStrategies.Dissolve,
        drawingStrategy = DrawingStrategies.BackVisible,
        emptyDiscard = false
    )

    constructor(settings: Settings) : this(
        settings.darkTheme,
        settings.backDesign,
        settings.layout.tag.toLayoutVariant(),
        settings.animationStrategy.toVariant(),
        settings.drawingStrategy.toVariant(),
        settings.emptyDiscard
    )

    fun create(assets: AssetManager): Settings {
        return Settings(
            backDesign,
            layout.create(),
            animation.create(assets),
            drawingStrategy.create(),
            SpriteSet(darkTheme, backDesign, assets),
            UiSkin(assets, assets[BundleAssets.Bundle]["skinKey"] == "cjk", darkTheme),
            emptyDiscard
        )
    }

    fun copy(
        darkTheme: Boolean = this.darkTheme,
        backDesign: Int = this.backDesign,
        layout: Layouts = this.layout,
        animation: AnimationStrategies = this.animation,
        drawingStrategy: DrawingStrategies = this.drawingStrategy,
        emptyDiscard: Boolean = this.emptyDiscard,
    ): SettingsData {
        return SettingsData(
            darkTheme,
            backDesign,
            layout,
            animation,
            drawingStrategy,
            emptyDiscard,
        )
    }

}

enum class Layouts {
    Basic,
    Diamonds,
    Inverted2nd;

    val tag
        get() = when (this) {
            Basic -> BasicLayout.TAG
            Diamonds -> DiamondsLayout.TAG
            Inverted2nd -> Inverted2ndLayout.TAG
        }
}

fun String.toLayoutVariant(): Layouts = when (this) {
    BasicLayout.TAG -> Layouts.Basic
    DiamondsLayout.TAG -> Layouts.Diamonds
    else -> Layouts.Inverted2nd
}

fun Layouts.create(): Layout = when (this) {
    Layouts.Basic -> BasicLayout()
    Layouts.Diamonds -> DiamondsLayout()
    Layouts.Inverted2nd -> Inverted2ndLayout()
}

enum class AnimationStrategies {
    Blink,
    Dissolve,
    FadeOut
}

fun AnimationStrategy.toVariant(): AnimationStrategies = when (this) {
    is AnimationStrategy.Strategies.Blink -> AnimationStrategies.Blink
    is AnimationStrategy.Strategies.FadeOut -> AnimationStrategies.FadeOut
    is AnimationStrategy.Strategies.Dissolve -> AnimationStrategies.Dissolve
}

fun AnimationStrategies.create(assets: AssetManager): AnimationStrategy = when (this) {
    AnimationStrategies.Blink -> AnimationStrategy.Strategies.Blink(assets)
    AnimationStrategies.Dissolve -> AnimationStrategy.Strategies.Dissolve(assets)
    AnimationStrategies.FadeOut -> AnimationStrategy.Strategies.FadeOut(assets)
}

enum class DrawingStrategies {
    BackVisible,
    BackHidden
}

fun CardDrawingStrategy.toVariant(): DrawingStrategies =
    if (this is CardDrawingStrategy.Strategies.BackVisible) {
        DrawingStrategies.BackVisible
    } else {
        DrawingStrategies.BackHidden
    }

fun DrawingStrategies.create(): CardDrawingStrategy = when (this) {
    DrawingStrategies.BackHidden -> CardDrawingStrategy.Strategies.BackHidden
    DrawingStrategies.BackVisible -> CardDrawingStrategy.Strategies.BackVisible
}

