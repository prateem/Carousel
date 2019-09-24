package com.meetarp.carousel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

abstract class CarouselAdapter<ItemType>(context: Context)
    : RecyclerView.Adapter<CarouselAdapter<ItemType>.CarouselViewHolder>() {

    protected var carouselItems: List<ItemType> = listOf()
    private var itemClickListener: Carousel.ItemClickListener? = null

    @ColorInt
    var errorTint: Int = ContextCompat.getColor(context, android.R.color.white)

    fun setItems(items: List<ItemType>) {
        carouselItems = items
        notifyDataSetChanged()
    }

    fun setItemClickListener(listener: Carousel.ItemClickListener?) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.carousel_viewholder, parent, false)
        ).also { holder -> holder.container.setOnClickListener(holder) }
    }

    override fun getItemCount(): Int {
        return carouselItems.size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.container.visibility = View.VISIBLE
        holder.progressBar.visibility = View.VISIBLE
        holder.error.visibility = View.GONE
        bindItemForPosition(holder, position)
    }

    /**
     * Bind the item for the given position. The [holder] has three properties:
     *
     * * container: [RelativeLayout] to place a view into.
     * * progressBar: [ProgressBar] to show indeterminate progress status
     * * error: [ImageView] to show an error indicator
     */
    abstract fun bindItemForPosition(holder: CarouselViewHolder, position: Int)

    inner class CarouselViewHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {
        val container: RelativeLayout = view.findViewById(R.id.viewContainer)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val error: ImageView = view.findViewById(R.id.error)

        override fun onClick(v: View?) {
            val position = adapterPosition
            itemClickListener?.onItemClicked(container.getChildAt(0), position)
        }
    }

}