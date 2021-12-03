package com.appsmoviles.gruposcomunitarios.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.bumptech.glide.Glide

abstract class PostsAdapter(
    var username: String,
    var items: List<Post>
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = items[position]

        holder.tvUsername.text = item.createdBy
        holder.tvGroup.text = item.groupId
        holder.tvTitle.text = item.title

        if (item.photo != null) {
            holder.tvContent.visibility = View.GONE

            Glide.with(holder.imageView.context)
                .load(item.photo)
                .into(holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
            holder.tvContent.visibility = View.VISIBLE
            holder.tvContent.text = item.content
        }

        holder.tvLikesCount.text = item.likes?.count().toString()
        holder.tvCommentsCount.text = item.commentsCount.toString()

        if (item.likes!!.contains(username))
            holder.btnLike.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            holder.btnLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        holder.tvGroup.setOnClickListener { onOpenGroupListener(position) }
        holder.btnLike.setOnClickListener { onLikeListener(position) }
        holder.card.setOnClickListener { onPostClickListener(position) }
    }

    override fun getItemCount(): Int = items.count()

    abstract fun onPostClickListener(position: Int)

    abstract fun onLikeListener(position: Int)

    abstract fun onOpenGroupListener(position: Int)

    inner class PostViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card_post)
        val tvUsername: TextView = view.findViewById(R.id.text_post_username)
        val tvGroup: TextView = view.findViewById(R.id.text_post_group)
        val tvTitle: TextView = view.findViewById(R.id.text_post_title)
        val tvContent: TextView = view.findViewById(R.id.text_post_content)
        val imageView: ImageView = view.findViewById(R.id.post_image)
        val btnLike: ImageView = view.findViewById(R.id.btn_post_like)
        val tvLikesCount: TextView = view.findViewById(R.id.post_likes_count)
        val tvCommentsCount: TextView = view.findViewById(R.id.post_text_comments_count)
    }
}