package ogz.tripeaks.services

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.Disposable
import ktx.inject.Context
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.graphics.SpriteSet
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.Settings
import ogz.tripeaks.models.ThemeMode
import ogz.tripeaks.models.layout.BasicLayout
import ogz.tripeaks.models.layout.DiamondsLayout
import ogz.tripeaks.models.layout.Inverted2ndLayout
import ogz.tripeaks.models.layout.Layout
import ogz.tripeaks.models.layout.TheaterLayout
import ogz.tripeaks.views.AnimationStrategy
import ogz.tripeaks.views.CardDrawingStrategy

class SettingsService(private val systemDarkMode: Boolean) : Disposable {

    private var settings: Settings? = null
    private lateinit var persistence: PersistenceService
    private lateinit var assets: AssetManager
    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
        persistence = context.inject()
        assets = context.inject()
        settings =
            persistence.loadSettings()?.create(assets, systemDarkMode)
                ?: SettingsData().create(
                    assets, systemDarkMode
                )
    }

    fun paused() {
        // TODO: Saving on update now, not needed.
        // settings?.let { persistence.saveSettings(SettingsData(it)) }
    }

    fun resumed() {
        settings = settings
            ?: persistence.loadSettings()?.create(assets, systemDarkMode)
                    ?: SettingsData().create(assets, systemDarkMode)
    }

    fun get(): Settings = settings!!

    fun getData(): SettingsData = SettingsData(settings!!)

    fun update(settingsData: SettingsData) {
        val current = this.settings!!
        val settingsChanged =
            settingsData.themeMode != current.themeMode
                    || settingsData.backDesign != current.backDesign
                    || settingsData.animation != current.animationStrategy.toVariant()
                    || settingsData.drawingStrategy != current.drawingStrategy.toVariant()
                    || settingsData.layout.tag != current.layout.tag
                    || settingsData.emptyDiscard != current.emptyDiscard

        if (settingsChanged) {
            val newSettings = settingsData.create(assets, systemDarkMode)
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

class SettingsDataV1_1 private constructor(
    var darkTheme: Boolean,
    var backDesign: Int,
    var layout: Layouts,
    var animation: AnimationStrategies,
    var drawingStrategy: DrawingStrategies,
    var emptyDiscard: Boolean,
)

class SettingsData private constructor(
    var themeMode: ThemeMode,
    var backDesign: Int,
    var layout: Layouts,
    var animation: AnimationStrategies,
    var drawingStrategy: DrawingStrategies,
    var emptyDiscard: Boolean,
) {

    constructor() : this(
        themeMode = ThemeMode.System,
        backDesign = 0,
        layout = Layouts.Basic,
        animation = AnimationStrategies.FadeOut,
        drawingStrategy = DrawingStrategies.BackHidden,
        emptyDiscard = false
    )

    constructor(settings: Settings) : this(
        settings.themeMode,
        settings.backDesign,
        settings.layout.tag.toLayoutVariant(),
        settings.animationStrategy.toVariant(),
        settings.drawingStrategy.toVariant(),
        settings.emptyDiscard
    )

    fun create(assets: AssetManager, systemDarkMode: Boolean): Settings {
        val darkTheme =
            themeMode == ThemeMode.Dark || themeMode == ThemeMode.Black || (themeMode == ThemeMode.System && systemDarkMode)
        return Settings(
            themeMode,
            backDesign,
            layout.create(),
            animation.create(assets).apply {
                setTheme(darkTheme)
            },
            drawingStrategy.create(),
            SpriteSet(themeMode, systemDarkMode, backDesign, assets),
            UiSkin(
                assets,
                assets[BundleAssets.Bundle]["skinKey"] == "cjk",
                themeMode,
                systemDarkMode
            ),
            emptyDiscard
        )
    }

    fun copy(
        themeMode: ThemeMode = this.themeMode,
        backDesign: Int = this.backDesign,
        layout: Layouts = this.layout,
        animation: AnimationStrategies = this.animation,
        drawingStrategy: DrawingStrategies = this.drawingStrategy,
        emptyDiscard: Boolean = this.emptyDiscard,
    ): SettingsData {
        return SettingsData(
            themeMode,
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
    Inverted2nd,
    Theater;

    val tag
        get() = when (this) {
            Basic -> BasicLayout.TAG
            Diamonds -> DiamondsLayout.TAG
            Inverted2nd -> Inverted2ndLayout.TAG
            Theater -> TheaterLayout.TAG
        }
}

fun String.toLayoutVariant(): Layouts = when (this) {
    BasicLayout.TAG -> Layouts.Basic
    DiamondsLayout.TAG -> Layouts.Diamonds
    Inverted2ndLayout.TAG -> Layouts.Inverted2nd
    TheaterLayout.TAG -> Layouts.Theater
    else -> Layouts.Basic // Default
}

fun Layouts.create(): Layout = when (this) {
    Layouts.Basic -> BasicLayout()
    Layouts.Diamonds -> DiamondsLayout()
    Layouts.Inverted2nd -> Inverted2ndLayout()
    Layouts.Theater -> TheaterLayout()
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

