package com.meetarp.imagecarousel

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val carousel = findViewById<ImageCarousel>(R.id.carousel_test)
        carousel.setImagesFromUriList(listOf(
            Uri.parse("https://fsa.zobj.net/crop.php?r=7bA18pB6HgnNI1uCbpE9TuAE_j1kdrSGH76BfLzQTSfmLFhS0lCiSkGnCYXkgGpvyRalh66ln2-x1ogy4wnrWrsdXeEWTWOjaNpOQzCO4SVKbcwiyp9WVVTKnFMPMbkK4XhNyvl4BCE3ZViq"),
            Uri.parse("https://images.ctfassets.net/kn3y2dwjjr07/ka2XHE8D9kbyMVc0tJu19/9b048b84de3b75ab23ea6249c8cb2036/4051523_iphone7__color_silver_298604__render14.png.560x560-w.m80.jpg"),
            Uri.parse("https://images.ctfassets.net/kn3y2dwjjr07/3P4odXAUAGm9lhRAiutCp8/6fe1edb18f46af7caa6537813d9e2719/4051523_iphone7_298604.png.560x560-w.m80.jpg"),
            Uri.parse("https://images.ctfassets.net/kn3y2dwjjr07/6Y5L2z9c7VnkWVO1qJJmsb/f823f6d828775db7cec6a66fe1bf789f/case_color_preview_298604.jpg"),
            Uri.parse("https://images.ctfassets.net/kn3y2dwjjr07/6DVsIRY3Uujs6wUw9w7OWh/28d04e18e2a938bade7141aa5c1fa742/side_color_preview_298604.jpg")
        ))
        carousel.insetIndicators = false
    }
}
