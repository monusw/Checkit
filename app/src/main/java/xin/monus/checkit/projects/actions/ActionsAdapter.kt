package xin.monus.checkit.projects.actions

import android.content.Context
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import xin.monus.checkit.R
import xin.monus.checkit.base.BaseAdapter
import xin.monus.checkit.data.entity.Action

/**
 * @author wu
 * @date   2017/12/12
 */
class ActionsAdapter(val context: Context, actionList: List<Action>, val itemClickListerner:ActionsActivity.ItemClickListener) : BaseAdapter<ActionsAdapter.ViewHolder>(context) {

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

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.activity_actions_item, parent, false)
        return ViewHolder(view)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTxt: TextView = itemView.findViewById(R.id.content)
        val deadlineTxt: TextView = itemView.findViewById(R.id.deadline)
        val completeBtn: ImageButton = itemView.findViewById(R.id.complete)
    }
}