package ogz.tripeaks.ui

import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.UiSkin

class Menu(skin: UiSkin, actions: List<Pair<String, () -> Unit>>) :
    PopTable(skin["menu", PopTableStyle::class.java]) {
    init {
        val last = actions.size - 1
        actions.forEachIndexed { index, action ->
            add(MenuItem(action.first, skin).apply {
                onClick { action.second.invoke() }
            }).fillX()
            if (index != last) row()
        }
    }
}