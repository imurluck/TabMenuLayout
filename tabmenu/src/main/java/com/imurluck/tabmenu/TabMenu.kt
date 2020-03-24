package com.imurluck.tabmenu

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.appcompat.view.menu.MenuView
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.lang.IllegalArgumentException

/**
 * for
 * create by imurluck
 * create at 2020-03-21
 */
class TabMenu @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), MenuView {

    private lateinit var menuBuilder: MenuBuilder

    private val menuItemHeight = resources.getDimensionPixelOffset(R.dimen.menu_item_height)

    private val topDecorationHeight = resources.getDimensionPixelOffset(R.dimen.top_decoration_height)

    private var currentSelectItemPosition = 0
    private var currentSelectItem: MenuItemView? = null

    private val menuDrawable = TabMenuDrawable(context, menuItemHeight, topDecorationHeight)

    init {
        initialize(MenuBuilder(context))
        resolveAttrs()
        setupMenuItems()
        background = menuDrawable
    }

    private fun resolveAttrs() {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TabMenu)
        if (ta.hasValue(R.styleable.TabMenu_tab_menu_res)) {
            MenuInflater(context).inflate(ta.getResourceId(R.styleable.TabMenu_tab_menu_res, -1), menuBuilder)
        } else {
            throw IllegalArgumentException("must set menu res with #app:tab_menu_res#")
        }
        setCurrentSelectItem(ta.getInt(R.styleable.TabMenu_select_item_position, 0))
        ta.recycle()
    }

    private fun setupMenuItems() {
        menuBuilder.forEach { menuItem ->
            addView(MenuItemView(context).also { itemView ->
                itemView.initialize(menuItem as MenuItemImpl, 0)
                itemView.setOnClickListener {
                    animationToSelectedItem(itemView)
                }
            })
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = MeasureSpec.getSize(widthMeasureSpec)
        val childVisibleCount = menuBuilder.visibleItems.size
        val averageChildWidth = totalWidth / childVisibleCount
        var modWidth = totalWidth % childVisibleCount
        val childHeightSpec = MeasureSpec.makeMeasureSpec(menuItemHeight, MeasureSpec.EXACTLY)
        for (child in children) {
            if (child.visibility == View.GONE) {
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

    /**
     * set current select item position with no transition,
     * transition animation runs only in click actions
     */
    fun setCurrentSelectItem(position: Int) {
        if (position < 0 || position >= menuBuilder.visibleItems.size) {
            return
        }
        currentSelectItemPosition = position
    }

    private fun animationToSelectedItem(itemView: MenuItemView) {
        if (itemView == currentSelectItem && currentSelectItemPosition == findVisibleItemPosition(itemView)) {
            return
        }
        menuDrawable.animationToSelectItem(itemView)
    }

    private fun findVisibleItemPosition(itemView: TabMenuItem): Int {
        var position = 0
        for (child in children) {
            if (child.visibility == View.GONE) {
                continue
            }
            if (child == itemView) {
                return position
            }
            position++
        }
        return position
    }

    override fun getWindowAnimations(): Int = 0

    override fun initialize(menu: MenuBuilder) {
        this.menuBuilder = menu
    }

    companion object {
        private const val MAX_ITEM_COUNT = 5
    }
}