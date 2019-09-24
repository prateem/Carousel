package com.meetarp.carousel

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * Custom view that renders a carousel of items leveraging ViewPager2.
 *
 * Can create a custom implementation of [CarouselAdapter] to make the carousel work for
 * any item content you wish. There are two pre-built implementations for image resources (ints)
 * and generic views: [CarouselImagesAdapter] and [CarouselViewsAdapter]
 *
 * Also automatically creates item indicators to denote the current position of the ViewPager.
 *
 * @author Prateem Shrestha (prateems@gmail.com)
 */
@Suppress("MemberVisibilityCanBePrivate", "Unused")
class Carousel<ItemType> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    interface ItemClickListener {
        fun onItemClicked(view: View, position: Int)
    }

    interface PageChangeListener {
        fun onPageSelected(position: Int)
    }

    // region Layout elements
    private val component: ConstraintLayout = LayoutInflater.from(context)
        .inflate(R.layout.carousel, this, false) as ConstraintLayout
    private val viewPager: ViewPager2 = component.findViewById(R.id.carouselPager)
    private val indicatorContainer: LinearLayout = component.findViewById(R.id.indicatorContainer)
    // endregion

    // region view pager and page logic related
    var adapter: CarouselAdapter<ItemType>? = null
        set(value) {
            field = value
            viewPager.adapter = adapter
            adapter?.let { adapter ->
                adapter.errorTint = indicatorColor
                post {
                    createIndicators(adapter.itemCount)
                    updateViewPadding()
                }
            }
        }

    private var previousActiveIndex = 0
    // endregion

    // region attributes
    /**
     * The color that will be applied to the background of the carousel, if visible.
     * Default: [android.R.color.transparent]
     */
    @ColorInt var carouselBackgroundColor: Int
            = ContextCompat.getColor(context, android.R.color.transparent)
        set(value) {
            field = value
            updateViewPagerBackground()
        }

    /**
     * If set to true, will overlay the page indicators on top of the image.
     * Default: true
     */
    var insetIndicators: Boolean = true
        set(value) {
            field = value
            updateConstraints()
            updateViewPadding()
        }

    /**
     * The distance between the bottom edge of the carousel and the indicators, in pixels.
     * Default: 16dp.
     */
    @Dimension(unit = Dimension.PX) var offsetIndicatorsBy: Int = dpToPx(16f).toInt()
        set(value) {
            field = value
            updateConstraints()
        }

    /**
     * The tint/color to apply to each individual page indicator.
     * Default: [android.R.color.white]
     */
    @ColorInt var indicatorColor: Int
            = ContextCompat.getColor(context, android.R.color.white)
        set(value) {
            field = value
            adapter?.errorTint = value
            updateAllIndicatorAttributes()
        }

    /**
     * The size of each individual page indicator, in pixels.
     * Default: 5dp.
     */
    @Dimension(unit = Dimension.PX) var indicatorSize: Int = dpToPx(5f).toInt()
        set(value) {
            field = value
            updateAllIndicatorAttributes()
        }

    /**
     * The total space in between each individual page indicator, in pixels.
     * Default: 10dp.
     */
    @Dimension var indicatorSpacing: Int = dpToPx(10f).toInt()
        set(value) {
            field = value
            updateAllIndicatorAttributes()
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
     * A page change listener to respond to paging events.
     */
    var pageChangeListener: PageChangeListener? = null
        set(value) {
            field = value
            pageChangeListener?.onPageSelected(previousActiveIndex)
        }
    // endregion

    // region initialization / lifecycle
    init {
        // If attributes were supplied in the XML, utilize them as a first-measure, falling back to defaults
        // as defined above. Programmatically setting these attributes will predictably override these values
        attrs?.let {
            val attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Carousel)
            carouselBackgroundColor = attributes.getColor(R.styleable.Carousel_carousel_backgroundColor, carouselBackgroundColor)
            insetIndicators = attributes.getBoolean(R.styleable.Carousel_carousel_insetIndicators, insetIndicators)
            offsetIndicatorsBy = attributes.getDimensionPixelOffset(R.styleable.Carousel_carousel_offsetIndicatorsBy, offsetIndicatorsBy)

            indicatorColor = attributes.getColor(R.styleable.Carousel_carousel_indicatorColor, indicatorColor)
            indicatorSize = attributes.getDimensionPixelSize(R.styleable.Carousel_carousel_indicatorSize, indicatorSize)
            indicatorSpacing = attributes.getDimensionPixelOffset(R.styleable.Carousel_carousel_indicatorSpacing, indicatorSpacing)
            indicatorActiveScaleFactor = attributes.getFloat(R.styleable.Carousel_carousel_indicatorActiveScaleFactor, indicatorActiveScaleFactor)
            attributes.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Can get onPageSelected called when only 1 image is available
                // In that case, no indicators are added, so prevent a NPE when calling getChildAt()
                if (indicatorContainer.childCount > 0) {
                    animateIndicator(indicatorContainer.getChildAt(previousActiveIndex), reverse = true)
                    animateIndicator(indicatorContainer.getChildAt(position))
                    previousActiveIndex = position
                }
                pageChangeListener?.onPageSelected(position)
            }
        })

        // Make sure circle page indicators don't get clipped by padding
        clipChildren = false
        clipToPadding = false

        // Now that all the setup is done, actually attach the component.
        addView(component)
        updateViewPadding()
        updateViewPagerBackground()
        updateConstraints()
    }
    // endregion

    // region attribute change helpers
    private fun updateAllIndicatorAttributes() {
        for (i in 0 until indicatorContainer.childCount) {
            updateIndicatorAttributes(
                indicatorContainer.getChildAt(i), i,
                i == indicatorContainer.childCount - 1
            )
        }
    }

    private fun updateViewPadding() {
        // This is necessary so that page indicator scaling does not get clipped by the layout bound
        val bottom =
            if (!insetIndicators)
                (indicatorSize * indicatorActiveScaleFactor / 2).toInt()
            else 0
        setPadding(0, 0, 0, bottom)
    }

    private fun updateViewPagerBackground() {
        viewPager.setBackgroundColor(carouselBackgroundColor)
    }
    // endregion

    // region helper methods
    private fun createIndicators(count: Int) {
        indicatorContainer.removeAllViews()
        if (count == 1) {
            indicatorContainer.visibility = View.GONE
        } else {
            indicatorContainer.visibility = View.VISIBLE

            for (i in 0 until count) {
                // create and add the indicator to the container
                val indicator = View(context)
                updateIndicatorAttributes(indicator, i, i == count - 1)
                indicator.setOnClickListener { goTo(i) }
                indicatorContainer.addView(indicator)

                // Animate the indicator to the correct initial state
                animateIndicator(indicator, reverse = (i != 0), zeroDuration = true)
            }
        }

        updateConstraints()
    }

    private fun updateIndicatorAttributes(
        indicator: View,
        indicatorPosition: Int,
        isLast: Boolean = false
    ) {
        if (indicator.background == null) {
            indicator.background = ContextCompat.getDrawable(context, R.drawable.carousel_item_indicator)
        }
        indicator.background.setTint(indicatorColor)
        indicator.layoutParams = LayoutParams(indicatorSize, indicatorSize)
            .apply {
                setMargins(
                    if (indicatorPosition == 0) 0 else (indicatorSpacing / 2), 0,
                    if (isLast) 0 else (indicatorSpacing / 2), 0)
            }
    }

    private fun updateConstraints() {
        ConstraintSet().also { constraints ->
            constraints.clone(component)

            if (indicatorContainer.childCount > 0) {
                if (insetIndicators) {
                    constraints.connect(
                        R.id.carouselPager, ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                        0
                    )
                    constraints.connect(
                        R.id.indicatorContainer, ConstraintSet.BOTTOM,
                        R.id.carouselPager, ConstraintSet.BOTTOM,
                        offsetIndicatorsBy
                    )
                    constraints.clear(R.id.indicatorContainer, ConstraintSet.TOP)
                } else {
                    constraints.connect(
                        R.id.carouselPager, ConstraintSet.BOTTOM,
                        R.id.indicatorContainer, ConstraintSet.TOP,
                        0
                    )
                    constraints.connect(
                        R.id.indicatorContainer, ConstraintSet.TOP,
                        R.id.carouselPager, ConstraintSet.BOTTOM,
                        offsetIndicatorsBy
                    )
                    constraints.connect(
                        R.id.indicatorContainer, ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                        0
                    )
                }
            } else {
                constraints.connect(
                    R.id.carouselPager, ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                    0
                )
            }

            constraints.applyTo(component)
        }
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    private fun goTo(pos: Int) {
        viewPager.post { viewPager.setCurrentItem(pos, true) }
    }

    private fun animateIndicator(
        indicator: View,
        reverse: Boolean = false,
        zeroDuration: Boolean = false
    ) {
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            indicator,
            *getIndicatorAnimationProperties()
        )

        animator.duration =
            if (zeroDuration) 0
            else context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        if (reverse)
            animator.interpolator = ReverseInterpolator()

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
            return abs(paramFloat - 1f)
        }
    }
    // endregion

}