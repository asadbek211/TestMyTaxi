package com.bizmiz.kunlikishlar.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bizmiz.kunlikishlar.R
import com.bizmiz.kunlikishlar.databinding.JobItemBinding


class NewJobsAdapter : RecyclerView.Adapter<NewJobsAdapter.Myholder>() {
    private var onclick: (appealId: String) -> Unit = {}
    fun onClickListener(onclick: (appealId: String) -> Unit) {
        this.onclick = onclick
    }

    val listImage: ArrayList<Int> = arrayListOf(
        R.drawable.job1,
        R.drawable.job2,
        R.drawable.job3,
        R.drawable.job5
    )

    inner class Myholder(private val binding: JobItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val listName: List<String> = listOf(
            "Payvandchilarga kunlik ish bor narxi kelishiladi",
            "Quruvchi kerak Ichki va tashqi qurilish ishlari",
            "Ofitsiantlikga ishga olamiz",
            "Oshxonaga oshpazga yordamchi kerak"
        )
        private val dataList: List<String> = listOf(
            "Bugun,12:54",
            "Bugun,10:32",
            "Kecha,22:00",
            "12-Yanvar"
        )
        private val locationList: List<String> = listOf(
            "Amudaryo tumani",
            "Nukus shahri",
            "Nukus shahri",
            "Beruniy Tumani"
        )

        fun populateModel(position: Int) {
            binding.catImage.setImageResource(listImage[position])
            binding.catName.text = listName[position]
            binding.tvData.text = dataList[position]
            binding.tvLocation.text = locationList[position]
//            binding.homeCard.setOnClickListener {
//                onclick.invoke(listName[position])
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myholder {
        val jobItemBinding =
            JobItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Myholder(jobItemBinding)
    }

    override fun onBindViewHolder(holder: Myholder, position: Int) {
        holder.populateModel(position)
    }

    override fun getItemCount(): Int = listImage.size
}