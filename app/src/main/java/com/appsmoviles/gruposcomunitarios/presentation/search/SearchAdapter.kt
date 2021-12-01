package com.appsmoviles.gruposcomunitarios.presentation.search

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

abstract class SearchAdapter(
    private val items: List<Group>
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)

        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.name

        var tags = ""

        for (i: Int in item.tags!!.indices) {
            tags += item.tags[i]

            if (i != item.tags.size - 1)
                tags += ", "
        }
        holder.tvSubtitle.text = tags

        Glide.with(holder.itemView.context)
            .load(item.photo)
            .centerCrop()
            .into(holder.imageView)

        if (item.subscribed == true)
            holder.btnSubscribe.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            holder.btnSubscribe.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        holder.btnSubscribe.setOnClickListener { onSubscribeListener(position) }

        holder.layout.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = items.size

    abstract fun onSubscribeListener(position: Int)

    inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.item_search_layout)
        val tvTitle: TextView = view.findViewById(R.id.item_search_title)
        val tvSubtitle: TextView = view.findViewById(R.id.item_search_subtitle)
        val imageView: ImageView = view.findViewById(R.id.item_search_image_view)
        val btnSubscribe: ImageButton = view.findViewById(R.id.item_search_btn_subscribe)
    }
}
