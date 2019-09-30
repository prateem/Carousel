package com.meetarp.carousel

/**
 * MIT License
 * Copyright (c) 2019 Prateem Shrestha
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.FrameLayout
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
 * any item content you wish. There are two pre-built implementations for images
 * and generic views: CarouselImagesAdapter and CarouselViewsAdapter
 *
 * Also automatically creates item indicators to denote the current position of the ViewPager.
 *
 * @author Prateem Shrestha (http://www.github.com/prateem)
 */
@Suppress("MemberVisibilityCanBePrivate")
class Carousel<ItemType> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    enum class IndicatorPosition { TOP, BOTTOM, START, END }

    interface ItemClickListener {
        fun onItemClicked(container: ViewGroup, position: Int)
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
            adapter?.let {
                post {
                    setIndicators()
                    updateOrientation()
                    updateConstraints()
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
    @ColorInt
    var carouselBackgroundColor: Int = ContextCompat.getColor(context, android.R.color.transparent)
        set(value) {
            field = value
            updateViewPagerBackground()
        }

    /**
     * Whether to show or hide the paging indicators.
     * Default: true
     */
    var showIndicators: Boolean = true
        set(value) {
            field = value
            setIndicators()
            updateConstraints()
        }

    /**
     * What position to place the indicators relative to the carousel.
     *
     * This changes the carousel behaviour as well.
     * [IndicatorPosition.TOP] and [IndicatorPosition.BOTTOM] will cause horizontal scrolling.
     * [IndicatorPosition.START] and [IndicatorPosition.END] will cause vertical scrolling.
     *
     * Default: [IndicatorPosition.BOTTOM]
     */
    var indicatorPosition: IndicatorPosition = IndicatorPosition.BOTTOM
        set(value) {
            field = value
            setIndicators()
            updateOrientation()
            updateConstraints()
            updateViewPadding()
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
    @Dimension(unit = Dimension.PX)
    var indicatorOffset: Int = context.resources.getDimensionPixelOffset(R.dimen.default_indicator_offset)
        set(value) {
            field = value
            updateConstraints()
        }

    /**
     * The tint/color to apply to each individual page indicator.
     * Default: [android.R.color.white]
     */
    @ColorInt
    var indicatorColor: Int = ContextCompat.getColor(context, android.R.color.white)
        set(value) {
            field = value
            updateAllIndicatorAttributes()
        }

    /**
     * The size of each individual page indicator, in pixels.
     * Default: 5dp.
     */
    @Dimension(unit = Dimension.PX)
    var indicatorSize: Int = context.resources.getDimensionPixelSize(R.dimen.default_indicator_size)
        set(value) {
            field = value
            updateAllIndicatorAttributes()
        }

    /**
     * The total space in between each individual page indicator, in pixels.
     * Default: 10dp.
     */
    @Dimension
    var indicatorSpacing: Int = context.resources.getDimensionPixelOffset(R.dimen.default_indicator_spacing)
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
    var pageChangeListener: ViewPager2.OnPageChangeCallback? = null
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
            carouselBackgroundColor = attributes.getColor(
                R.styleable.Carousel_carousel_backgroundColor,
                carouselBackgroundColor
            )
            insetIndicators = attributes.getBoolean(
                R.styleable.Carousel_carousel_insetIndicators,
                insetIndicators
            )
            indicatorOffset = attributes.getDimensionPixelOffset(
                R.styleable.Carousel_carousel_indicatorOffset,
                indicatorOffset
            )

            showIndicators =
                attributes.getBoolean(R.styleable.Carousel_carousel_showIndicators, showIndicators)
            indicatorPosition = IndicatorPosition.values()[
                    attributes.getInt(R.styleable.Carousel_carousel_indicatorPosition, 1)
            ]

            indicatorColor =
                attributes.getColor(R.styleable.Carousel_carousel_indicatorColor, indicatorColor)
            indicatorSize = attributes.getDimensionPixelSize(
                R.styleable.Carousel_carousel_indicatorSize,
                indicatorSize
            )
            indicatorSpacing = attributes.getDimensionPixelOffset(
                R.styleable.Carousel_carousel_indicatorSpacing,
                indicatorSpacing
            )
            indicatorActiveScaleFactor = attributes.getFloat(
                R.styleable.Carousel_carousel_indicatorActiveScaleFactor,
                indicatorActiveScaleFactor
            )
            attributes.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                pageChangeListener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                // Can get onPageSelected called when only 1 image is available
                // In that case, no indicators are added, so prevent a NPE when calling getChildAt()
                if (position != -1 && indicatorContainer.childCount > 0) {
                    animateIndicator(
                        indicatorContainer.getChildAt(previousActiveIndex),
                        reverse = true
                    )
                    animateIndicator(indicatorContainer.getChildAt(position))
                    previousActiveIndex = position
                }
                pageChangeListener?.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                pageChangeListener?.onPageScrollStateChanged(state)
            }
        })

        // Make sure circle page indicators don't get clipped by padding
        clipChildren = false
        clipToPadding = false

        // Now that all the setup is done, actually attach the component.
        addView(component)
        updateViewPadding()
        updateViewPagerBackground()
        updateOrientation()
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
        val padding =
            if (!insetIndicators)
                (indicatorSize * indicatorActiveScaleFactor / 2).toInt()
            else 0

        when (indicatorPosition) {
            IndicatorPosition.TOP -> setPaddingRelative(0, padding, 0, 0)
            IndicatorPosition.BOTTOM -> setPaddingRelative(0, 0, 0, padding)
            IndicatorPosition.START -> setPaddingRelative(padding, 0, 0, 0)
            IndicatorPosition.END -> setPaddingRelative(0, 0, padding, 0)
        }

    }

    private fun updateViewPagerBackground() {
        viewPager.setBackgroundColor(carouselBackgroundColor)
    }
    // endregion

    // region helper methods
    fun goTo(pos: Int) {
        viewPager.post { viewPager.setCurrentItem(pos, true) }
    }

    private fun updateOrientation() {
        indicatorContainer.orientation =
            if (isHorizontalPaging())
                LinearLayout.HORIZONTAL
            else LinearLayout.VERTICAL
        viewPager.orientation =
            if (isHorizontalPaging())
                ViewPager2.ORIENTATION_HORIZONTAL
            else ViewPager2.ORIENTATION_VERTICAL
    }

    private fun setIndicators() {
        indicatorContainer.removeAllViews()

        if (showIndicators) {
            adapter?.itemCount?.let { count ->
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
            }
        } else {
            indicatorContainer.visibility = View.GONE
        }
    }

    private fun updateIndicatorAttributes(
        indicator: View,
        indicatorPosition: Int,
        isLast: Boolean = false
    ) {
        if (indicator.background == null) {
            indicator.background =
                ContextCompat.getDrawable(context, R.drawable.carousel_item_indicator)
        }
        indicator.background.setTint(indicatorColor)
        indicator.layoutParams = LayoutParams(indicatorSize, indicatorSize)
            .also { lp ->
                if (isHorizontalPaging()) {
                    lp.setMargins(
                        if (indicatorPosition == 0) 0 else (indicatorSpacing / 2),
                        0,
                        if (isLast) 0 else (indicatorSpacing / 2),
                        0
                    )
                } else {
                    lp.setMargins(
                        0,
                        if (indicatorPosition == 0) 0 else (indicatorSpacing / 2),
                        0,
                        if (isLast) 0 else (indicatorSpacing / 2)
                    )
                }
            }
    }

    private fun updateConstraints() {
        ConstraintSet().also { constraints ->
            constraints.clone(component)

            // Go back to a 'reset' state on the actual ViewPager.
            constraints.connect(
                R.id.carouselPager, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START,
                0
            )
            constraints.connect(
                R.id.carouselPager, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END,
                0
            )
            constraints.connect(
                R.id.carouselPager, ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP,
                0
            )
            constraints.connect(
                R.id.carouselPager, ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                0
            )

            // Set up the indicator container for edges that are defined by scroll direction
            if (isHorizontalPaging()) {
                constraints.connect(
                    R.id.indicatorContainer, ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START,
                    0
                )
                constraints.connect(
                    R.id.indicatorContainer, ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END,
                    0
                )
                constraints.constrainWidth(R.id.indicatorContainer, ConstraintSet.MATCH_CONSTRAINT)
                constraints.constrainHeight(R.id.indicatorContainer, ConstraintSet.WRAP_CONTENT)
            } else {
                constraints.connect(
                    R.id.indicatorContainer, ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID, ConstraintSet.TOP,
                    0
                )
                constraints.connect(
                    R.id.indicatorContainer, ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                    0
                )
                constraints.constrainWidth(R.id.indicatorContainer, ConstraintSet.WRAP_CONTENT)
                constraints.constrainHeight(R.id.indicatorContainer, ConstraintSet.MATCH_CONSTRAINT)
            }

            if (indicatorContainer.childCount > 0) {
                when (indicatorPosition) {
                    IndicatorPosition.START -> {
                        if (insetIndicators) {
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.START,
                                ConstraintSet.PARENT_ID, ConstraintSet.START,
                                indicatorOffset
                            )
                            constraints.clear(R.id.indicatorContainer, ConstraintSet.END)
                        } else {
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.START,
                                ConstraintSet.PARENT_ID, ConstraintSet.START,
                                0
                            )
                            constraints.connect(
                                R.id.carouselPager, ConstraintSet.START,
                                R.id.indicatorContainer, ConstraintSet.END,
                                0
                            )
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.END,
                                R.id.carouselPager, ConstraintSet.START,
                                indicatorOffset
                            )
                        }
                    }
                    IndicatorPosition.END -> {
                        if (insetIndicators) {
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.END,
                                ConstraintSet.PARENT_ID, ConstraintSet.END,
                                indicatorOffset
                            )
                            constraints.clear(R.id.indicatorContainer, ConstraintSet.START)
                        } else {
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.END,
                                ConstraintSet.PARENT_ID, ConstraintSet.END,
                                0
                            )
                            constraints.connect(
                                R.id.carouselPager, ConstraintSet.END,
                                R.id.indicatorContainer, ConstraintSet.START,
                                0
                            )
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.START,
                                R.id.carouselPager, ConstraintSet.END,
                                indicatorOffset
                            )
                        }
                    }
                    IndicatorPosition.TOP -> {
                        if (insetIndicators) {
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.TOP,
                                ConstraintSet.PARENT_ID, ConstraintSet.TOP,
                                indicatorOffset
                            )
                            constraints.clear(R.id.indicatorContainer, ConstraintSet.BOTTOM)
                        } else {
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.TOP,
                                ConstraintSet.PARENT_ID, ConstraintSet.TOP,
                                0
                            )
                            constraints.connect(
                                R.id.carouselPager, ConstraintSet.TOP,
                                R.id.indicatorContainer, ConstraintSet.BOTTOM,
                                0
                            )
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.BOTTOM,
                                R.id.carouselPager, ConstraintSet.TOP,
                                indicatorOffset
                            )
                        }
                    }
                    else -> {
                        // default case
                        assert(indicatorPosition == IndicatorPosition.BOTTOM)
                        if (insetIndicators) {
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.BOTTOM,
                                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                                indicatorOffset
                            )
                            constraints.clear(R.id.indicatorContainer, ConstraintSet.TOP)
                        } else {
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.BOTTOM,
                                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                                0
                            )
                            constraints.connect(
                                R.id.carouselPager, ConstraintSet.BOTTOM,
                                R.id.indicatorContainer, ConstraintSet.TOP,
                                0
                            )
                            constraints.connect(
                                R.id.indicatorContainer, ConstraintSet.TOP,
                                R.id.carouselPager, ConstraintSet.BOTTOM,
                                indicatorOffset
                            )
                        }
                    }
                }
            } else {
                constraints.connect(
                    R.id.carouselPager, ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                    0
                )
            }
        }.applyTo(component)
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

    private fun isHorizontalPaging(): Boolean {
        return indicatorPosition in listOf(IndicatorPosition.TOP, IndicatorPosition.BOTTOM)
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