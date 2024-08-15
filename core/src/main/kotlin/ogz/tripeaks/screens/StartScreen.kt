package ogz.tripeaks.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.ray3k.stripe.PopTable
import ktx.app.KtxScreen
import ktx.inject.Context
import ogz.tripeaks.Constants
import ogz.tripeaks.Main
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.graphics.CustomViewport
import ogz.tripeaks.screens.stage.AboutDialog
import ogz.tripeaks.screens.stage.OptionsDialog
import ogz.tripeaks.screens.stage.OptionsDialogResult
import ogz.tripeaks.screens.stage.StatisticsDialog
import ogz.tripeaks.services.PlayerStatisticsService
import ogz.tripeaks.services.SettingsService
import ogz.tripeaks.ui.LabelButton

class StartScreen(private val app: Main, private val context: Context) : KtxScreen {

    private val settings = context.inject<SettingsService>()
    private val stats = context.inject<PlayerStatisticsService>()
    private val switch = StartScreenSwitch()
    private val stage = context.inject<Stage>()
    private val viewport = context.inject<CustomViewport>()
    private val bundle = context.inject<AssetManager>()[BundleAssets.Bundle]
    private val assets = context.inject<AssetManager>()
    private lateinit var table: Table

    init {
        val batch = context.inject<SpriteBatch>()
        switch.addState(
            PausedStartScreen::class.java, PausedStartScreen(
                batch,
                viewport,
                settings,
                assets
            )
        )
        switch.addState(
            PlayingStartScreen::class.java, PlayingStartScreen(
                batch,
                viewport,
                settings
            )
        )
        switch.update()
        switch.switch(PlayingStartScreen::class.java)
        Gdx.input.inputProcessor = stage
        setupUI()
    }

    private fun setupUI() {
        stage.clear()
        val skin = settings.get().skin
        table = Table(skin).apply {
            setSize(viewport.worldWidth, viewport.worldHeight)
            center()
            setPosition(0f, 0f)
            setupButtonTable(this, skin)
        }
        stage.addActor(table)

    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
        stage.viewport.update(width, height, true)
        table.setSize(viewport.worldWidth, viewport.worldHeight)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        switch.render(delta)
        stage.draw()
    }

    override fun dispose() {
        switch.dispose()
    }

    private fun setupButtonTable(table: Table, skin: UiSkin) {
        table.apply {
            defaults()
                .fillX()
                .uniformX()
                .minWidth(86f)
                .pad(
                    0f,
                    MathUtils.floor(Constants.UI_HORIZONTAL_SPACING / 2f).toFloat(),
                    Constants.UI_VERTICAL_SPACING,
                    MathUtils.floor(Constants.UI_HORIZONTAL_SPACING / 2f).toFloat()
                )

            add(LabelButton(skin, bundle["start"], this@StartScreen::startGame))
                .padTop(1f)
                .colspan(2)
                .fillX()
                .minHeight(26f)
            row()
            add(LabelButton(skin, bundle["options"], this@StartScreen::showOptionsDialog))
            add(LabelButton(skin, bundle["statistics"], this@StartScreen::showStatisticsDialog))
            row()
            add(LabelButton(skin, bundle["about"], this@StartScreen::showAboutDialog)).padBottom(0f)
            add(LabelButton(skin, bundle["exit"], this@StartScreen::exit)).padBottom(0f)
        }
    }

    private fun showOptionsDialog() {
        val dialog = OptionsDialog(settings.get().skin, assets, settings.getData()) { result ->
            when (result) {
                is OptionsDialogResult.Types.Return -> {}
                is OptionsDialogResult.Types.Apply -> {
                    settings.update(result.settingsData)
                    setupUI()
                }
            }
        }
        showDialog(dialog)
    }

    private fun showStatisticsDialog() {
        val dialog = StatisticsDialog(settings.get().skin, assets, stats.get())
        showDialog(dialog)
    }

    private fun showAboutDialog() {
        val dialog = AboutDialog(settings.get().skin, assets)
        showDialog(dialog)
    }

    private fun exit() {
        Gdx.app.exit()
    }

    private fun showDialog(dialog: PopTable) {
        switch.switch(PausedStartScreen::class.java)
        dialog.addListener {
            if (dialog.isHidden) {
                onDialogHidden()
                true
            } else {
                false
            }
        }
        dialog.show(stage)
    }

    private fun startGame() {
        val gameScreen = GameScreen(context)
        app.addScreen(gameScreen)
        app.setScreen<GameScreen>()
        app.removeScreen<StartScreen>()
        dispose()
    }

    private fun onDialogHidden() {
        switch.switch(PlayingStartScreen::class.java)
    }

}