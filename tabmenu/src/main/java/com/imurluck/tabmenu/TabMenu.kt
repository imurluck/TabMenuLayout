package com.imurluck.tabmenu

import android.content.Context
import android.util.AttributeSet
import android.view.MenuInflater
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuView
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

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }

    override fun getWindowAnimations(): Int = 0

    override fun initialize(menu: MenuBuilder) {
        this.menuBuilder = menu
    }
}