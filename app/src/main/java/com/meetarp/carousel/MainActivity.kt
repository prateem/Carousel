package com.meetarp.carousel

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupImagesCarousel()
        setupViewsCarousel()
    }

    private fun setupImagesCarousel() {
        val imagesCarousel = findViewById<Carousel<Int>>(R.id.carousel_images_test)
        imagesCarousel.insetIndicators = false

        // Photos from Unsplash by:
        //     Vlad Tchompalov
        //     Jairo Alzate
        //     Daryan Shamkhali
        val carouselImages = mutableListOf<Int>()

        carouselImages.add(R.drawable.ant_vlad_tchompalov_unsplash)
        carouselImages.add(R.drawable.puppy_jairo_alzate_unsplash)
        carouselImages.add(R.drawable.city_daryan_shamkhali_unsplash)

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

        val views = mutableListOf<View>()
        views.add(ImageView(this).also { it.setImageResource(R.drawable.puppy_jairo_alzate_unsplash) })
        views.add(TextView(this).also { it.text = "Hello world!" })
        views.add(Button(this).also { it.text = "Interesting" })

        val adapter = CarouselViewsAdapter(this)
        adapter.setItems(views)
        viewsCarousel.adapter = adapter
    }
}
