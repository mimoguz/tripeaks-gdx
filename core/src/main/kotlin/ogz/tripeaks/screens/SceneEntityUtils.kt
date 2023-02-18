package ogz.tripeaks.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ktx.ashley.EngineEntity
import ktx.ashley.with
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.ecs.MultiSpriteComponent
import ogz.tripeaks.ecs.SpriteLayer
import ogz.tripeaks.ecs.SpriteLayerPool
import ogz.tripeaks.graphics.BackSprite
import ogz.tripeaks.graphics.CardSprite
import ogz.tripeaks.graphics.FaceSprite
import ogz.tripeaks.graphics.SmallFaceSprite
import ogz.tripeaks.models.Card
import ogz.tripeaks.ui.IconButton

/** Helper functions for GameScreen for creating actors and entities. */
class SceneEntityUtils(private val layerPool: SpriteLayerPool, private val assets: AssetManager) {
    fun menuButton(stage: Stage, skin: UiSkin, assets: AssetManager, menu: PopTable, onShow: () -> Unit): IconButton =
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
            onClick {
                action.invoke()
            }
        }

    fun undoButton(skin: UiSkin, action: () -> Unit): IconButton =
        IconButton(skin, assets[TextureAtlasAssets.Ui].findRegion("undo_${skin.resourcePostfix}")).apply {
            setSize(Constants.CARD_WIDTH.toFloat(), Constants.CARD_HEIGHT.toFloat())
            onClick {
                action.invoke()
            }
        }

    fun setupCardOpen(entity: EngineEntity, card: Card, level: Int) {
        entity.with<MultiSpriteComponent> {
            setupCard(this, level)
            layers.add(faceLayer(card))
        }
    }

    fun setupCardClosed(entity: EngineEntity, level: Int) {
        entity.with<MultiSpriteComponent> {
            setupCard(this, level)
            layers.add(layerPool.obtain().apply { spriteType = BackSprite })
        }
    }

    fun setupCardClosedShowing(entity: EngineEntity, card: Card, level: Int) {
        entity.with<MultiSpriteComponent> {
            setupCard(this, level)
            layers.add(faceLayer(card))
            layers.add(smallFaceLayer(card))
        }
    }

    private fun setupCard(component: MultiSpriteComponent, level: Int) {
        component.apply {
            z = level
            color.set(1f, 1f, 1f, 1f)
            layers.add(layerPool.obtain().apply {
                spriteType = CardSprite
            })
        }
    }

    private fun faceLayer(card: Card): SpriteLayer =
        layerPool.obtain().apply {
            spriteType = FaceSprite(card)
            localPosition.set(
                ((Constants.CARD_WIDTH - Constants.FACE_WIDTH) / 2).toFloat(),
                ((Constants.CARD_HEIGHT - Constants.FACE_HEIGHT) / 2).toFloat()
            )
        }

    private fun smallFaceLayer(card: Card): SpriteLayer =
        layerPool.obtain().apply {
            spriteType = SmallFaceSprite(card)
            localPosition.set(
                (Constants.CARD_WIDTH - Constants.SMALL_FACE_WIDTH).toFloat() - Constants.SMALL_FACE_H_PADDING,
                Constants.SMALL_FACE_V_PADDING
            )
        }
}