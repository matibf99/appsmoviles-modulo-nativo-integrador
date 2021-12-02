package com.appsmoviles.gruposcomunitarios.presentation.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.bumptech.glide.Glide

abstract class GroupsAdapter(
    private val title: String,
    var items: List<Group>,
    private val displaySubscribeButton: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER
            else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_group_header, parent, false)
            GroupHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_group, parent, false)
            GroupViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupViewHolder) {
            val item = items[position-1]

            holder.tvTitle.text = item.name!!

            holder.tvSubtitle.text = item.userRol!!

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .circleCrop()
                .into(holder.imageView)

            holder.btnUnsubscribe.setOnClickListener {
                onUnsubscribeListener(position-1)
            }

            if (displaySubscribeButton) {
                if (item.subscribed == true)
                    holder.btnUnsubscribe.setImageResource(R.drawable.ic_baseline_favorite_24)
                else
                    holder.btnUnsubscribe.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            } else {
                holder.btnUnsubscribe.visibility = View.GONE
            }

            holder.layout.setOnClickListener {

            }
        } else if (holder is GroupHeaderViewHolder) {
            holder.tvTitle.text = title
        }
    }

    override fun getItemCount(): Int = items.size + 1

    abstract fun onUnsubscribeListener(position: Int)

    inner class GroupHeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.item_group_header_text)
    }

    inner class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.item_group_layout)
        val tvTitle: TextView = view.findViewById(R.id.item_group_title)
        val tvSubtitle: TextView = view.findViewById(R.id.item_group_subtitle)
        val imageView: ImageView = view.findViewById(R.id.item_group_image_view)
        val btnUnsubscribe: ImageButton = view.findViewById(R.id.item_group_btn_subscribe)
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

}