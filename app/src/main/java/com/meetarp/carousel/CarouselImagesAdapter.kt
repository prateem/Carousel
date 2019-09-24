package com.meetarp.carousel

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class CarouselImagesAdapter(private val context: Context)
    : CarouselAdapter<Int>(context) {

    override fun bindItemForPosition(holder: CarouselViewHolder, position: Int) {
        val imageResource = carouselItems[position]

        val withCallback = object : Callback {
            override fun onSuccess() {
                holder.progressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                Log.v("Carousel", "Displaying error image due to failure loading desired image: ", e)
                holder.container.visibility = View.GONE
                holder.progressBar.visibility = View.GONE
                holder.error.let {
                    it.visibility = View.VISIBLE
                    it.setColorFilter(errorTint)
                }
            }
        }

        val imageView: ImageView
        if (holder.container.childCount > 0 && holder.container.getChildAt(0) is ImageView) {
            imageView = holder.container.getChildAt(0) as ImageView
        } else {
            imageView = ImageView(context)
            holder.container.removeAllViews()
            holder.container.addView(imageView)
        }

        holder.container.post {
            Picasso.get()
                .load(imageResource)
                .resize(holder.container.width, holder.container.height)
                .onlyScaleDown()
                .centerInside()
                .into(imageView, withCallback)
        }
    }

}