package com.appsmoviles.gruposcomunitarios.presentation.adapters

import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.ItemSearchBinding
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

abstract class SearchAdapter(
    var username: String,
    var items: List<Group>
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding
        val isSubscribed = item.subscribed?.contains(username) == true

        binding.itemSearchTitle.text = item.name

        var tags = ""

        for (i: Int in item.tags!!.indices) {
            tags += item.tags[i]

            if (i != item.tags.size - 1)
                tags += ", "
        }
        binding.itemSearchSubtitle.text = tags

        Glide.with(binding.root.context)
            .load(item.photo)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.itemSearchImageView)

        if (isSubscribed)
            binding.itemSearchBtnSubscribe.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            binding.itemSearchBtnSubscribe.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        binding.itemSearchBtnSubscribe.setOnClickListener {
            onSubscribeListener(
                position,
                username
            )
        }
        binding.itemSearchLayout.setOnClickListener { onOpenGroupListener(position) }
        binding.itemSearchImageView.setOnClickListener { onOpenImageListener(position) }

        binding.itemSearchLayout.setOnCreateContextMenuListener { menu, v, menuInfo ->
            createContextMenu(menu, position, isSubscribed)
        }
    }

    private fun createContextMenu(menu: ContextMenu, position: Int, isSubscribed: Boolean) {
        val openGroup = menu.add(Menu.NONE, MENU_OPEN_GROUP, 1, R.string.menu_open_group)
        val openImage = menu.add(Menu.NONE, MENU_OPEN_IMAGE, 2, R.string.menu_open_image)
        val subscribe = menu.add(
            Menu.NONE, MENU_SUBSCRIBE, 3,
            if (!isSubscribed) R.string.menu_subscribe else R.string.menu_unsubscribe
        )

        openGroup.setOnMenuItemClickListener {
            onOpenGroupListener(position)
            true
        }


        openImage.setOnMenuItemClickListener {
            onOpenImageListener(position)
            true
        }

        subscribe.setOnMenuItemClickListener {
            onSubscribeListener(position, username)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    abstract fun onSubscribeListener(position: Int, username: String)

    abstract fun onOpenGroupListener(position: Int)

    abstract fun onOpenImageListener(position: Int)

    inner class SearchViewHolder(val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    companion object {
        const val MENU_OPEN_GROUP = 1
        const val MENU_SUBSCRIBE = 2
        const val MENU_OPEN_IMAGE = 3
    }
}
