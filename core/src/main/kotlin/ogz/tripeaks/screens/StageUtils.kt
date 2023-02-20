package ogz.tripeaks.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ui.IconButton

class StageUtils(private val assets: AssetManager, private val stage: Stage) {
    fun menuButton(skin: UiSkin, menu: PopTable, onShow: () -> Unit): IconButton =
        IconButton(skin, assets[TextureAtlasAssets.Ui].findRegion("menu_${skin.resourcePostfix}")).apply {
            setSize(Constants.CARD_WIDTH.toFloat(), Constants.CARD_WIDTH.toFloat())
            menu.attachToActor(this, Align.bottomRight, Align.bottomLeft, 0f, -Constants.VERTICAL_PADDING.toFloat())
            onClick {
                onShow.invoke()
                menu.show(stage)
            }
        }

    fun dealButton(skin: UiSkin, action: () -> Unit): IconButton =
        IconButton(skin, assets[TextureAtlasAssets.Ui].findRegion("deal_${skin.resourcePostfix}")).apply {
            setSize(Constants.CARD_WIDTH.toFloat(), Constants.CARD_HEIGHT.toFloat())
            onClick { action.invoke() }
        }

    fun undoButton(skin: UiSkin, action: () -> Unit): IconButton =
        IconButton(skin, assets[TextureAtlasAssets.Ui].findRegion("undo_${skin.resourcePostfix}")).apply {
            setSize(Constants.CARD_WIDTH.toFloat(), Constants.CARD_HEIGHT.toFloat())
            onClick { action.invoke() }
        }
}