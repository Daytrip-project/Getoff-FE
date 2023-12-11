package com.example.getoff.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.getoff.dto.BusStop
import com.example.getoff.databinding.BusstopItemBinding
import com.example.getoff.response.ThirdResponse

class BusStopRViewAdapter(private val busStops: List<ThirdResponse.Response.Body.Items.Item>) : RecyclerView.Adapter<BusStopRViewAdapter.Holder>() {
    interface ItemClick {
        fun onClick(view : View, position : Int)
    }

    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStopRViewAdapter.Holder {
        val binding = BusstopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: BusStopRViewAdapter.Holder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position)
        }
        holder.busStopName.text = busStops[position].nodenm
        holder.locationName.text = busStops[position].routeid
    }

    override fun getItemCount(): Int {
        return busStops.size
    }

    inner class Holder(val binding: BusstopItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val busStopName = binding.busStopName
        val locationName = binding.locationName
    }
}