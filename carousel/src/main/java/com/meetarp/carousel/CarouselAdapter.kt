package com.meetarp.carousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView

abstract class CarouselAdapter<ItemType>
    : RecyclerView.Adapter<CarouselAdapter<ItemType>.CarouselViewHolder>() {

    private var carouselItems: List<ItemType> = listOf()
    private var itemClickListener: Carousel.ItemClickListener? = null

    open fun handleDataChange(
        oldData: List<ItemType>,
        newData: List<ItemType>
    ): Boolean = false

    fun setItems(items: List<ItemType>) {
        val oldItems = carouselItems
        carouselItems = items
        if (!handleDataChange(oldItems, items)) {
            notifyDataSetChanged()
        }
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
        bindItemForPosition(holder, position, carouselItems[position])
    }

    /**
     * Bind the item for the given position. The [holder] has two properties:
     *
     * * container: [RelativeLayout] to place content into.
     * * progressBar: [ProgressBar] to show indeterminate progress status
     *
     * @param holder The ViewHolder for the carousel position.
     * @param position The carousel position.
     * @param item The item corresponding to the carousel position.
     */
    abstract fun bindItemForPosition(
        holder: CarouselViewHolder,
        position: Int,
        item: ItemType
    )

    inner class CarouselViewHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {
        val container: RelativeLayout = view.findViewById(R.id.viewContainer)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        override fun onClick(v: View?) {
            val position = adapterPosition
            itemClickListener?.onItemClicked(container, position)
        }
    }

}