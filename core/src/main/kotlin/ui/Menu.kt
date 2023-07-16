package ogz.tripeaks.ui

import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.screens.Constants.TEXT_BUTTON_HORIZONTAL_PADDING
import ogz.tripeaks.screens.Constants.TEXT_BUTTON_VERTICAL_PADDING
import ogz.tripeaks.screens.Constants.VERTICAL_PADDING

class Menu(skin: UiSkin, actions: List<Pair<String, () -> Unit>>) :
    PopTable(skin["menu", PopTableStyle::class.java]) {
    init {
        isHideOnUnfocus = true
        pad(
            VERTICAL_PADDING,
            VERTICAL_PADDING,
            VERTICAL_PADDING + 2,
            VERTICAL_PADDING,
        )
        val last = actions.size - 1
        actions.forEachIndexed { index, action ->
            val item = add(MenuItem(action.first, skin).apply {
                onClick { action.second.invoke() }
            }).fillX()
            if (index != last) {
                item.spaceBottom(2f)
                row()
            }
        }
    }
}