package xin.monus.checkit.inbox

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.InboxItem
import java.text.SimpleDateFormat
import java.util.*


class InboxListAdapter(val context: Context, list: List<InboxItem>, val itemClickedListener: InboxFragment.ItemClickedListener) :
        xin.monus.checkit.base.BaseAdapter<InboxListAdapter.ViewHolder>(context) {

    var list: List<InboxItem> = list
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val deadlineTime = list[position].deadline
            val flag = list[position].flag
            id = list[position].id
            deadline.text = deadlineTime
            complete = list[position].complete
            content.text = list[position].content

            val sfFull = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val sfCommon = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val sfYmd = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val current = Date()
            val cal = Calendar.getInstance()
            cal.time = current
            val time1 = current.time
            val today = sfYmd.parse(sfYmd.format(current)) as Date
            cal.time = today
            cal.add(Calendar.DAY_OF_MONTH, 2)
            val time2 = cal.time.time
            val date = try {
                sfCommon.parse(deadlineTime)
            } catch (e: Exception) {
                sfFull.parse(deadlineTime)
            }
            when {
                date.time < time1 -> deadline.setBackgroundColor(ContextCompat.getColor(context, R.color.background_red))
                date.time < time2 -> deadline.setBackgroundColor(ContextCompat.getColor(context, R.color.background_yellow))
                else -> deadline.setBackgroundColor(Color.WHITE)
            }
            if (flag) {
                content.setTextColor(ContextCompat.getColor(context, R.color.forecast_date_red))
            } else {
                content.setTextColor(Color.BLACK)
            }



            if (complete) {
                content.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
            }
            else {
                content.paint.flags = 0
            }
            if (!complete) {
                completeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_normal))
            }
            else {
                completeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_press))
            }

            completeBtn.setOnClickListener {
                if (!complete) {
                    println(complete)
                    completeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_normal))
                    println(complete)
                }
                else {
                    println(complete)
                    completeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_press))
                    println(complete)
                }
                notifyDataSetChanged()
                itemClickedListener.itemComplete(id)
            }

            holder.itemView.setOnClickListener {
                itemClickedListener.getID(id)
            }
        }
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.activity_inbox_frag_item, parent, false)
        return ViewHolder(view)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id = -1
        val content: TextView = itemView.findViewById(R.id.content)
        val deadline: TextView = itemView.findViewById(R.id.deadLine)
        var complete = false
        val completeBtn: ImageButton = itemView.findViewById(R.id.complete)
    }
}