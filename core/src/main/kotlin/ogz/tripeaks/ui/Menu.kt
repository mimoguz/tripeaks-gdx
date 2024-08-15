package ogz.tripeaks.ui

import com.ray3k.stripe.PopTable
import ogz.tripeaks.Constants.VIEWPORT_VERTICAL_PADDING
import ogz.tripeaks.assets.UiSkin

class Menu(skin: UiSkin, actions: List<Pair<String, () -> Unit>>) :
    PopTable(skin[UiSkin.MENU_POP_STYLE, PopTableStyle::class.java]) {

    init {
        isHideOnUnfocus = true
        pad(
            VIEWPORT_VERTICAL_PADDING,
            VIEWPORT_VERTICAL_PADDING,
            VIEWPORT_VERTICAL_PADDING + 2,
            VIEWPORT_VERTICAL_PADDING,
        )
        val last = actions.size - 1
        actions.forEachIndexed { index, action ->
            val item = add(MenuItem(action.first, skin, action.second)).fillX()
            if (index != last) {
                item.spaceBottom(2f)
                row()
            }
        }
    }

}