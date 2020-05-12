package com.manurung.covidapp

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class InfoAdapter (var infoList: ArrayList<InfoModel>):RecyclerView.Adapter<InfoAdapter.ViewHolder>() {

    class ViewHolder (inflater: LayoutInflater, viewGroup: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_info, viewGroup, false)) {

        fun bind (infoModel: InfoModel) {
            val itemInfo = itemView.findViewById<LinearLayout>(R.id.item_info_linearlayout)
            val infoCountrFlag = itemView.findViewById<ImageView>(R.id.imageFlag)
            val tvCountryName = itemView.findViewById<TextView>(R.id.tvCountryName)
            val COVID_COLOR = listOf(
                ColorTemplate.rgb("#E91E63"),  //merah
                ColorTemplate.rgb("#009688"),  //hijau
                ColorTemplate.rgb("#868ea5") //abu-abu
            )

            val dialog = Dialog(itemInfo.context)
            dialog.setContentView(R.layout.dialog_info)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            tvCountryName.text = infoModel.country
            Glide.with(itemInfo).load(infoModel.flag).into(infoCountrFlag)

            itemInfo.setOnClickListener ( View.OnClickListener {
                val chart: PieChart = dialog.findViewById<PieChart>(R.id.pieChart)

                chart.setUsePercentValues(false)
                chart.description.isEnabled = false
                chart.setExtraOffsets(5f, 10f, 5f, 5f)

                chart.dragDecelerationFrictionCoef = 0.95f

                chart.setExtraOffsets(15f, 0f, 15f, 0f)

                chart.isDrawHoleEnabled = true
                chart.setHoleColor(Color.WHITE)

//        chart.setTransparentCircleRadius(0f);
                //        chart.setTransparentCircleRadius(0f);
                chart.setTransparentCircleColor(Color.WHITE)
                chart.setTransparentCircleAlpha(110)

                chart.holeRadius = 40f
                chart.transparentCircleRadius = 45f

                //delete description
                //delete description
//                val l = chart.legend
//                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//                l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
//                l.orientation = Legend.LegendOrientation.VERTICAL
//                l.setDrawInside(false)
//                l.isEnabled = false

                val yValues = ArrayList<PieEntry>()

                yValues.add(PieEntry(infoModel.cases.toFloat(), "Terinfeksi"))
                yValues.add(PieEntry(infoModel.recovered.toFloat(), "Sembuh"))
                yValues.add(PieEntry(infoModel.deaths.toFloat(), "Meninggal"))

                chart.animateXY(1000, 1000)

                val pieDataSet = PieDataSet(yValues, infoModel.country)
                pieDataSet.sliceSpace = 2f
                pieDataSet.selectionShift = 5f
                pieDataSet.colors = COVID_COLOR
                pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

                val pieData = PieData(pieDataSet)
                pieData.setValueTextSize(12f)
                pieData.setValueTextColor(Color.BLACK)

                chart.data = pieData

                //menghilangkan x value
                chart.setDrawEntryLabels(!chart.isDrawEntryLabelsEnabled());
                chart.invalidate();

                dialog.show()
            } )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    override fun onBindViewHolder(holder: InfoAdapter.ViewHolder, position: Int) {
        val infoModel = infoList[position]
        holder.bind(infoModel)
    }

}