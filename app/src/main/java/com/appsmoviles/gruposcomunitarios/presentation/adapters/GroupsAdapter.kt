package com.appsmoviles.gruposcomunitarios.presentation.adapters

import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.ItemGroupBinding
import com.appsmoviles.gruposcomunitarios.databinding.ItemGroupHeaderBinding
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

abstract class GroupsAdapter(
    @StringRes private val title: Int,
    var username: String,
    var items: List<Group>,
    private val displaySubscribeButton: Boolean,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var position: Int = 0

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER
        else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            GroupHeaderViewHolder(
                ItemGroupHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            GroupViewHolder(
                ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupHeaderViewHolder)
            onBindHeaderHolder(holder)
        else if (holder is GroupViewHolder)
            onBindGroupViewHolder(holder, position - 1)
    }

    private fun onBindHeaderHolder(holder: GroupHeaderViewHolder) {
        val binding = holder.binding

        binding.itemGroupHeaderText.setText(title)
    }

    private fun onBindGroupViewHolder(holder: GroupViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding
        val isSubscribed = item.subscribed?.contains(username) == true

        binding.itemGroupTitle.text = item.name!!

        binding.itemGroupSubtitle.setText(
            when {
                username == item.createdBy -> R.string.adapter_groups_rol_owner
                item.moderators?.contains(username) == true -> R.string.adapter_groups_rol_moderator
                else -> R.string.adapter_groups_rol_user
            }
        )

        Glide.with(binding.root.context)
            .load(item.photo)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.itemGroupImageView)

        binding.itemGroupBtnSubscribe.setOnClickListener {
            onUnsubscribeListener(position)
        }

        if (username.isEmpty())
            binding.itemGroupBtnSubscribe.visibility = View.GONE
        else
            binding.itemGroupBtnSubscribe.visibility = View.VISIBLE

        if (displaySubscribeButton) {
            if (isSubscribed)
                binding.itemGroupBtnSubscribe.setImageResource(R.drawable.ic_baseline_favorite_24)
            else
                binding.itemGroupBtnSubscribe.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        } else {
            binding.itemGroupBtnSubscribe.visibility = View.GONE
        }

        binding.itemGroupImageView.setOnClickListener { onOpenImageListener(position) }
        binding.itemGroupLayout.setOnClickListener { onOpenGroupListener(position) }

        binding.itemGroupLayout.setOnCreateContextMenuListener { menu, v, menuInfo ->
            createContextMenu(menu, position, isSubscribed)
        }
    }

    private fun createContextMenu(menu: ContextMenu, position: Int, isSubscribed: Boolean) {
        val openGroup = menu.add(Menu.NONE, MENU_OPEN_GROUP, 1, R.string.menu_open_group)
        val openImage = menu.add(Menu.NONE, MENU_OPEN_IMAGE, 2, R.string.menu_open_image)

        openGroup.setOnMenuItemClickListener {
            onOpenGroupListener(position)
            true
        }

        openImage.setOnMenuItemClickListener {
            onOpenImageListener(position)
            true
        }

        if (displaySubscribeButton) {
            val subscribe = menu.add(
                Menu.NONE, MENU_SUBSCRIBE, 3,
                if (!isSubscribed) R.string.menu_subscribe else R.string.menu_unsubscribe
            )
            subscribe.setOnMenuItemClickListener {
                onUnsubscribeListener(position)
                true
            }
        }
    }

    override fun getItemCount(): Int = items.size + 1

    abstract fun onUnsubscribeListener(position: Int)

    abstract fun onOpenGroupListener(position: Int)

    abstract fun onOpenImageListener(position: Int)

    inner class GroupHeaderViewHolder(val binding: ItemGroupHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    inner class GroupViewHolder(val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1

        const val MENU_OPEN_GROUP = 1
        const val MENU_OPEN_IMAGE = 2
        const val MENU_SUBSCRIBE = 3
    }

}