package xin.monus.checkit.inbox

import android.content.Context
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.InboxItem




class InboxListAdapter(list: List<InboxItem>) : BaseAdapter() {
    var list: List<InboxItem> = list
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getCount() = list.size

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val inBoxItem = getItem(i)
        val rowView = view ?: LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.activity_inbox_frag_item, viewGroup, false)

        with(rowView.findViewById<TextView>(R.id.content)) {
            text = inBoxItem.content
            if (inBoxItem.complete)
                paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
            else
                paint.flags = 0
        }

        with(rowView.findViewById<TextView>(R.id.deadLine)) {
            text = inBoxItem.deadline
        }

        with(rowView.findViewById<ImageButton>(R.id.complete)) {
            if (!inBoxItem.complete) {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_normal))
            } else {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_press))
            }

            setOnClickListener {
                if (!inBoxItem.complete)
                    setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_press))
                else
                    setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_normal))

                inBoxItem.complete = !inBoxItem.complete
                notifyDataSetChanged()
            }
        }

        rowView.setOnClickListener {
            println("edit view")
        }

        return rowView
    }

    override fun getItem(i: Int) = list[i]

    override fun getItemId(i: Int) = i.toLong()
}