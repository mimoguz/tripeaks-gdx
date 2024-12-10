package ogz.tripeaks.views

import com.badlogic.gdx.utils.Pool

class CardViewPool : Pool<CardView>(32) {
    override fun newObject(): CardView = CardView()
}

class AnimationViewPool : Pool<AnimationView>(16) {
    override fun newObject(): AnimationView = AnimationView()
}