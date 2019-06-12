# ImageCarousel
An image carousel for Android applications with built-in page indicators.

Implemented as a custom view that renders an image carousel leveraging ViewPager2.

Image sources can be drawable resource ids or
[Uri](https://developer.android.com/reference/android/net/Uri#parse(java.lang.String))
objects, and can be loaded from either populating a `CarouselImageList` either manually or through
`CarouselImageList.fromDrawableResList` or `CarouselImageList.fromUriList`
and calling `ImageCarousel.setImages` on the reference to the carousel that is in your layout.

Carousel current item indicators are also automatically created and kept in sync
with the carousel's ViewPager.


Has multiple defined attributes that you can specify in your XML layout:

1. `app:insetIndicators` - Boolean. Determines whether or not to inset the carousel item indicators.
2. `app:offsetIndicatorsBy` - Dimension (pixels) to offset the carousel item indicators from the ViewPager.
3. `app:indicatorCircleColor` - Color to tint all carousel item indicators.
4. `app:indicatorCircleSize` - Dimension (pixels) for the base size of all carousel item indicators.
5. `app:indicatorActiveScaleFactor` - Scale factor for the selected state of a carousel item indicator.
6. `app:indicatorCircleSpacing` - Dimension (pixels) for the total space in between carousel item indicators.

All of the above attributes can also be set through code using identically named accessors

E.g. `ImageCarousel.insetIndicators = false`

## Usage

#### In Layout XML
```xml
<com.meetarp.imagecarousel.ImageCarousel
        android:id="@+id/imageCarousel"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
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
```

And that's all you need to do.

## Known Bugs
* Vector Drawables do not work, since Picasso does not load vector drawables
    into ImageViews outside of placeholder or error states.
* Images that are drastically different in size can cause the Carousel to resize when
    Picasso needs to re-load them into a viewholder. This resizing can cause the disappearance
    of the carousel indicators.