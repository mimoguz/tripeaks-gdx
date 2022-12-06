package ogz.tripeaks.graphics

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import ogz.tripeaks.ecs.AnimationComponent
import ogz.tripeaks.ecs.RenderComponent
import ogz.tripeaks.ecs.TransformComponent

typealias AnimationStep = (RenderComponent, TransformComponent, AnimationComponent, Float) -> Boolean

class AnimationSet(
    val cardRemoved: AnimationStep,
    val faceRemoved: AnimationStep,
    val screenTransition: AnimationStep,
    val shaderProgram: ShaderProgram
)

sealed interface AnimationType {
    fun get(animationSet: AnimationSet): AnimationStep
}

object CardRemovedAnimation : AnimationType {
    override fun get(animationSet: AnimationSet): AnimationStep = animationSet.cardRemoved
}

object FaceRemovedAnimation : AnimationType {
    override fun get(animationSet: AnimationSet): AnimationStep = animationSet.faceRemoved
}

object ScreenTransitionAnimation : AnimationType {
    override fun get(animationSet: AnimationSet): AnimationStep = animationSet.screenTransition
}

object NoAnimation : AnimationType {
    private val animationStep: AnimationStep = { _, _, _, _ -> true }
    override fun get(animationSet: AnimationSet): AnimationStep = animationStep
}

object Animations {
    private const val CARD_HEIGHT = 37
    private const val CARD_WIDTH = 25
    private const val FACE_HEIGHT = 30
    private const val FACE_WIDTH = 15

    val Dissolve = AnimationSet(
        cardRemoved = { render, transform, animation, delta ->
            val st = animation.timeRemaining % 2f
            if (st > 1f) {
                transform.scale.set(1f, 1f)
                transform.position.set(
                    MathUtils.floor(CARD_WIDTH * -0.5f).toFloat(),
                    MathUtils.floor(CARD_HEIGHT * -0.5f).toFloat(),
                )
                render.color.set(0.8f, 1f, 1f, 1f)
            } else {
                val rt = 1.0f - st
                render.color.set(render.color.r, st, 1f, 1f)
                transform.position.set(transform.position.x, transform.position.y - delta * rt * 400f)
                transform.scale.set(
                    transform.scale.x - delta * rt * 0.25f,
                    transform.scale.y + delta * 6f * rt
                )
            }
            animation.timeRemaining > 0f
        },
        faceRemoved = { render, transform, animation, delta ->
            val st = animation.timeRemaining % 2f
            if (st > 1f) {
                transform.scale.set(1f, 1f)
                transform.position.set(
                    MathUtils.floor(FACE_WIDTH * -0.5f).toFloat(),
                    MathUtils.floor(FACE_HEIGHT * -0.5f).toFloat(),
                )
                render.color.set(render.color.r, 1f, 1f, 1f)
            } else {
                val rt = 1.0f - st
                render.color.set(1f, st, 1f, 1f)
                transform.position.set(transform.position.x, transform.position.y - delta * (1.0f - st) * 350f)
                transform.scale.set(
                    transform.scale.x - delta * rt * 0.5f,
                    transform.scale.y + delta * 8f * rt
                )
            }
            animation.timeRemaining > 0f
        },
        screenTransition = { render, transform, animation, _ ->
            if (animation.timeRemaining <= 0.5f) {
                render.color.set(0.2f, animation.timeRemaining * 2f, 1f, 1f)
                transform.scale.set(1f, 1f)
            }
            animation.timeRemaining >= 0
        },
        shaderProgram = ShaderProgram(
            javaClass.classLoader.getResource("shaders/basic.vert")?.readText(),
            javaClass.classLoader.getResource("shaders/dissolve.frag")?.readText()
        )
    )

    val Blinds = AnimationSet(
        cardRemoved = { render, transform, animation, _ ->
            val st = animation.timeRemaining % 2f
            if (st > 0.5f) {
                render.color.set(0.02f, 1f, 1f, 1f)
                transform.scale.set(1f, 1f)
                transform.position.set(
                    MathUtils.floor(CARD_WIDTH * -0.5f).toFloat(),
                    MathUtils.floor(CARD_HEIGHT * -0.5f).toFloat(),
                )
            } else {
                render.color.set(0.02f, st * 2f, 1f, 1f)
            }
            animation.timeRemaining > 0f
        },
        faceRemoved = { render, transform, animation, delta ->
            val st = animation.timeRemaining % 2f
            if (st > 0.5f) {
                render.color.set(0.025f, 1f, 1f, 1f)
                transform.scale.set(1f, 1f)
                transform.position.set(
                    MathUtils.floor(FACE_WIDTH * -0.5f).toFloat(),
                    MathUtils.floor(FACE_HEIGHT * -0.5f).toFloat(),
                )
            } else {
                render.color.set(0.025f, st * 2f, 1f, 1f)
            }
            animation.timeRemaining > 0f
        },
        screenTransition = { render, _, animation, _ ->
            if (animation.timeRemaining <= 0.5f) {
                render.color.set(0.1f, animation.timeRemaining * 2f, 1f, 1f)
            }
            animation.timeRemaining >= 0
        },
        shaderProgram = ShaderProgram(
            javaClass.classLoader.getResource("shaders/basic.vert")?.readText(),
            javaClass.classLoader.getResource("shaders/curtains.frag")?.readText()
        )
    )
}


