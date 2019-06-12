package com.meetarp.imagecarousel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

internal class ImageCarouselAdapter(context: Context) : RecyclerView.Adapter<ImageCarouselAdapter.CarouselViewHolder>() {
    private var carouselImages: CarouselImageList = CarouselImageList()
    @ColorInt private var errorTint: Int = ContextCompat.getColor(context, android.R.color.white)

    fun setImages(images: CarouselImageList) {
        carouselImages = images
        notifyDataSetChanged()
    }

    fun setErrorTint(@ColorInt tint: Int) {
        errorTint = tint
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.carousel_viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        return carouselImages.getList().size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.image.visibility = View.VISIBLE
        holder.progressBar.visibility = View.VISIBLE
        holder.error.visibility = View.GONE

        val imageSource = carouselImages.getList()[position]
        val withCallback = object : Callback {
            override fun onSuccess() {
                holder.progressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                Log.v("ImageCarousel", "Displaying error image due to failure loading desired image: ", e)
                holder.image.visibility = View.GONE
                holder.progressBar.visibility = View.GONE
                holder.error.let {
                    it.visibility = View.VISIBLE
                    it.setColorFilter(errorTint)
                }
            }
        }

        with (holder.image) {
            Picasso.get().let {
                if (imageSource is Int) {
                    it.load(imageSource).into(this, withCallback)
                } else {
                    it.load(imageSource as Uri).into(this, withCallback)
                }
            }
        }
    }

    inner class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.carouselImage)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val error: ImageView = view.findViewById(R.id.error)
    }

}