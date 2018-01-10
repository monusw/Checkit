package xin.monus.checkit.forecast

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xin.monus.checkit.R
import xin.monus.checkit.base.BaseAdapter
import xin.monus.checkit.data.entity.Forecast
import xin.monus.checkit.data.entity.ForecastType
import java.text.SimpleDateFormat
import java.util.*

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

            val deadlineTime = list[position].deadline
            val sfFull = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val sfCommon = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val sfYmd = SimpleDateFormat("yyyy-MM-dd")
            val current = Date()
            val cal = Calendar.getInstance()
            cal.time = current
            val time1 = current.time
            val today = sfYmd.parse(sfYmd.format(current))
            cal.time = today
            cal.add(Calendar.DAY_OF_MONTH, 2)
            val time2 = cal.time.time
            val date = try {
                sfCommon.parse(deadlineTime)
            } catch (e: Exception) {
                sfFull.parse(deadlineTime)
            }
            if (date.time < time1) {
                deadline.setBackgroundColor(ContextCompat.getColor(context, R.color.background_red))
            } else if (date.time < time2) {
                deadline.setBackgroundColor(ContextCompat.getColor(context, R.color.background_yellow))
            } else {
                deadline.setBackgroundColor(Color.WHITE)
            }


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