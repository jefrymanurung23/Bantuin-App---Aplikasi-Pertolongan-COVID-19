package com.manurung.covidapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PencegahanAdapter (var pencegahanList: ArrayList<PencegahanModel>):RecyclerView.Adapter<PencegahanAdapter.ViewHolder>() {
    class ViewHolder (inflater: LayoutInflater, viewGroup: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_pencegahan, viewGroup, false)) {

        fun bind (pencegahanModel: PencegahanModel) {
            val pencegahanImage = itemView.findViewById<ImageView>(R.id.imgPencegahan)
            val pencegahanText = itemView.findViewById<TextView>(R.id.txtPencegahan)
            pencegahanImage.setImageResource(pencegahanModel.pencegahanImage)
            pencegahanText.text = pencegahanModel.pencegahanText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PencegahanAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return pencegahanList.size
    }

    override fun onBindViewHolder(holder: PencegahanAdapter.ViewHolder, position: Int) {
        val pencegahanModel = pencegahanList[position]
        holder.bind(pencegahanModel)
    }

}