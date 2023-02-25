package ogz.tripeaks.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import ktx.ashley.add
import ktx.ashley.configureEntity
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ogz.tripeaks.ecs.AnimationComponent
import ogz.tripeaks.ecs.MultiSpriteComponent
import ogz.tripeaks.ecs.SpriteLayerPool
import ogz.tripeaks.ecs.TransformComponent
import ogz.tripeaks.graphics.CardRemovedAnimation
import ogz.tripeaks.graphics.CardSprite
import ogz.tripeaks.graphics.EmptySprite
import ogz.tripeaks.graphics.FaceSprite
import ogz.tripeaks.models.Card
import ogz.tripeaks.models.GameState
import ogz.tripeaks.models.layout.Socket
import ogz.tripeaks.screens.Constants.CARD_HEIGHT
import ogz.tripeaks.screens.Constants.CARD_WIDTH
import ogz.tripeaks.screens.Constants.CELL_HEIGHT
import ogz.tripeaks.screens.Constants.CELL_PADDING_LEFT
import ogz.tripeaks.screens.Constants.CELL_PADDING_TOP
import ogz.tripeaks.screens.Constants.CELL_WIDTH
import ogz.tripeaks.screens.Constants.DISCARD_LEFT
import ogz.tripeaks.screens.Constants.FACE_HEIGHT
import ogz.tripeaks.screens.Constants.FACE_WIDTH
import ogz.tripeaks.screens.Constants.SMALL_10_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_HEIGHT
import ogz.tripeaks.screens.Constants.SMALL_FACE_H_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_V_PADDING
import ogz.tripeaks.screens.Constants.SMALL_FACE_WIDTH
import ogz.tripeaks.screens.Constants.STACK_RIGHT
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING
import ogz.tripeaks.screens.Constants.WORLD_HEIGHT
import kotlin.math.truncate

@Suppress("NOTHING_TO_INLINE")
abstract class AbstractEntityUtils(
    private val game: GameState,
    private val engine: PooledEngine,
    protected val layerPool: SpriteLayerPool,
    protected val assets: AssetManager,
    override val cardEntities: ImmutableArray<Entity>,
    override val stackEntity: Entity,
    override val discardEntity: Entity
) : EntityUtils {
    protected abstract fun addBack(component: MultiSpriteComponent, card: Card, dx: Float, dy: Float)
    protected abstract fun addStackBack(component: MultiSpriteComponent, card: Card, dx: Float, dy: Float)

    override fun addRemovalAnimation(socketIndex: Int) {
        engine.add {
            entity {
                val socket = game.gameLayout[socketIndex]
                val card = game.socketState(socketIndex).card
                with<TransformComponent> {
                    setSocketPosition(socket, position)
                }
                with<MultiSpriteComponent> {
                    z = socket.z + 100
                    addBaseLayer(this, 0f, 0f)
                    addFaceLayer(this, card, 0f, 0f)
                }
                with<AnimationComponent> {
                    timeRemaining = Constants.DISSOLVE_TIME
                    animationType = CardRemovedAnimation
                }
            }
        }
    }

    override fun initDiscard(worldWidth: Float) {
        updateDiscard(worldWidth)
        moveDiscard(worldWidth)
    }

    override fun updateDiscard(worldWidth: Float) {
        engine.configureEntity(discardEntity) {
            with<MultiSpriteComponent> {
                returnLayers(this)
                if (game.discard.isEmpty) {
                    addEmptyListLayer(this, 0f, 0f)
                } else {
                    val card = game.discard[game.discard.size - 1]
                    addBaseLayer(this, 0f, 0f)
                    addFaceLayer(this, card, 0f, 0f)
                }
            }
        }
    }

    override fun moveDiscard(worldWidth: Float) {
        engine.configureEntity(discardEntity) {
            with<TransformComponent> {
                position.set(
                    truncate(worldWidth / -2f) + DISCARD_LEFT,
                    truncate(-WORLD_HEIGHT / 2) + VERTICAL_PADDING - 1f
                )
            }
        }
    }

    override fun initStack(worldWidth: Float) {
        updateStack(worldWidth)
        moveStack(worldWidth)
    }

    override fun updateStack(worldWidth: Float) {
        engine.configureEntity(stackEntity) {
            with<MultiSpriteComponent> {
                returnLayers(this)
                if (game.stack.isEmpty) {
                    addEmptyListLayer(this, 0f, 0f)
                } else {
                    val last = game.stack.size - 1
                    val step = 6
                    for (i in 0..last) {
                        val dx = (-i * step).toFloat()
                        val card = game.stack[i]
                        addBaseLayer(this, dx, 0f)
                        if (i < last) {
                            addStackBack(this, card, dx, 0f)
                        } else {
                            addFaceLayer(this, card, dx, 0f)
                        }
                    }
                }
            }
        }
    }

    override fun moveStack(worldWidth: Float) {
        engine.configureEntity(stackEntity) {
            with<TransformComponent> {
                position.set(
                    truncate(worldWidth / 2f) - STACK_RIGHT,
                    truncate(-WORLD_HEIGHT / 2f) + VERTICAL_PADDING - 1f
                )
            }
        }
    }

    override fun initSocket(index: Int) {
        val socket = game.gameLayout[index]
        val state = game.socketState(index)
        val isOpen = game.isOpen(index)
        val entity = cardEntities[state.card]
        engine.configureEntity(entity) {
            with<TransformComponent> {
                setSocketPosition(socket, position)
            }
        }
        when {
            state.isEmpty -> {}
            isOpen -> setSocketOpen(entity, state.card, socket.z)
            else -> setSocketClosed(entity, state.card, socket.z)
        }
    }

    override fun updateSocket(index: Int) {
        val socket = game.gameLayout[index]
        val state = game.socketState(index)
        val isOpen = game.isOpen(index)
        val entity = cardEntities[state.card]
        when {
            state.isEmpty -> {
                entity[MultiSpriteComponent.mapper]?.let {
                    returnLayers(it)
                }
                entity.remove(MultiSpriteComponent::class.java)
            }
            isOpen -> setSocketOpen(entity, state.card, socket.z)
            else -> setSocketClosed(entity, state.card, socket.z)
        }
    }

    private inline fun returnLayers(component: MultiSpriteComponent) {
        component.layers.forEach { layerPool.free(it) }
        component.layers.clear()
    }

    private fun setSocketPosition(socket: Socket, position: Vector2) {
        val maxY = truncate(WORLD_HEIGHT / 2f) - VERTICAL_PADDING - CARD_HEIGHT + 1f
        val minX = truncate(game.gameLayout.numberOfColumns / -2f) * CELL_WIDTH
        position.set(
            minX + socket.column * CELL_WIDTH + CELL_PADDING_LEFT,
            maxY - socket.row * CELL_HEIGHT - CELL_PADDING_TOP
        )
    }

    private fun setSocketClosed(entity: Entity, card: Card, level: Int) {
        engine.configureEntity(entity) {
            with<MultiSpriteComponent> {
                z = level
                returnLayers(this)
                addBaseLayer(this, 0f, 0f)
                addBack(this, card, 0f, 0f)
            }
        }
    }

    private fun setSocketOpen(entity: Entity, card: Card, level: Int) {
        engine.configureEntity(entity) {
            with<MultiSpriteComponent> {
                z = level
                returnLayers(this)
                addBaseLayer(this, 0f, 0f)
                addFaceLayer(this, card, 0f, 0f)
            }
        }
    }

    protected inline fun addBaseLayer(component: MultiSpriteComponent, dx: Float, dy: Float) {
        component.layers.add(layerPool.obtain().apply {
            spriteType = CardSprite
            localPosition.set(dx, dy)
        })
    }

    protected inline fun addFaceLayer(component: MultiSpriteComponent, card: Card, dx: Float, dy: Float) {
        component.layers.add(layerPool.obtain().apply {
            spriteType = FaceSprite(card)
            localPosition.set(
                truncate((CARD_WIDTH - FACE_WIDTH) / 2f) + dx,
                truncate((CARD_HEIGHT - FACE_HEIGHT) / 2f) + dy
            )
        })
    }

    private inline fun addEmptyListLayer(component: MultiSpriteComponent, dx: Float, dy: Float) {
        component.layers.add(layerPool.obtain().apply {
            spriteType = EmptySprite
            localPosition.set(dx, dy)
        })
    }
}