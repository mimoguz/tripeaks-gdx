package ogz.tripeaks.util

import ktx.collections.GdxIntArray

fun GdxIntArray.any(p: (elem: Int) -> Boolean): Boolean = this.items.any(p)

fun GdxIntArray.all(p: (elem: Int) -> Boolean): Boolean = this.items.all(p)
