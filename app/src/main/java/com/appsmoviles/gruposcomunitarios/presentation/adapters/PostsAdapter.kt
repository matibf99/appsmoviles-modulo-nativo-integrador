package com.appsmoviles.gruposcomunitarios.presentation.adapters

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.utils.Res
import com.appsmoviles.gruposcomunitarios.utils.calculateImageHeight
import com.appsmoviles.gruposcomunitarios.utils.time.getTimeAgo
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.GrayscaleTransformation

abstract class PostsAdapter(
    var username: String,
    var items: List<Post>,
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private var height: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = items[position]

        holder.tvUsername.text = item.createdBy
        holder.tvGroup.text = item.groupName
        holder.tvTime.text = item.createdAt?.getTimeAgo()
        holder.tvTitle.text = item.title

        if (item.photo != null) {
            holder.tvContent.visibility = View.GONE

            if (height == 0)
                height = calculateImageHeight()

            holder.layoutImage.layoutParams.height = height

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .transform(MultiTransformation(CenterCrop(), BlurTransformation(25, 6)))
                .into(holder.imageViewBackground)

            Glide.with(holder.itemView.context)
                .load(item.photo)
                .into(holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
            holder.imageViewBackground.visibility = View.GONE

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
        holder.btnLike.setOnClickListener { onLikeListener(position, username) }
        holder.card.setOnClickListener { onPostClickListener(position) }
    }

    override fun getItemCount(): Int = items.count()

    abstract fun onPostClickListener(position: Int)

    abstract fun onLikeListener(position: Int, username: String)

    abstract fun onOpenGroupListener(position: Int)

    inner class PostViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card_post)
        val tvUsername: TextView = view.findViewById(R.id.text_post_username)
        val tvGroup: TextView = view.findViewById(R.id.text_post_group)
        val tvTime: TextView = view.findViewById(R.id.text_post_time)
        val tvTitle: TextView = view.findViewById(R.id.text_post_title)
        val tvContent: TextView = view.findViewById(R.id.text_post_content)
        val layoutImage: ConstraintLayout = view.findViewById(R.id.layout_post_image)
        val imageView: ImageView = view.findViewById(R.id.post_image)
        val imageViewBackground: ImageView = view.findViewById(R.id.post_image_background)
        val btnLike: ImageView = view.findViewById(R.id.btn_post_like)
        val tvLikesCount: TextView = view.findViewById(R.id.post_likes_count)
        val tvCommentsCount: TextView = view.findViewById(R.id.post_text_comments_count)
    }
}