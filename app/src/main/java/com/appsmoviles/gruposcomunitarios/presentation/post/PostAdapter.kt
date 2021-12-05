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
import com.appsmoviles.gruposcomunitarios.databinding.ItemPostCommentBinding
import com.appsmoviles.gruposcomunitarios.databinding.ItemPostDetailBinding
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.utils.calculateImageHeight
import com.appsmoviles.gruposcomunitarios.utils.time.getTimeAgo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
        return when (position) {
            0 -> POST_DETAIL_TYPE
            else -> POST_COMMENT_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == POST_DETAIL_TYPE)
            PostDetailHolder(
                ItemPostDetailBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        else
            PostCommentHolder(
                ItemPostCommentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostDetailHolder -> onBindPostDetailHolder(holder)
            is PostCommentHolder -> onBindPostCommentHolder(holder, position - 1)
        }
    }

    private fun onBindPostDetailHolder(holder: PostDetailHolder) {
        val binding = holder.binding

        binding.textPostDetailUsername.text = post.createdBy
        binding.textPostDetailGroup.text = post.groupName
        binding.textPostDetailTime.text = post.createdAt?.getTimeAgo()
        binding.textPostDetailTitle.text = post.title
        binding.textPostDetailContent.text = post.content

        post.photo?.let {
            binding.layoutPostDetailImage.layoutParams.height = calculateImageHeight()

            Glide.with(binding.root.context)
                .load(it)
                .transform(MultiTransformation(CenterCrop(), BlurTransformation(25, 3)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.postDetailImageBackground)

            Glide.with(binding.root.context)
                .load(it)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.postDetailImage)
        }

        if (post.latitude == null && post.longitude == null)
            binding.btnPostLocation.visibility = View.GONE

        binding.btnPostDetailLike.setImageResource(
            if (post.likes?.contains(username) == true) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )

        binding.postDetailTextCommentsCount.text = post.commentsCount.toString()
        binding.postDetailLikesCount.text = post.likes?.count().toString()

        binding.textPostDetailGroup.setOnClickListener { onOpenGroupListener() }
        binding.btnPostDetailLike.setOnClickListener { likePost(username) }
        binding.btnPostLocation.setOnClickListener { onOpenLocationListener() }
        binding.layoutPostDetailImage.setOnClickListener { onOpenImageListener() }
    }

    private fun onBindPostCommentHolder(holder: PostCommentHolder, position: Int) {
        val comment = parentComments[position]
        val binding = holder.binding

        binding.textCommentUsername.text = comment.username
        binding.textCommentTime.text = comment.createdAt?.getTimeAgo()
        binding.textCommentContent.text = comment.content
        binding.textCommentCommentsCount.text =
            allComments.filter { it.parent == comment.documentId }.count().toString()
        binding.textCommentLikesCount.text = comment.likes?.count().toString()

        binding.btnCommentLike.setImageResource(
            if (comment.likes?.contains(username) == true) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )

        binding.btnCommentLike.setOnClickListener { likeComment(position, username) }
        binding.cardComment.setOnClickListener { onOpenCommentListener(position) }
    }

    override fun getItemCount(): Int = parentComments.count() + 1

    abstract fun likePost(username: String)

    abstract fun likeComment(position: Int, username: String)

    abstract fun onOpenGroupListener()

    abstract fun onOpenCommentListener(position: Int)

    abstract fun onOpenImageListener()

    abstract fun onOpenLocationListener()

    inner class PostDetailHolder(val binding: ItemPostDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    inner class PostCommentHolder(val binding: ItemPostCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    companion object {
        private const val POST_DETAIL_TYPE = 1
        private const val POST_COMMENT_TYPE = 2
    }
}