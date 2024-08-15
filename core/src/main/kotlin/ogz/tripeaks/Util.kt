package ogz.tripeaks

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table

fun Table.pad(vertical: Float, horizontal: Float): Table = this.pad(
    vertical,
    horizontal,
    vertical,
    horizontal
)

fun <T : Actor> Cell<T>.pad(vertical: Float, horizontal: Float): Cell<T> = this.pad(
    vertical,
    horizontal,
    vertical,
    horizontal
)