package ogz.tripeaks.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.screens.Constants.CARD_HEIGHT
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.screens.stage.Menu
import ogz.tripeaks.ui.BottomLeft
import ogz.tripeaks.ui.BottomRight
import ogz.tripeaks.ui.GameButton
import ogz.tripeaks.ui.TopRight

class StageUtils(private val assets: AssetManager, private val stage: Stage) {
    fun menuButton(
        skin: UiSkin,
        anchor: Actor,
        onShow: (Menu) -> Unit,
        onHide: () -> Unit,
        actions: List<Pair<String, () -> Unit>>
    ): GameButton =
        GameButton(
            assets[TextureAtlasAssets.Ui].findRegion("menu_${skin.resourcePostfix}"),
            TopRight(Vector2(HORIZONTAL_PADDING, VERTICAL_PADDING))
        ) {
            val menu = Menu(skin, actions).apply {
                addListener {
                    if (isHidden) {
                        onHide.invoke()
                        true
                    } else {
                        false
                    }
                }

                attachToActor(
                    anchor,
                    Align.topRight,
                    Align.bottomRight,
                    -HORIZONTAL_PADDING,
                    -CARD_WIDTH - VERTICAL_PADDING
                )
            }
            onShow.invoke(menu)
            menu.show(stage)
        }.apply {
            setSize(CARD_WIDTH, CARD_WIDTH)
        }

    fun dealButton(skin: UiSkin, action: () -> Unit): GameButton =
        GameButton(
            assets[TextureAtlasAssets.Ui].findRegion("deal_${skin.resourcePostfix}"),
            BottomRight(Vector2(HORIZONTAL_PADDING, VERTICAL_PADDING - 1)),
            action
        ).apply {
            setSize(CARD_WIDTH, CARD_HEIGHT)
        }

    fun undoButton(skin: UiSkin, action: () -> Unit): GameButton =
        GameButton(
            assets[TextureAtlasAssets.Ui].findRegion("undo_${skin.resourcePostfix}"),
            BottomLeft(Vector2(HORIZONTAL_PADDING, VERTICAL_PADDING - 1)),
            action
        ).apply {
            setSize(CARD_WIDTH, CARD_HEIGHT)
        }
}