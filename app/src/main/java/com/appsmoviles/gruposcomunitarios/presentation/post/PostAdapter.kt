package com.appsmoviles.gruposcomunitarios.presentation.post

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.utils.calculateImageHeight
import com.appsmoviles.gruposcomunitarios.utils.time.getTimeAgo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation

abstract class PostAdapter(
    var username: String,
    var post: Post,
    var parentComments: List<PostComment>,
    var allComments: List<PostComment>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> POST_DETAIL_TYPE
            else -> POST_COMMENT_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == POST_DETAIL_TYPE)
                PostDetailHolder(LayoutInflater.from(parent.context).inflate(
                    R.layout.item_post_detail,
                    parent,
                    false
                ))
            else
                PostCommentHolder(LayoutInflater.from(parent.context).inflate(
                    R.layout.item_post_comment,
                    parent,
                    false
                ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is PostDetailHolder -> onBindPostDetailHolder(holder)
            is PostCommentHolder -> onBindPostCommentHolder(holder, position - 1)
        }
    }

    private fun onBindPostDetailHolder(holder: PostDetailHolder) {
        holder.tvUsername.text = post.createdBy
        holder.tvGroup.text = post.groupName
        holder.tvTime.text = post.createdAt?.getTimeAgo()
        holder.tvTitle.text = post.title
        holder.tvContent.text = post.content

        post.photo?.let {
            holder.layoutImage.layoutParams.height = calculateImageHeight()

            Glide.with(holder.imageView.context)
                .load(it)
                .transform(MultiTransformation(CenterCrop(), BlurTransformation(25, 3)))
                .into(holder.imageViewBackground)

            Glide.with(holder.itemView.context)
                .load(it)
                .fitCenter()
                .into(holder.imageView)
        }

        holder.btnLike.setImageResource(
            if (post.likes?.contains(username) == true) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )

        holder.tvCommentsCount.text = post.commentsCount.toString()
        holder.tvLikesCount.text = post.likes?.count().toString()

        holder.tvGroup.setOnClickListener { onOpenGroupListener() }
        holder.btnLike.setOnClickListener { likePost(username) }
    }

    private fun onBindPostCommentHolder(holder: PostCommentHolder, position: Int) {
        val comment = parentComments[position]

        holder.tvUsername.text = comment.username
        holder.tvTime.text = comment.createdAt?.getTimeAgo()
        holder.tvContent.text = comment.content
        holder.tvCommentsCount.text = allComments.filter { it.parent == comment.documentId }.count().toString()
        holder.tvLikesCount.text = comment.likes?.count().toString()

        holder.btnLike.setImageResource(
            if (comment.likes?.contains(username) == true) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )

        holder.btnLike.setOnClickListener { likeComment(position, username) }
        holder.layout.setOnClickListener { onOpenCommentListener(position) }
    }

    override fun getItemCount(): Int = parentComments.count() + 1

    abstract fun likePost(username: String)

    abstract fun likeComment(position: Int, username: String)

    abstract fun onOpenGroupListener()

    abstract fun onOpenCommentListener(position: Int)

    inner class PostDetailHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card_post_detail)
        val tvUsername: TextView = view.findViewById(R.id.text_post_detail_username)
        val tvGroup: TextView = view.findViewById(R.id.text_post_detail_group)
        val tvTime: TextView = view.findViewById(R.id.text_post_detail_time)
        val tvTitle: TextView = view.findViewById(R.id.text_post_detail_title)
        val tvContent: TextView = view.findViewById(R.id.text_post_detail_content)
        val layoutImage: ConstraintLayout = view.findViewById(R.id.layout_post_detail_image)
        val imageView: ImageView = view.findViewById(R.id.post_detail_image)
        val imageViewBackground: ImageView = view.findViewById(R.id.post_detail_image_background)
        val btnLike: ImageButton = view.findViewById(R.id.btn_post_detail_like)
        val tvLikesCount: TextView = view.findViewById(R.id.post_detail_likes_count)
        val tvCommentsCount: TextView = view.findViewById(R.id.post_detail_text_comments_count)
    }

    inner class PostCommentHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.card_comment)
        val tvUsername: TextView = view.findViewById(R.id.text_comment_username)
        val tvTime: TextView = view.findViewById(R.id.text_comment_time)
        val tvContent: TextView = view.findViewById(R.id.text_comment_content)
        val tvCommentsCount: TextView = view.findViewById(R.id.text_comment_comments_count)
        val tvLikesCount: TextView = view.findViewById(R.id.text_comment_likes_count)
        val btnLike: ImageButton = view.findViewById(R.id.btn_comment_like)
    }

    companion object {
        private const val POST_DETAIL_TYPE = 1
        private const val POST_COMMENT_TYPE = 2
    }
}