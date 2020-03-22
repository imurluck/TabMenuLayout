package com.imurluck.tabmenu

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.appcompat.view.menu.MenuView
import kotlinx.android.synthetic.main.menu_item.view.*

/**
 * for tab menu item
 * create by imurluck
 * create at 2020-03-21
 */
class MenuItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), MenuView.ItemView {

    init {
        View.inflate(context, R.layout.menu_item, this)
    }

    private lateinit var itemData: MenuItemImpl

    override fun initialize(itemData: MenuItemImpl, menuType: Int) {
        this.itemData = itemData
        setTitle(itemData.title)
        setCheckable(itemData.isCheckable)
        if (itemData.isCheckable) {
            setChecked(itemData.isChecked)
        }
        setIcon(itemData.icon)

    }

    override fun getItemData(): MenuItemImpl = itemData

    override fun setIcon(icon: Drawable?) {
        iconImg.setImageDrawable(icon)
    }

    override fun showsIcon(): Boolean = true

    override fun prefersCondensedTitle(): Boolean = false

    override fun setCheckable(checkable: Boolean) {
        refreshDrawableState()
    }

    override fun setTitle(title: CharSequence?) {
        titleTv.text = title
    }

    override fun setChecked(checked: Boolean) {
    }

    override fun setShortcut(showShortcut: Boolean, shortcutKey: Char) {
    }


}