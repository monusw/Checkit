package xin.monus.checkit.base

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater

/**
 * Base adapter for recycler view
 */
abstract class BaseAdapter<VH : RecyclerView.ViewHolder>(context: Context) :
        RecyclerView.Adapter<VH>(){

    val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

}