package ogz.tripeaks.ui

import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.screens.Constants.TEXT_BUTTON_VERTICAL_PADDING

class Menu(skin: UiSkin, actions: List<Pair<String, () -> Unit>>) :
    PopTable(skin["menu", PopTableStyle::class.java]) {
    init {
        val last = actions.size - 1
        actions.forEachIndexed { index, action ->
            val item = add(MenuItem(action.first, skin).apply {
                onClick { action.second.invoke() }
            }).fillX()
            if (index != last) {
                item.spaceBottom(TEXT_BUTTON_VERTICAL_PADDING)
                row()
            }
        }
    }
}