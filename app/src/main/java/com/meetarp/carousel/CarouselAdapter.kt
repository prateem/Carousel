package com.meetarp.carousel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

internal class CarouselAdapter(private val context: Context) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    private var carouselImages: CarouselImageList? = null
    private var carouselViews: List<View>? = null

    @ColorInt private var errorTint: Int = ContextCompat.getColor(context, android.R.color.white)
    private var imageClickListener: Carousel.ImageClickListener? = null

    fun setImages(images: CarouselImageList) {
        carouselImages = images
        carouselViews = null
        notifyDataSetChanged()
    }

    fun setViews(views: List<View>) {
        carouselViews = views
        carouselImages = null
        notifyDataSetChanged()
    }

    fun setErrorTint(@ColorInt tint: Int) {
        errorTint = tint
    }

    fun setImageClickListener(listener: Carousel.ImageClickListener?) {
        imageClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.carousel_viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        return carouselImages?.getList()?.size ?: carouselViews?.size ?: 0
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.container.visibility = View.VISIBLE
        holder.progressBar.visibility = View.VISIBLE
        holder.error.visibility = View.GONE

        if (carouselViews != null) {
            holder.progressBar.visibility = View.GONE
            holder.container.removeAllViews()
            holder.container.addView(carouselViews!![position])
        } else {
            val imageSource = carouselImages!!.getList()[position]
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

            Picasso.get().let {
                if (imageSource is Int) {
                    it.load(imageSource).into(imageView, withCallback)
                } else {
                    it.load(imageSource as Uri).into(imageView, withCallback)
                }
            }

            imageView.setOnClickListener(null)
            if (imageClickListener != null) {
                imageView.setOnClickListener { imageClickListener?.onImageClicked(holder.adapterPosition) }
            }
        }
    }

    inner class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: FrameLayout = view.findViewById(R.id.viewContainer)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val error: ImageView = view.findViewById(R.id.error)
    }

}