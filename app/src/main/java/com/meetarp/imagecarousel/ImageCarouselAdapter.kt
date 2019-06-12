package com.meetarp.imagecarousel

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

internal class ImageCarouselAdapter(private val context: Context) : RecyclerView.Adapter<ImageCarouselAdapter.CarouselViewHolder>() {
    private var carouselImages: ImageCarousel.CarouselImageList = ImageCarousel.CarouselImageList()

    fun setImages(images: ImageCarousel.CarouselImageList) {
        carouselImages = images
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(
            ImageView(context)
                .apply { layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) }
        )
    }

    override fun getItemCount(): Int {
        return carouselImages.getList().size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val imageSource = carouselImages.getList()[position]
        with (holder.itemView as ImageView) {
            Picasso.get().let {
                if (imageSource is DrawableRes) {
                    it.load(imageSource as Int).into(this)
                } else {
                    it.load(imageSource as Uri).into(this)
                }
            }
        }
    }

    inner class CarouselViewHolder(imageView: ImageView) : RecyclerView.ViewHolder(imageView)

}