package com.daytrip.getoff.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daytrip.getoff.dto.BusStop
import com.daytrip.getoff.databinding.BusstopItemBinding

class BusStopRViewAdapter(private val busStops: MutableList<BusStop>) : RecyclerView.Adapter<BusStopRViewAdapter.Holder>() {
    interface ItemClick {
        fun onClick(view : View, position : Int)
    }

    var itemClick : ItemClick? = null
    private var selectedItem = -1

    fun setSelectedItem(idx: Int) {
        selectedItem = idx
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStopRViewAdapter.Holder {
        val binding = BusstopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: BusStopRViewAdapter.Holder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position)
        }
        holder.busStopName.text = busStops[position].name
        holder.locationName.text = busStops[position].routeId
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return busStops.size
    }

    inner class Holder(val binding: BusstopItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val busStopName = binding.busStopName
        val locationName = binding.locationName

        fun bind(position: Int) {
            if (selectedItem == position) {
                itemView.setBackgroundColor(Color.RED)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}