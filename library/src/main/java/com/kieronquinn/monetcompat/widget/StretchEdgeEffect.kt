package com.kieronquinn.monetcompat.widget

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

class StretchEdgeEffect(context: Context, private val view: View, private val direction: Direction): EdgeEffectCompat(context) {

    private var originalHeight = view.measuredHeight.toFloat()

    private var mWidth: Float? = null
    private var mHeight: Float? = null

    /**
     *  The maximum [View.setScaleY] to use when stretching either by pull or by absorb (fling)
     */
    var maxScaleY = 1.1f

    init {
        //Update the view width & height after load if needed (seems to be required for NestedScrollView)
        view.post {
            originalHeight = view.measuredHeight.toFloat()
            mWidth = view.measuredWidth.toFloat()
            mHeight = view.measuredHeight.toFloat()
        }
    }

    override fun onPull(deltaDistance: Float, displacement: Float) {
        super.onPull(deltaDistance, displacement)
        if(direction == Direction.UNSUPPORTED) return
        absorbAnimation?.cancel()
        view.pivotY = direction.getPivotY(originalHeight) ?: return
        val distance = calculateDistanceFromGlowValues(mGlowAlpha, mGlowScaleY)
        view.scaleY = (1 + distance).coerceAtMost(maxScaleY)
    }

    override fun onRelease() {
        super.onRelease()
        if(direction == Direction.UNSUPPORTED) return
        //Animate the scaleY back to 1
        view.animate().scaleY(1f).setDuration(250L).start()
    }

    private var absorbAnimation: AnimatorSet? = null
    override fun onAbsorb(velocity: Int) {
        super.onAbsorb(velocity)
        if(direction == Direction.UNSUPPORTED) return
        absorbAnimation?.cancel()
        view.pivotY = direction.getPivotY(originalHeight) ?: return
        val distance = calculateDistanceFromGlowValues(mGlowAlphaFinish, mGlowScaleYFinish) * 25f
        absorbAnimation = AnimatorSet()
        val endScaleY = (1f + distance).coerceAtMost(maxScaleY)
        val scaleUp = ValueAnimator.ofFloat(1f, endScaleY).apply {
            addUpdateListener {
                view.scaleY = it.animatedValue as Float
            }
            duration = mDuration.toLong()
        }
        val scaleDown = ValueAnimator.ofFloat(endScaleY, 1f).apply {
            addUpdateListener {
                view.scaleY = it.animatedValue as Float
            }
            duration = mDuration.toLong()
        }
        absorbAnimation?.run {
            play(scaleUp)
            play(scaleDown).after(scaleUp)
            start()
        }
    }

    private fun calculateDistanceFromGlowValues(arg4: Float, arg5: Float): Float {
        if (arg4 >= 1.0f) {
            return 1.0f
        }
        if (arg4 > 0.0f) {
            val v = 1.428571f / (mGlowScaleY - 1.0f)
            return v * v / mHeight as Float
        }
        return arg5 / 0.8f
    }

    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
        mWidth = width.toFloat()
        mHeight = height.toFloat()
    }

    enum class Direction {
        TOP, BOTTOM, UNSUPPORTED;

        companion object {
            fun fromRecyclerViewDirection(direction: Int): Direction {
                return when(direction){
                    RecyclerView.EdgeEffectFactory.DIRECTION_TOP -> TOP
                    RecyclerView.EdgeEffectFactory.DIRECTION_BOTTOM -> BOTTOM
                    //TODO support left/right
                    else -> UNSUPPORTED
                }
            }
        }

        fun getPivotY(originalHeight: Float): Float? {
            return when(this){
                TOP -> 0f
                BOTTOM -> originalHeight
                else -> null
            }
        }

    }

}