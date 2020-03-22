package com.imurluck.tabmenu

import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.lang.ref.WeakReference
import kotlin.math.sqrt

/**
 * for
 * create by imurluck
 * create at 2020-03-22
 */
class TabMenuDrawable(
    context: Context,
    private val menuItemHeight: Int,
    private val topDecorationHeight: Int) : Drawable() {

    private val contextRef = WeakReference(context)

    private val radius = context.resources.getDimensionPixelOffset(R.dimen.select_circle_radius)

    private var currentX = 500.0F

    private var currentY = topDecorationHeight + radius * 6.0F / 10.0F

    private val topDecorationPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FBFCFE")
        isAntiAlias = true
    }

    private val rectBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FBFCFE")
        isAntiAlias = true
    }

    private val circlePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#0D9E74")
        isAntiAlias = true
    }

    private val decorationTopPoint = PointF(0.0F, 0.0F)

    private val topDecorationPath = Path()

    private val animator = ValueAnimator().apply {
        setFloatValues(0.0F, radius * 4.0F / 10.0F)
        interpolator = FastOutSlowInInterpolator()
        duration = 1000L
        val originY = currentY
        addUpdateListener {
            currentY = originY + it.animatedValue as Float
            invalidateSelf()
        }
        repeatMode = REVERSE
        repeatCount = ValueAnimator.INFINITE
        startDelay = 2000L
        start()
    }


    override fun draw(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        drawRectBackground(canvas)
        drawTopDecoration(canvas)
        canvas.drawCircle(currentX, currentY, radius.toFloat(), circlePaint)
        canvas.restoreToCount(saveCount)
    }

    private fun drawRectBackground(canvas: Canvas) {
        canvas.drawRect(0.0F, topDecorationHeight.toFloat(),
            bounds.right.toFloat() - bounds.left, bounds.bottom.toFloat(), rectBgPaint)
    }

    private fun drawTopDecoration(canvas: Canvas) {
        decorationTopPoint.x = currentX
        decorationTopPoint.y = currentY - radius - radius * 2.0F / 10.0F
        // decoration top height / 6
        val unit = ((topDecorationHeight - decorationTopPoint.y) / 6.0).toFloat()
        topDecorationPath.reset()
        topDecorationPath.moveTo(decorationTopPoint.x, decorationTopPoint.y)
        topDecorationPath.cubicTo(currentX - 5 * unit, decorationTopPoint.y,
            currentX - 8 * unit, decorationTopPoint.y + 2 * unit,
            currentX - 10 * unit, decorationTopPoint.y + 3 * unit)
        topDecorationPath.quadTo(currentX - 12 * unit, decorationTopPoint.y + 4 * unit,
            currentX - 15 * unit, decorationTopPoint.y + 5 * unit)
        topDecorationPath.quadTo(currentX - 18 * unit, decorationTopPoint.y + 6 * unit,
            currentX - 20 * unit, decorationTopPoint.y + 6 * unit)

        topDecorationPath.lineTo(currentX + 20 * unit, decorationTopPoint.y + 6 * unit)
        topDecorationPath.quadTo(currentX + 18 * unit, decorationTopPoint.y + 6 * unit,
            currentX + 15 * unit, decorationTopPoint.y + 5 * unit)
        topDecorationPath.quadTo(currentX + 12 * unit, decorationTopPoint.y + 4 * unit,
            currentX + 10 * unit, decorationTopPoint.y + 3 * unit)
        topDecorationPath.cubicTo(currentX + 8 * unit, decorationTopPoint.y + 2 * unit,
            currentX + 5 * unit, decorationTopPoint.y,
            decorationTopPoint.x, decorationTopPoint.y)
        topDecorationPath.close()
        canvas.drawPath(topDecorationPath, topDecorationPaint)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    companion object {
        private const val TAG = "TabMenuDrawable"
    }
}