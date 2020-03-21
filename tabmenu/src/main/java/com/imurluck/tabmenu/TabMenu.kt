package com.imurluck.tabmenu

import android.content.Context
import android.util.AttributeSet
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuView
import androidx.core.view.children
import androidx.core.view.forEach

/**
 * for
 * create by imurluck
 * create at 2020-03-21
 */
class TabMenu(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), MenuView {

    private lateinit var menuBuilder: MenuBuilder

    private val tempChildWidth = IntArray(MAX_ITEM_COUNT)

    private val menuItemHeight = resources.getDimensionPixelOffset(R.dimen.menu_item_height)

    private val topDecorationHeight = resources.getDimensionPixelOffset(R.dimen.top_decoration_height)

    init {
        initialize(MenuBuilder(context))
        resolveAttrs()
        setupMenuItems()
    }

    private fun resolveAttrs() {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TabMenu)
        inflateMenu(ta.getInt(R.styleable.TabMenu_tab_menu_res, -1))
        ta.recycle()
    }

    private fun inflateMenu(@MenuRes menuResId: Int) {
        require(menuResId != -1) { "must set menu res with #app:tab_menu_res#" }
        MenuInflater(context).inflate(menuResId, menuBuilder)
    }

    private fun setupMenuItems() {
        menuBuilder.forEach {

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = MeasureSpec.getSize(widthMeasureSpec)
        val childVisibleCount = menuBuilder.visibleItems.size
        val averageChildWidth = totalWidth / childVisibleCount
        var modWidth = totalWidth % childVisibleCount
        val childHeightSpec = MeasureSpec.makeMeasureSpec(menuItemHeight, MeasureSpec.EXACTLY)
        for (child in children) {
            if (child.visibility != View.VISIBLE) {
                continue
            }
            val childWidth = if (modWidth-- > 0) averageChildWidth + 1 else averageChildWidth
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), childHeightSpec)
        }
        val heightSpec = MeasureSpec.makeMeasureSpec(menuItemHeight + topDecorationHeight, MeasureSpec.EXACTLY)
        setMeasuredDimension(
            View.resolveSizeAndState(totalWidth, MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.EXACTLY), 0),
            View.resolveSizeAndState(menuItemHeight + topDecorationHeight, heightSpec, 0)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val top = topDecorationHeight
        var left = 0
        for (child in children) {
            if (child.visibility != GONE) {
                child.layout(left, top, left + child.measuredWidth, top + child.measuredHeight)
                left += child.measuredWidth
            }
        }
    }

    override fun getWindowAnimations(): Int = 0

    override fun initialize(menu: MenuBuilder) {
        this.menuBuilder = menu
    }

    companion object {
        private const val MAX_ITEM_COUNT = 5
    }
}