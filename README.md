# Carousel for Android
A carousel for Android applications with built-in page indicators.

Implemented as a custom view that renders a carousel leveraging ViewPager2, with an
extensible Adapter system that allows you to build the carousel you want.

Comes with two built-in adapters to quick-start

* `CarouselImagesAdapter` for drawables or image resources from within the app
* `CarouselViewsAdapter` for a simple, generic View adapter.

Simply instantiate the Carousel in your activity or fragment, create the adapter, and attach it.
Everything else is handled for you.

Currently active item indicators are also automatically created and kept in sync
with the carousel's ViewPager.

Has multiple defined attributes that you can specify in your XML layout:

|XML Attribute|Description|Default|
|-------------|-----------|-------|
|`app:carousel_backgroundColor`|`@ColorInt` The color that will be applied to the background of the carousel, if visible.|android.R.color.transparent|
|`app:carousel_insetIndicators`|Boolean. Determines whether or not to inset the carousel item indicators.|true|
|`app:carousel_offsetIndicatorsBy`|`@Dimension` Dimension (pixels) representing the distance between the bottom of the viewpager and the closest edge of the indicator container (bottom edge if indicators are inset, top edge if outset)|16dp|
|`app:carousel_indicatorColor`|`@ColorInt` Color to tint all carousel item indicators.|android.R.color.white|
|`app:carousel_indicatorSize`|`@Dimension` Dimension (pixels) for the base size of all carousel item indicators.|5dp|
|`app:carousel_indicatorSpacing`|`@Dimension` Dimension (pixels) for the total space in between carousel item indicators.|10dp|
|`app:carousel_indicatorActiveScaleFactor`|Scale factor for the selected state of a carousel item indicator.|1.8|

## Usage

**Note**: It is _highly_ recommended that you constrain the height of the Carousel so
that the viewpager does not resize itself when loading items of drastically different sizes.
What that means for the sizing controls of the views you pass in is up to you; it is
recommended that they be of similar (if not identical) heights.

#### In Layout XML
```xml
<!-- Shown with all of the default values as described in the table above -->
<com.meetarp.carousel.Carousel
        android:id="@+id/carousel"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:carousel_backgroundColor="@android:color/transparent"
        app:carousel_insetIndicators="true"
        app:carousel_offsetIndicatorsBy="16dp"
        app:carousel_indicatorColor="@android:color/white"
        app:carousel_indicatorSize="5dp"
        app:carousel_indicatorSpacing="10dp"
        app:carousel_indicatorActiveScaleFactor="1.8" />
```

#### In Activity/Fragment
```kotlin
// Capture the reference to the carousel
val imageResCarousel: Carousel<Int> = findViewById(R.id.carousel)

// Populate the image list with drawables
val images = mutableListOf<Int>()
images.add(R.drawable.image1)
images.add(R.drawable.image2)
images.add(R.drawable.image3)

// Create the adapter and set the items
val imagesAdapter = CarouselImagesAdapter(context)
imagesAdapter.setItems(images)

// Attach a click listener, if you want.
imagesAdapter.setItemClickListener(object : Carousel.ItemClickListener {
    override fun onItemClicked(view: View, position: Int) {
        // .. do something
    }
})

// Give the carousel the adapter
imageResCarousel.adapter = imagesAdapter

// All of the xml attributes can also be set through code using identically named accessors
carousel.carouselBackgroundColor = ContextCompat.getColor(context, R.color.grey)
carousel.insetIndicators = false
carousel.offsetIndicatorsBy = dpToPx(20f).toInt()
carousel.indicatorColor = ContextCompat.getColor(context, R.color.royal_blue)
carousel.indicatorSize = dpToPx(8f).toInt()
carousel.indicatorSpacing = dpToPx(12f).toInt()
carousel.indicatorActiveScaleFactor = 1.5f
```

And that's all you need to do.

## Known Bugs
* Vector Drawables do not work, since Picasso does not load vector drawables
    into ImageViews outside of placeholder or error states.
* Images that are drastically different in size can cause the Carousel to resize when
    Picasso needs to re-load them into a viewholder. This only happens when the carousel
    height is not explicitly defined (i.e. `wrap_contents`) and constituent image heights
    are not equal.
    
![Carousel example](carousel.webm)