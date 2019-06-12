package com.meetarp.imagecarousel

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.res.TypedArray
import android.net.Uri
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2

/**
 * Custom view that renders an image carousel leveraging ViewPager2.
 *
 * Image sources can be [DrawableRes] or [Uri], and can be loaded from either
 * populating a [CarouselImageList] either manually or through
 * [CarouselImageList.fromDrawableResList] or [CarouselImageList.fromUriList]
 * and calling [setImages].
 *
 * Also automatically creates carousel item indicators to denote the current position of the
 * carousel's ViewPager.
 *
 * Has multiple defined app:attributes:
 *
 * 1. app:[insetIndicators] - Boolean. Determines whether or not to inset the carousel item indicators.
 * 2. app:[offsetIndicatorsBy] - Dimension (pixels) to offset the carousel item indicators from the ViewPager.
 * 3. app:[indicatorCircleColor] - Color to tint all carousel item indicators.
 * 4. app:[indicatorCircleSize] - Dimension (pixels) for the base size of all carousel item indicators.
 * 5. app:[indicatorActiveScaleFactor] - Scale factor for the selected state of a carousel item indicator.
 * 6. app:[indicatorCircleSpacing] - Dimension (pixels) for the total space in between carousel item indicators.
 *
 * @author Prateem Shrestha (prateems@gmail.com)
 */
@Suppress("MemberVisibilityCanBePrivate", "Unused")
class ImageCarousel @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
    LinearLayout(ctx, attrs, defStyleAttr, defStyleRes) {

    // region Layout elements
    private val component = LayoutInflater.from(ctx).inflate(R.layout.image_carousel, this, false)
    private val imageViewPager: ViewPager2 = component.findViewById(R.id.imagesPager)
    private val indicatorContainer: LinearLayout = component.findViewById(R.id.indicatorContainer)
    // endregion

    // region view pager and page indicator logic related
    private val imagesAdapter = ImageCarouselAdapter(ctx)
    private var previousSelectedImage = -1
    // endregion

    // region attributes
    /**
     * If set to true, will overlay the page indicators on top of the image.
     * Default: true
     */
    var insetIndicators: Boolean = true
        set(value) {
            field = value
            updateIndicatorContainerLayout()
            updateViewPadding()
        }

    /**
     * The distance between the bottom edge of the carousel and the indicators, in pixels.
     * Default: 16dp.
     */
    @Dimension(unit = Dimension.PX) var offsetIndicatorsBy: Int = dpToPx(16f).toInt()
        set(value) {
            field = value
            updateIndicatorContainerLayout()
        }

    /**
     * The tint/color to apply to each individual page indicator.
     * Default: [android.R.color.white]
     */
    @ColorInt var indicatorCircleColor: Int = ContextCompat.getColor(ctx, android.R.color.white)
        set(value) {
            field = value
            imagesAdapter.setErrorTint(value)
            updateIndicatorAttributes()
        }

    /**
     * The size of each individual page indicator, in pixels.
     * Default: 5dp.
     */
    @Dimension(unit = Dimension.PX) var indicatorCircleSize: Int = dpToPx(5f).toInt()
        set(value) {
            field = value
            updateIndicatorAttributes()
        }

    /**
     * The scale factor to apply to the indicators when they are in the 'selected' state.
     * Default: 1.8.
     */
    var indicatorActiveScaleFactor: Float = 1.8f
        set(value) {
            field = value
            updateViewPadding()
        }

    /**
     * The total space in between each individual page indicator, in pixels.
     * Default: 10dp.
     */
    @Dimension var indicatorCircleSpacing: Float = dpToPx(10f)
        set(value) {
            field = value
            updateIndicatorAttributes()
        }
    // endregion

    // region initialization / lifecycle
    init {
        // If attributes were supplied in the XML, utilize them as a first-measure.
        // Programmatically setting these attributes will predictably override these values
        attrs?.let {
            val attributes: TypedArray = ctx.obtainStyledAttributes(attrs, R.styleable.ImageCarousel)
            insetIndicators = attributes.getBoolean(R.styleable.ImageCarousel_insetIndicators, true)
            offsetIndicatorsBy = attributes.getDimension(R.styleable.ImageCarousel_offsetIndicatorsBy, dpToPx(16f)).toInt()

            indicatorCircleColor = attributes.getColor(R.styleable.ImageCarousel_indicatorCircleColor, ContextCompat.getColor(ctx, android.R.color.white))
            indicatorCircleSize = attributes.getDimension(R.styleable.ImageCarousel_indicatorCircleSize, dpToPx(5f)).toInt()
            indicatorActiveScaleFactor = attributes.getFloat(R.styleable.ImageCarousel_indicatorActiveScaleFactor, 1.8f)
            indicatorCircleSpacing = attributes.getDimension(R.styleable.ImageCarousel_indicatorCircleSpacing, dpToPx(5f))
            attributes.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // Set up the adapter
        imageViewPager.adapter = imagesAdapter
        imageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Can get onPageSelected called when only 1 image is available
                // In that case, no indicators are added, so prevent a NPE when calling getChildAt()
                if (indicatorContainer.childCount > 0) {
                    if (previousSelectedImage != -1) {
                        animateIndicator(indicatorContainer.getChildAt(previousSelectedImage), reverse = true)
                    }
                    animateIndicator(indicatorContainer.getChildAt(position))
                    previousSelectedImage = position
                }
            }
        })

        // Make sure circle page indicators don't get clipped by padding
        clipChildren = false
        clipToPadding = false

        // Now that all the setup is done, actually attach the component.
        addView(component)
        updateViewPadding()
        updateIndicatorContainerLayout()
    }
    // endregion

    // region attribute change helpers
    private fun updateIndicatorContainerLayout() {
        indicatorContainer.layoutParams = RelativeLayout
            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            .apply {
                addRule(if (insetIndicators) RelativeLayout.ALIGN_BOTTOM else RelativeLayout.BELOW, R.id.imagesPager)
                setMargins(0, if (insetIndicators) topMargin else offsetIndicatorsBy,
                    0, if (insetIndicators) offsetIndicatorsBy else bottomMargin)
            }
    }

    private fun updateIndicatorAttributes() {
        for (i in 0 until indicatorContainer.childCount) {
            val indicator = indicatorContainer.getChildAt(i)
            indicator.background.setTint(indicatorCircleColor)
            indicator.layoutParams = LayoutParams(indicatorCircleSize, indicatorCircleSize)
                .apply {
                    setMargins(
                        if (i == 0) 0 else (indicatorCircleSpacing / 2).toInt(), 0,
                        if (i == indicatorContainer.childCount - 1) 0 else (indicatorCircleSpacing / 2).toInt(), 0)
                }
        }
    }

    private fun updateViewPadding() {
        // This is necessary so that page indicator scaling does not get clipped by the layout bound
        val bottom = if (!insetIndicators) (indicatorCircleSize * indicatorActiveScaleFactor / 2).toInt() else 0
        setPadding(0, 0, 0, bottom)
    }
    // endregion

    // region public api
    fun setImages(images: CarouselImageList) {
        // update page indicators
        previousSelectedImage = 0
        indicatorContainer.removeAllViews()
        val imagesCount = images.getList().size
        if (imagesCount == 1) {
            indicatorContainer.visibility = View.GONE
        } else {
            indicatorContainer.visibility = View.VISIBLE

            for (i in 0 until imagesCount) {
                // add the indicator to the container
                val indicator = View(context).apply {
                    background = ContextCompat.getDrawable(context, R.drawable.carousel_item_indicator)!!.apply {
                        setTint(indicatorCircleColor)
                    }
                    layoutParams = LayoutParams(indicatorCircleSize, indicatorCircleSize)
                        .apply {
                            setMargins(
                                if (i == 0) 0 else (indicatorCircleSpacing / 2).toInt(), 0,
                                if (i == imagesCount - 1) 0 else (indicatorCircleSpacing / 2).toInt(), 0)
                        }
                }
                indicator.setOnClickListener { goTo(i) }
                indicatorContainer.addView(indicator)

                // Animate the indicator to the correct initial state
                animateIndicator(indicator, reverse = (i != 0), zeroDuration = true)
            }
        }

        // Update the adapter
        imagesAdapter.setImages(images)
        goTo(0)
    }
    // endregion

    // region helper methods
    private fun goTo(pos: Int) {
        imageViewPager.post { imageViewPager.setCurrentItem(pos, true) }
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    private fun animateIndicator(indicator: View, reverse: Boolean = false, zeroDuration: Boolean = false) {
        val animator = ObjectAnimator.ofPropertyValuesHolder(indicator, *getIndicatorAnimationProperties())
        animator.duration = if (zeroDuration) 0 else context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        if (reverse) {
            animator.interpolator = ReverseInterpolator()
        }
        animator.start()
    }

    private fun getIndicatorAnimationProperties(): Array<PropertyValuesHolder> {
        return arrayOf(
            PropertyValuesHolder.ofFloat("alpha", 0.5f, 1.0f),
            PropertyValuesHolder.ofFloat("scaleX", 1.0f, indicatorActiveScaleFactor),
            PropertyValuesHolder.ofFloat("scaleY", 1.0f, indicatorActiveScaleFactor)
        )
    }
    // endregion

    // region inner classes / components
    /**
     * This interpolator will play an animation in reverse.
     */
    private inner class ReverseInterpolator : Interpolator {
        override fun getInterpolation(paramFloat: Float): Float {
            return Math.abs(paramFloat - 1f)
        }
    }
    // endregion

}