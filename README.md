# ImageCarousel
An image carousel for Android applications with built-in page indicators.

Implemented as a custom view that renders an image carousel leveraging ViewPager2.

Image sources can be drawable resource ids or
[Uri](https://developer.android.com/reference/android/net/Uri#parse(java.lang.String))
objects. They can be loaded from populating a `CarouselImageList` either manually or through
one of the two builder methods: `CarouselImageList.fromDrawableResList` and `CarouselImageList.fromUriList`.
Once a populated image list is available, call `ImageCarousel.setImages` on a reference to the ImageCarousel.

Carousel current item indicators are also automatically created and kept in sync
with the carousel's ViewPager.

Has multiple defined attributes that you can specify in your XML layout:

|XML Attribute|Description|Default|
|-------------|-----------|-------|
|`app:carouselBackgroundColor`|The color that will be applied to the background of the carousel, if visible.|android.R.color.transparent|
|`app:insetIndicators`|Boolean. Determines whether or not to inset the carousel item indicators.|true|
|`app:offsetIndicatorsBy`|Dimension (pixels) to offset the carousel item indicators from the ViewPager.|16dp|
|`app:indicatorCircleColor`|Color to tint all carousel item indicators.|android.R.color.white|
|`app:indicatorCircleSize`|Dimension (pixels) for the base size of all carousel item indicators.|5dp|
|`app:indicatorActiveScaleFactor`|Scale factor for the selected state of a carousel item indicator.|1.8|
|`app:indicatorCircleSpacing`|Dimension (pixels) for the total space in between carousel item indicators.|10dp|

## Usage

**Note**: It is _highly_ recommended that you constrain the height of the ImageCarousel so
that the viewpager does not resize itself when loading images of drastically different sizes.

#### In Layout XML
```xml
<!-- Shown with all of the default values as described in the table above -->
<com.meetarp.imagecarousel.ImageCarousel
        android:id="@+id/imageCarousel"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:carouselBackgroundColor="@android:color/transparent"
        app:insetIndicators="false"
        app:offsetIndicatorsBy="16dp"
        app:indicatorCircleColor="@android:color/white"
        app:indicatorCircleSize="5dp"
        app:indicatorActiveScaleFactor="1.8"
        app:indicatorCircleSpacing="10dp" />
```

#### In Activity/Fragment
```kotlin
// Capture the reference to the carousel
val carousel: ImageCarousel = findViewById(R.id.imageCarousel)

// Populate the image list with drawables or Uri objects
val images = CarouselImageList()
images.add(R.drawable.image1)
images.add(Uri.parse("https://www.example.com/image2.png"))
images.add(R.drawable.image3)

// Let the carousel know you're ready
carousel.setImages(images)

// All of the xml attributes can also be set through code using identically named accessors
carousel.carouselBackgroundColor = ContextCompat.getColor(context, R.color.grey)
carousel.insetIndicators = false
carousel.offsetIndicatorsBy = dpToPx(20f).toInt()
carousel.indicatorCircleColor = ContextCompat.getColor(context, R.color.royal_blue)
carousel.indicatorCircleSize = dpToPx(8f).toInt()
carousel.indicatorActiveScaleFactor = 1.5f
carousel.indicatorCircleSpacing = dpToPx(12f).toInt()
```

And that's all you need to do.

## TODO
* Allow click listeners for carousel images.

## Known Bugs
* Vector Drawables do not work, since Picasso does not load vector drawables
    into ImageViews outside of placeholder or error states.
* Images that are drastically different in size can cause the Carousel to resize when
    Picasso needs to re-load them into a viewholder. This only happens when the carousel
    height is not explicitly defined (i.e. `wrap_contents`) and constituent image heights
    are not equal.