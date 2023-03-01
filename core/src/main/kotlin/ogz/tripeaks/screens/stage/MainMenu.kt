package ogz.tripeaks.screens.stage

import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.UiSkin

class MainMenu(skin: UiSkin) : PopTable(skin["menu", PopTableStyle::class.java]) {
    init {
        add(MenuItem("Menu item 1", skin).apply {
            onClick { println("Menu item 1") }
        }).fillX()
        row()
        add(MenuItem("Menu item 2", skin).apply {
            onClick { println("Menu item 1") }
        }).fillX()
        row()
        add(MenuItem("Menu item 3", skin).apply {
            onClick { println("Menu item 1") }
        }).fillX()
    }
}