package ogz.tripeaks.graphics

import ogz.tripeaks.ecs.AnimationComponent
import ogz.tripeaks.ecs.RenderComponent
import ogz.tripeaks.ecs.TransformComponent

typealias AnimationStep = (RenderComponent, TransformComponent, AnimationComponent, Float) -> Boolean

class AnimationSet(
    val cardRemoved: AnimationStep,
    val faceRemoved: AnimationStep,
    val screenTransition: AnimationStep
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
    private val animationStep: AnimationStep =  { _, _, _, _ -> true }
    override fun get(animationSet: AnimationSet): AnimationStep = animationStep
}


