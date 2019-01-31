package org.fossasia.susi.ai.chat.adapters.viewholders

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.model.ChatMessage

import java.util.ArrayList

import butterknife.ButterKnife
import kotterknife.bindView
import timber.log.Timber

class PieChartViewHolder(view: View, listener: MessageViewHolder.ClickListener) : MessageViewHolder(view, listener) {

    val backgroundLayout: LinearLayout by bindView(R.id.background_layout)
    val chatTextView: TextView by bindView(R.id.text)
    val pieChart: PieChart by bindView(R.id.piechart)
    val timeStamp: TextView by bindView(R.id.timestamp)

    init {
        ButterKnife.bind(this, itemView)
    }

    /**
     * Inflate PieChart
     *
     * @param model the ChatMessage object
     */
    fun setView(model: ChatMessage?) {
        if (model != null) {
            try {
                chatTextView.text = model.content
                timeStamp.text = model.timeStamp
                pieChart.setUsePercentValues(true)
                pieChart.isDrawHoleEnabled = true
                pieChart.holeRadius = 7f
                pieChart.transparentCircleRadius = 10f
                pieChart.isRotationEnabled = true
                pieChart.rotationAngle = 0f
                pieChart.dragDecelerationFrictionCoef = 0.001f
                pieChart.legend.isEnabled = false
                val description = Description()
                description.text = ""
                pieChart.description = description
                val datumList = model.datumRealmList
                val yVals = ArrayList<PieEntry>()
                val xVals = ArrayList<String>()

                if (datumList != null) {
                    for (item in datumList) {
                        yVals.add(PieEntry(item.percent, item.president))
                    }
                }
                pieChart.isClickable = false
                pieChart.isHighlightPerTapEnabled = false
                val dataSet = PieDataSet(yVals, "")
                dataSet.sliceSpace = 3f
                dataSet.selectionShift = 5f
                val colors = listOf(
                        ColorTemplate.COLORFUL_COLORS,
                        ColorTemplate.JOYFUL_COLORS,
                        ColorTemplate.VORDIPLOM_COLORS,
                        ColorTemplate.LIBERTY_COLORS,
                        ColorTemplate.PASTEL_COLORS)
                        .flatMap {
                            it.asIterable()
                        }
                dataSet.colors = colors
                val data = PieData(dataSet)
                data.setValueFormatter(PercentFormatter())
                data.setValueTextSize(11f)
                data.setValueTextColor(Color.GRAY)
                pieChart.data = data
                pieChart.highlightValues(null)
                pieChart.invalidate()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}
