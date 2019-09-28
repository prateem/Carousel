package com.meetarp.carousel

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.meetarp.carousel.adapters.CarouselImagesAdapter
import com.meetarp.carousel.adapters.CarouselViewsAdapter
import com.meetarp.carousel.images.CarouselImage
import com.meetarp.carousel.images.ResourceImage
import com.meetarp.carousel.images.UriImage

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupImagesCarousel()
        setupViewsCarousel()
    }

    private fun setupImagesCarousel() {
        val imagesCarousel = findViewById<Carousel<CarouselImage>>(R.id.carousel_images_test)
        imagesCarousel.insetIndicators = true

        // Photos from Unsplash by:
        //     Joanna Kosinska
        //     Vlad Tchompalov
        //     Jairo Alzate
        //     Daryan Shamkhali
        val carouselImages = mutableListOf<CarouselImage>()

        carouselImages.add(UriImage(Uri.parse("https://raw.githubusercontent.com/prateem/ImageCarousel/master/app/src/main/res/raw/remote_image.jpg")))
        carouselImages.add(ResourceImage(R.drawable.ant_vlad_tchompalov_unsplash))
        carouselImages.add(ResourceImage(R.drawable.puppy_jairo_alzate_unsplash))
        carouselImages.add(ResourceImage(R.drawable.city_daryan_shamkhali_unsplash))
        carouselImages.add(UriImage(Uri.parse("https://raw.githubusercontent.com/prateem/ImageCarousel/master/app/src/main/res/raw/does_not_exit.jpg")))

        val adapter = CarouselImagesAdapter(this)
        adapter.setItems(carouselImages)
        adapter.setItemClickListener(object : Carousel.ItemClickListener {
            override fun onItemClicked(view: View, position: Int) {
                Log.d("Carousel", "Position $position clicked")
            }
        })

        imagesCarousel.adapter = adapter
    }

    private fun setupViewsCarousel() {
        val viewsCarousel = findViewById<Carousel<View>>(R.id.carousel_view_test)
        viewsCarousel.insetIndicators = false
        viewsCarousel.indicatorPosition = Carousel.IndicatorPosition.BOTTOM

        val views = mutableListOf<View>()
        views.add(ImageView(this).also { it.setImageResource(R.drawable.puppy_jairo_alzate_unsplash) })
        views.add(TextView(this).also { it.text = "Hello world!" })
        views.add(
                Button(this).also {
                    it.text = "Interesting"
                    it.setOnClickListener {
                        Toast.makeText(this, "Button clicked", Toast.LENGTH_LONG)
                                .show()
                    }
                }
        )

        val adapter = CarouselViewsAdapter(this)
        adapter.setItems(views)
        viewsCarousel.adapter = adapter
    }

}
