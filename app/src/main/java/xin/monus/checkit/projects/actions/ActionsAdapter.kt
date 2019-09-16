package xin.monus.checkit.projects.actions

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
import xin.monus.checkit.base.BaseAdapter
import xin.monus.checkit.data.entity.Action
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author wu
 * @date   2017/12/12
 */
class ActionsAdapter(val context: Context, actionList: List<Action>, private val itemClickListerner:ActionsActivity.ItemClickListener) : BaseAdapter<ActionsAdapter.ViewHolder>(context) {

    var actionList: List<Action> = actionList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = actionList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            contentTxt.text = actionList[position].content
            deadlineTxt.text = actionList[position].deadline

            val deadlineTime = actionList[position].deadline
            val content = contentTxt
            val deadline = deadlineTxt
            val flag = actionList[position].flag
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
            if (date.time < time1) {
                deadline.setBackgroundColor(ContextCompat.getColor(context, R.color.background_red))
            } else if (date.time < time2) {
                deadline.setBackgroundColor(ContextCompat.getColor(context, R.color.background_yellow))
            }
            if (flag) {
                content.setTextColor(ContextCompat.getColor(context, R.color.forecast_date_red))
            } else {
                content.setTextColor(Color.BLACK)
            }

            val complete = actionList[position].complete
            if (complete) {
                contentTxt.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
            }
            else {
                contentTxt.paint.flags = 0
            }
            if (!complete) {
                completeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_normal))
            }
            else {
                completeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_press))
            }

            completeBtn.setOnClickListener {
                itemClickListerner.itemComplete(actionList[position].id)
            }
        }

        holder.itemView.setOnClickListener {
            itemClickListerner.itemClick(actionList[position].id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.activity_actions_item, parent, false)
        return ViewHolder(view)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTxt: TextView = itemView.findViewById(R.id.content)
        val deadlineTxt: TextView = itemView.findViewById(R.id.deadline)
        val completeBtn: ImageButton = itemView.findViewById(R.id.complete)
    }
}