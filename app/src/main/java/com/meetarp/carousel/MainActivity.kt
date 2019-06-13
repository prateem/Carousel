package com.meetarp.carousel

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val carousel = findViewById<Carousel>(R.id.carousel_test)
        carousel.insetIndicators = true

        // Photos from Unsplash by:
        //     Joanna Kosinska
        //     Vlad Tchompalov
        //     Jairo Alzate
        //     Daryan Shamkhali
        val carouselImages = CarouselImageList()

        carouselImages.add(Uri.parse("https://raw.githubusercontent.com/prateem/ImageCarousel/master/app/src/main/res/raw/remote_image.jpg"))
        carouselImages.add(R.drawable.ant_vlad_tchompalov_unsplash)
        carouselImages.add(R.drawable.puppy_jairo_alzate_unsplash)
        carouselImages.add(R.drawable.city_daryan_shamkhali_unsplash)
        carouselImages.add(Uri.parse("https://raw.githubusercontent.com/prateem/ImageCarousel/master/app/src/main/res/raw/does_not_exist.jpg"))

        carousel.ofImages(carouselImages)
        carousel.setImageClickListener(object : Carousel.ImageClickListener {
            override fun onImageClicked(position: Int) {
                Log.d("Carousel", "Position $position clicked")
            }
        })
    }
}
