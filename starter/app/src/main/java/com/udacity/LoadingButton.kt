package com.udacity

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // background setup
    private var backGroundColor = 0
    private var backGroundRect = RectF(0F, 0F, 0F, 0F)

    // text setup
    private var text = resources.getString(R.string.button_init)
    private val textSize = resources.getDimension(R.dimen.button_text_size)
    private var textColor = 0

    // pac man setup
    private val pacmanSize = resources.getDimension(R.dimen.pacman_size)
    private val pacmanColor = resources.getColor(R.color.colorAccent, null)
    private val pacmanRect = RectF(0F, 0F, 0F, 0F)
    private var currentSweepAngle = 0F

    // loading bar setup
    private val loadingBarColor = resources.getColor(R.color.colorPrimaryDark, null)
    private val loadingBarRect = RectF(0F, 0F, 0F, 0F)
    private var loadingBarWidth = 0F
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    private var widthSize = 0
    private var heightSize = 0

    private val loadingAnimatorSet = AnimatorSet()
    private var completedAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Init) { _, _, newButtonState ->
        when (newButtonState) {
            ButtonState.Init -> {
                text = resources.getString(R.string.button_init)
                contentDescription = resources.getString(R.string.button_init)
            }
            ButtonState.Clicked -> {
                contentDescription = resources.getString(R.string.button_clicked)
                buttonState = ButtonState.Loading
            }
            ButtonState.Loading -> {
                text = resources.getString(R.string.button_loading)
                contentDescription = resources.getString(R.string.button_loading)
                startLoadingAnimation()
            }
            ButtonState.Completed -> {
                text = resources.getString(R.string.button_completed)
                contentDescription = resources.getString(R.string.button_completed)
                startCompletedAnimation()
            }
        }

        invalidate()
    }

    private fun startCompletedAnimation() {
        completedAnimator = ValueAnimator.ofFloat(widthSize.toFloat(), 0F).apply {
            duration = 1000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                loadingBarWidth = this.animatedValue as Float
                invalidate()
            }
            start()
        }

        completedAnimator.addListener(object : AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                buttonState = ButtonState.Init
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationRepeat(p0: Animator?) {}
        })
    }

    private fun startLoadingAnimation() {
        val pacmanAnimator = ValueAnimator.ofFloat(0F, 360F).apply {
            interpolator = LinearInterpolator()
            addUpdateListener {
                currentSweepAngle = this.animatedValue as Float
                invalidate()
            }
        }

        val loadingBarAnimator = ValueAnimator.ofFloat(0F, widthSize.toFloat()).apply {
            interpolator = LinearInterpolator()
            addUpdateListener {
                loadingBarWidth = this.animatedValue as Float
                invalidate()
            }
        }

        loadingAnimatorSet.apply {
            playTogether(pacmanAnimator, loadingBarAnimator)
            duration = 2000L
            start()
        }

        loadingAnimatorSet.addListener(object : AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                buttonState = ButtonState.Completed
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationRepeat(p0: Animator?) {}
        })
    }


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backGroundColor = getColor(R.styleable.LoadingButton_backGroundColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBackground(canvas)
        when(buttonState) {
            ButtonState.Loading -> {
                drawLoadingBar(canvas)
                drawPacman(canvas)
            }
            ButtonState.Completed -> {
                drawLoadingBar(canvas)
            }
            else -> {}
        }
        drawText(canvas)
    }

    private fun drawBackground(canvas: Canvas?) {
        paint.color = backGroundColor
        backGroundRect.set(
            0F,
            0F,
            widthSize.toFloat(),
            heightSize.toFloat()
        )
        canvas?.drawRect(backGroundRect, paint)
    }

    private fun drawText(canvas: Canvas?) {
        paint.color = textColor
        paint.textSize = textSize
        val textPositionX = (widthSize / 2).toFloat()
        val textPositionY = (heightSize / 2 * 1.2).toFloat()
        canvas?.drawText(text, textPositionX, textPositionY, paint)
    }

    private fun drawPacman(canvas: Canvas?) {
        paint.color = pacmanColor
        val pacmanLeft = (2 * widthSize / 3).toFloat() + pacmanSize
        val pacmanTop = (height / 2 / 1.2).toFloat()
        val pacmanRight = pacmanLeft + pacmanSize
        val pacmanBottom = pacmanTop + pacmanSize
        pacmanRect.set(
            pacmanLeft,
            pacmanTop,
            pacmanRight,
            pacmanBottom
        )
        canvas?.drawArc(
            pacmanRect,
            0f,
            currentSweepAngle,
            true,
            paint
        )
    }

    private fun drawLoadingBar(canvas: Canvas?) {
        paint.color = loadingBarColor
        loadingBarRect.set(
            0F,
            0F,
            loadingBarWidth,
            heightSize.toFloat()
        )
        canvas?.drawRect(loadingBarRect, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        return if (buttonState == ButtonState.Init) {
            buttonState = ButtonState.Clicked
            super.performClick()
        } else false
    }
}