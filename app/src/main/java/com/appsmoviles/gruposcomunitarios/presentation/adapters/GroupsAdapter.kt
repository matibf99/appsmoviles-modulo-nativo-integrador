package com.appsmoviles.gruposcomunitarios.presentation.adapters

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
    private val username: String,
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
            val i = position - 1

            val item = items[i]

            holder.tvTitle.text = item.name!!

            holder.tvSubtitle.text = when {
                username == item.createdBy -> "Owner"
                item.moderators?.contains(username) == true -> "Moderator"
                else -> "User"
            }

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .circleCrop()
                .into(holder.imageView)

            holder.btnUnsubscribe.setOnClickListener {
                onUnsubscribeListener(i)
            }

            if (displaySubscribeButton) {
                if (item.subscribed?.contains(username) == true)
                    holder.btnUnsubscribe.setImageResource(R.drawable.ic_baseline_favorite_24)
                else
                    holder.btnUnsubscribe.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            } else {
                holder.btnUnsubscribe.visibility = View.GONE
            }

            holder.layout.setOnClickListener {
                onOpenGroupListener(i)
            }
        } else if (holder is GroupHeaderViewHolder) {
            holder.tvTitle.text = title
        }
    }

    override fun getItemCount(): Int = items.size + 1

    abstract fun onUnsubscribeListener(position: Int)

    abstract fun onOpenGroupListener(position: Int)

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