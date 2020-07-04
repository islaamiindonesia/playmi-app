package id.islaami.playmi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.islaami.playmi.R
import id.islaami.playmi.data.model.category.Label
import id.islaami.playmi.util.value
import kotlinx.android.synthetic.main.label_item.view.*

class LabelAdapter(
    var list: List<Label> = emptyList(),
    var itemClickListener: (Int, String) -> Unit
) :
    RecyclerView.Adapter<LabelAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size.value()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.label_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(label: Label) = with(itemView) {
            name.text = label.name

            itemLayout.setOnClickListener {
                itemClickListener(label.ID.value(), label.name.toString())
            }
        }
    }
}