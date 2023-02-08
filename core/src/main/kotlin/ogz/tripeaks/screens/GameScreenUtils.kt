package ogz.tripeaks.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ui.IconButton

object GameScreenUtils {
    fun menuButton(stage: Stage, skin: UiSkin, assets: AssetManager, menu: PopTable): IconButton =
        IconButton(skin, assets[TextureAtlasAssets.Ui].findRegion("icon_${skin.resourcePostfix}")).apply {
            menu.attachToActor(this)
            onClick { menu.show(stage) }
            setSize(Constants.CARD_WIDTH.toFloat(), (Constants.CARD_HEIGHT / 2).toFloat())
        }
}