package com.manurung.covidapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GejalaAdapter (var gejalaList: ArrayList<GejalaModel>):RecyclerView.Adapter<GejalaAdapter.ViewHolder>() {
    class ViewHolder (inflater: LayoutInflater, viewGroup: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_gejala, viewGroup, false)) {

        fun bind (gejalaModel: GejalaModel) {
            val gejalaImage = itemView.findViewById<ImageView>(R.id.imgGejala)
            val gejalaText = itemView.findViewById<TextView>(R.id.txtGejala)
            val gejalaTextDetail = itemView.findViewById<TextView>(R.id.txtGejalaDetail)
            gejalaImage.setImageResource(gejalaModel.gejalaImage)
            gejalaText.text = gejalaModel.gejalaText
            gejalaTextDetail.text = gejalaModel.gejalaDetail
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GejalaAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return gejalaList.size
    }

    override fun onBindViewHolder(holder: GejalaAdapter.ViewHolder, position: Int) {
        val gejalaModel = gejalaList[position]
        holder.bind(gejalaModel)
    }

}