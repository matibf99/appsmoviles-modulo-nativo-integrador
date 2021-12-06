package com.appsmoviles.gruposcomunitarios.presentation.adapters

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.ItemPostBinding
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.utils.calculateImageHeight
import com.appsmoviles.gruposcomunitarios.utils.time.getTimeAgo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import jp.wasabeef.glide.transformations.BlurTransformation

abstract class PostsAdapter(
    var username: String,
    var items: List<Post>,
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private var height: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding
        val liked = item.likes?.contains(username) == true
        val hasImage = item.photo != null

        binding.textPostUsername.text = item.createdBy
        binding.textPostGroup.text = item.groupName
        binding.textPostTime.text = item.createdAt?.getTimeAgo(binding.root.context)
        binding.textPostTitle.text = item.title

        if (hasImage) {
            binding.textPostContent.visibility = View.GONE
            binding.layoutPostImage.visibility = View.VISIBLE

            if (height == 0)
                height = calculateImageHeight()

            binding.layoutPostImage.layoutParams.height = height

            Glide.with(binding.root.context)
                .load(item.photo)
                .transform(MultiTransformation(CenterCrop(), BlurTransformation(25, 6)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.postImageBackground)

            Glide.with(binding.root.context)
                .load(item.photo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.postImage)
        } else {
            binding.layoutPostImage.visibility = View.GONE

            binding.textPostContent.visibility = View.VISIBLE
            binding.textPostContent.text = item.content
        }

        binding.postLikesCount.text = item.likes?.count().toString()
        binding.postTextCommentsCount.text = item.commentsCount.toString()

        if (liked)
            binding.btnPostLike.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            binding.btnPostLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        binding.textPostGroup.setOnClickListener { onOpenGroupListener(position) }
        binding.btnPostLike.setOnClickListener { onLikeListener(position, username) }
        binding.cardPost.setOnClickListener { onPostClickListener(position) }
        binding.layoutPostImage.setOnClickListener { onOpenImageListener(position) }

        binding.layoutPostImage.setOnLongClickListener { false }

        binding.cardPost.setOnCreateContextMenuListener { menu, v, menuInfo ->
            createContextMenu(menu, position, liked, hasImage)
        }
    }

    private fun createContextMenu(menu: ContextMenu, position: Int, liked: Boolean, hasImage: Boolean) {
        val openPost = menu.add(Menu.NONE, MENU_OPEN_POST, 1, R.string.menu_open_post)
        val like = menu.add(
            Menu.NONE, MENU_LIKE, 3,
            if (liked) R.string.menu_unlike else R.string.menu_like
        )

        openPost.setOnMenuItemClickListener {
            onPostClickListener(position)
            true
        }



        like.setOnMenuItemClickListener {
            onLikeListener(position, username)
            true
        }
        
        if (hasImage) {
            val openImage = menu.add(Menu.NONE, MENU_OPEN_IMAGE, 2, R.string.menu_open_image)
            openImage.setOnMenuItemClickListener {
                onOpenImageListener(position)
                true
            }
        }
    }

    override fun getItemCount(): Int = items.count()

    abstract fun onPostClickListener(position: Int)

    abstract fun onLikeListener(position: Int, username: String)

    abstract fun onOpenGroupListener(position: Int)

    abstract fun onOpenImageListener(position: Int)

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    companion object {
        const val MENU_OPEN_POST = 1
        const val MENU_OPEN_IMAGE = 2
        const val MENU_LIKE = 3
    }
}