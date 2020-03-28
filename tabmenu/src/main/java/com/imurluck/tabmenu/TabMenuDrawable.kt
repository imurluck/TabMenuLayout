package com.imurluck.tabmenu

import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.lang.ref.WeakReference
import kotlin.math.abs

/**
 * for
 * create by imurluck
 * create at 2020-03-22
 */
class TabMenuDrawable(
    context: Context,
    private val topDecorationHeight: Int) : Drawable() {

    private val contextRef = WeakReference(context)

    private val radius = context.resources.getDimensionPixelOffset(R.dimen.select_circle_radius)

    private val radiusUnit = radius / 10.0F

    private var currentX = 500.0F

    private var currentY = topDecorationHeight + radius * 6.0F / 10.0F


    private var currentCircleChangeRatio = 0.0F

    private var waterDropTopPoint = PointF(currentX, currentY - radius + radius * currentCircleChangeRatio)
    private var waterDropBottomPoint = PointF(currentX, currentY + radius - radius * currentCircleChangeRatio)
    private var waterDropLeftPoint = PointF(currentX - radius - radius * currentCircleChangeRatio, currentY)
    private var waterDropRightPoint = PointF(currentX + radius + radius * currentCircleChangeRatio, currentY)

    private val moveAnimatorListeners = mutableListOf<MoveAnimatorListener>()

    internal var menuItemWidth = 0.0F

    private val topDecorationPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FBFCFE")
        strokeWidth = 1.5F
        isAntiAlias = true
    }

    private val rectBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FBFCFE")
        isAntiAlias = true
    }

    private val waterDropPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#0D9E74")
        isAntiAlias = true
    }

    private val decorationTopPoint = PointF(0.0F, 0.0F)

    private val topDecorationPath = Path()

    private val waterDropPath = Path()

    private var lastEndStayX = 0.0F

    private val moveAnimator = MoveAnimator()

    internal fun initialize(x: Float) {
        currentX = x
        lastEndStayX = x

        waterDropLeftPoint.x = currentX - radius - radius * currentCircleChangeRatio
        waterDropLeftPoint.y = currentY

        waterDropTopPoint.x = x
        waterDropTopPoint.y = currentY - radius + radius * currentCircleChangeRatio

        waterDropRightPoint.x = currentX + radius + radius * currentCircleChangeRatio
        waterDropRightPoint.y = currentY

        waterDropBottomPoint.x = currentX
        waterDropBottomPoint.y = currentY + radius - radius * currentCircleChangeRatio

        moveAnimator.initialize(menuItemWidth)
    }

    /**
     * dispatch the water drop center point when moving
     */
    private fun dispatchMoveUpdate(currentX: Float, currentY: Float) {
        for (moveListener in moveAnimatorListeners) {
            moveListener.onMoveUpdate(currentX, currentY)
        }
    }


    override fun draw(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        updateWaterDropPoint()
        drawRectBackground(canvas)
        drawTopDecoration(canvas)
        drawWaterDrop(canvas)
        canvas.restoreToCount(saveCount)
    }

    private fun updateWaterDropPoint() {
        waterDropTopPoint.x = currentX
        waterDropTopPoint.y = currentY - radius + radius * currentCircleChangeRatio
        waterDropBottomPoint.x = currentX
        waterDropBottomPoint.y = currentY + radius - radius * currentCircleChangeRatio
        waterDropLeftPoint.x = currentX - radius - radius * currentCircleChangeRatio
        waterDropLeftPoint.y = currentY
        waterDropRightPoint.x = currentX + radius + radius * currentCircleChangeRatio
        waterDropRightPoint.y = currentY
    }

    /**
     * draw the water drop with Bézier curve
     */
    private fun drawWaterDrop(canvas: Canvas) {
        waterDropPath.reset()
        waterDropPath.moveTo(waterDropTopPoint.x, waterDropTopPoint.y)
        waterDropPath.cubicTo(waterDropTopPoint.x + radius * CIRCLE_MAGIC_DIGIT, waterDropTopPoint.y,
            waterDropRightPoint.x, waterDropRightPoint.y - radius * CIRCLE_MAGIC_DIGIT,
            waterDropRightPoint.x, waterDropRightPoint.y)
        waterDropPath.cubicTo(waterDropRightPoint.x, waterDropRightPoint.y + radius * CIRCLE_MAGIC_DIGIT,
            waterDropBottomPoint.x + radius * CIRCLE_MAGIC_DIGIT, waterDropBottomPoint.y,
            waterDropBottomPoint.x, waterDropBottomPoint.y)
        waterDropPath.cubicTo(waterDropBottomPoint.x - radius * CIRCLE_MAGIC_DIGIT, waterDropBottomPoint.y,
            waterDropLeftPoint.x, waterDropLeftPoint.y + radius * CIRCLE_MAGIC_DIGIT,
            waterDropLeftPoint.x, waterDropLeftPoint.y)
        waterDropPath.cubicTo(waterDropLeftPoint.x, waterDropLeftPoint.y - radius * CIRCLE_MAGIC_DIGIT,
            waterDropTopPoint.x - radius * CIRCLE_MAGIC_DIGIT, waterDropTopPoint.y,
            waterDropTopPoint.x, waterDropTopPoint.y)
        canvas.drawPath(waterDropPath, waterDropPaint)
    }

    private fun drawRectBackground(canvas: Canvas) {
        canvas.drawRect(0.0F, topDecorationHeight.toFloat(),
            bounds.right.toFloat() - bounds.left, bounds.bottom.toFloat(), rectBgPaint)
    }

    private fun drawTopDecoration(canvas: Canvas) {
        decorationTopPoint.x = currentX
        decorationTopPoint.y = waterDropTopPoint.y - radius * 3.0F / 10.0F
        // decoration top height / 6
        val unit = ((topDecorationHeight - decorationTopPoint.y) / 6.0).toFloat()
        topDecorationPath.reset()
        topDecorationPath.moveTo(decorationTopPoint.x, decorationTopPoint.y)
        topDecorationPath.cubicTo(currentX - 5 * radiusUnit, decorationTopPoint.y,
            currentX - 8 * radiusUnit, decorationTopPoint.y + 2 * unit,
            currentX - 10 * radiusUnit, decorationTopPoint.y + 3 * unit)
        topDecorationPath.quadTo(currentX - 12 * radiusUnit, decorationTopPoint.y + 4 * unit,
            currentX - 15 * radiusUnit, decorationTopPoint.y + 5 * unit)
        topDecorationPath.quadTo(currentX - 18 * radiusUnit, decorationTopPoint.y + 6 * unit + 0.5F,
            currentX - 20 * radiusUnit, decorationTopPoint.y + 6 * unit + 0.5F)

        topDecorationPath.lineTo(currentX + 20 * radiusUnit, decorationTopPoint.y + 6 * unit + 0.5F)
        topDecorationPath.quadTo(currentX + 18 * radiusUnit, decorationTopPoint.y + 6 * unit + 0.5F,
            currentX + 15 * radiusUnit, decorationTopPoint.y + 5 * unit)
        topDecorationPath.quadTo(currentX + 12 * radiusUnit, decorationTopPoint.y + 4 * unit,
            currentX + 10 * radiusUnit, decorationTopPoint.y + 3 * unit)
        topDecorationPath.cubicTo(currentX + 8 * radiusUnit, decorationTopPoint.y + 2 * unit,
            currentX + 5 * radiusUnit, decorationTopPoint.y,
            decorationTopPoint.x, decorationTopPoint.y)
        topDecorationPath.close()
        canvas.drawPath(topDecorationPath, topDecorationPaint)
    }

    override fun setAlpha(alpha: Int) {

    }

    fun addMoveAnimatorListener(listener: MoveAnimatorListener) {
        if (!moveAnimatorListeners.contains(listener)) {
            moveAnimatorListeners.add(listener)
        }
    }

    fun release() {
        moveAnimatorListeners.clear()
        moveAnimator.cancel()
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    fun animationToDestination(destinationX: Float) {
        moveAnimator.restart(destinationX)
    }

    companion object {
        private const val TAG = "TabMenuDrawable"

        private const val CIRCLE_MAGIC_DIGIT = 0.552284749831F
    }

    interface MoveAnimatorListener {

        fun onMoveUpdate(centerX: Float, centerY: Float)
    }

    inner class MoveAnimator : ValueAnimator() {

        private var destinationX = 0.0F

        private var changeRangeX = 0.0F

        private val maxCircleChangeRatio = 0.4F

        init {
            interpolator = LinearInterpolator()
            duration = 400L
            addUpdateListener {
                currentX = it.animatedValue as Float
                if (abs(currentX - lastEndStayX) <= changeRangeX) {
                    currentCircleChangeRatio = abs(currentX - lastEndStayX) / changeRangeX * maxCircleChangeRatio
                } else if (abs(currentX - destinationX) <= changeRangeX) {
                    currentCircleChangeRatio = abs(currentX - destinationX) / changeRangeX * maxCircleChangeRatio
                }
                invalidateSelf()
                dispatchMoveUpdate(currentX, currentY)
            }
            doOnEnd {
                lastEndStayX = destinationX
            }
        }

        internal fun initialize(menuItemWidth: Float) {
            changeRangeX = menuItemWidth * maxCircleChangeRatio
        }

        internal fun restart(destinationX: Float) {
            cancel()
            this.destinationX = destinationX
            setFloatValues(currentX, destinationX)
            start()
        }
    }
}