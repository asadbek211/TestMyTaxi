package com.bizmiz.testtopshiriq.ui.home.orders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bizmiz.testtopshiriq.databinding.OrdersItemBinding
import javax.inject.Inject


class OrdersAdapter @Inject constructor() : RecyclerView.Adapter<OrdersAdapter.MyViewHolder>() {
    val startLocList: List<String> = listOf(
        "Шахрисабз, 20",
        "2 проезд Фаргона йули, 29",
        "5 тупик Фаргона йули, 13",
        "Абдурауфа Фитрата, 6а"
    )
    val dataList: List<String> = listOf(
        "22:54",
        "22:34",
        "21:34",
        "21:21"
    )
    val endLocList: List<String> = listOf(
        "Абдурауфа Фитрата, 83а",
        "Фаргона йули, 101",
        "2 проезд Фаргона йули, 29",
        "Мухтара Ашрафи, 34/1"
    )

    inner class MyViewHolder(private val binding: OrdersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(position: Int) {
            binding.tvStartLoc.text = startLocList[position]
            binding.tvData.text = dataList[position]
            binding.tvEndLoc.text = endLocList[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val ordersItemBinding =
            OrdersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(ordersItemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.populateModel(position)
    }

    override fun getItemCount(): Int = startLocList.size
}