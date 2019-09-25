package com.meetarp.carousel.images

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.meetarp.carousel.adapters.CarouselAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class UriImage(private val uri: Uri) : CarouselImage() {

    override fun loadInto(
        imageView: ImageView,
        adapter: CarouselAdapter<CarouselImage>,
        viewHolder: CarouselAdapter<CarouselImage>.CarouselViewHolder
    ) {
        val withCallback = object : Callback {
            override fun onSuccess() {
                viewHolder.progressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                Log.v("Carousel", "Displaying error image due to failure loading desired image: ", e)
                viewHolder.container.visibility = View.GONE
                viewHolder.progressBar.visibility = View.GONE
                viewHolder.error.let {
                    it.visibility = View.VISIBLE
                    it.setColorFilter(adapter.errorTint)
                }
            }
        }

        viewHolder.container.post {
            Picasso.get()
                .load(uri)
                .resize(viewHolder.container.width, viewHolder.container.height)
                .onlyScaleDown()
                .centerInside()
                .into(imageView, withCallback)
        }
    }

}