package com.meetarp.carousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for [Carousel] with a default [RecyclerView.ViewHolder] implementation that includes
 * a loading spinner and a simple / default error image for use.
 */
abstract class CarouselAdapter<ItemType>
    : RecyclerView.Adapter<CarouselAdapter<ItemType>.CarouselViewHolder>() {

    /**
     * A [Carousel.ItemClickListener] that, if set, will be called by the default
     * view holder (if [onCreateViewHolder] is not overwritten) when a carousel item is clicked.
     */
    var itemClickListener: Carousel.ItemClickListener? = null

    protected var carouselItems: List<ItemType> = listOf()
    internal var itemChangeCallback: (() -> Unit)? = null

    /**
     * Return true if a data change event has been handled.
     * Default behaviour is to return false.
     * If false is returned, [notifyDataSetChanged] will be called.
     */
    open fun handleDataChange(
        oldData: List<ItemType>,
        newData: List<ItemType>
    ): Boolean = false

    /**
     * Set the items this adapter is responsible for displaying.
     */
    fun setItems(items: List<ItemType>) {
        if (carouselItems == items) return

        val oldItems = carouselItems
        carouselItems = items
        if (!handleDataChange(oldItems, items)) {
            notifyDataSetChanged()
        }
        itemChangeCallback?.invoke()
    }

    override fun getItemCount(): Int {
        return carouselItems.size
    }

    fun getDefaultCarouselViewHolder(parent: ViewGroup): DefaultCarouselViewHolder {
        return DefaultCarouselViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.carousel_viewholder, parent, false)
        ).also { holder -> holder.container.setOnClickListener(holder) }
    }

    abstract inner class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class DefaultCarouselViewHolder(view: View)
        : CarouselViewHolder(view), View.OnClickListener {
        val container: RelativeLayout = view.findViewById(R.id.viewContainer)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        override fun onClick(v: View) {
            val position = adapterPosition
            itemClickListener?.onItemClicked(v, position)
        }
    }

}