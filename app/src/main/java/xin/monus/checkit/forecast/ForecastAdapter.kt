package xin.monus.checkit.forecast

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xin.monus.checkit.R
import xin.monus.checkit.base.BaseAdapter
import xin.monus.checkit.data.entity.Forecast
import xin.monus.checkit.data.entity.ForecastType

class ForecastAdapter(val context: Context, list: List<Forecast>):BaseAdapter<ForecastAdapter.ViewHolder>(context) {

    var list: List<Forecast> = list
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.activity_forecast_frag_item, parent, false)
        return ForecastAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            content.text = list[position].content
            deadline.text = list[position].deadline
            when (list[position].type) {
                ForecastType.INBOX -> {
                    type.setText(R.string.forecast_inbox)
                }
                ForecastType.ACTION -> {
                    type.setText(R.string.forecast_action)
                }
                ForecastType.PROJECT -> {
                    type.setText(R.string.forecast_project)
                }
            }
        }
    }

    override fun getItemCount() = list.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content by lazy { itemView.findViewById(R.id.content) as TextView }
        val deadline by lazy { itemView.findViewById(R.id.deadline) as TextView }
        val type by lazy { itemView.findViewById(R.id.type_label) as TextView }
    }
}