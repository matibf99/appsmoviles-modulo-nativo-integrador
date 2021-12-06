package com.appsmoviles.gruposcomunitarios.presentation.postcomments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.ItemPostCommentChildBinding
import com.appsmoviles.gruposcomunitarios.databinding.ItemPostCommentParentBinding
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.utils.time.getTimeAgo

abstract class PostCommentsAdapter(
    var username: String,
    var parent: PostComment,
    var comments: List<PostComment>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ITEM_PARENT
            else -> ITEM_COMMENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_PARENT)
            ParentCommentHolder(
                ItemPostCommentParentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
        else
            ChildCommentHolder(
                ItemPostCommentChildBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ParentCommentHolder)
            onBindParentCommentHolder(holder)
        else if (holder is ChildCommentHolder)
            onBindChildCommentHolder(holder, position - 1)
    }

    private fun onBindParentCommentHolder(holder: ParentCommentHolder) {
        val binding = holder.binding

        binding.textCommentParentUsername.text = parent.username
        binding.textCommentParentTime.text = parent.createdAt?.getTimeAgo(binding.root.context)
        binding.textCommentParentCommentsCount.text = comments.count().toString()
        binding.textCommentParentContent.text = parent.content
        binding.textCommentParentLikesCount.text = parent.likes?.count().toString()

        binding.btnCommentParentLike.setImageResource(
            if (parent.likes?.contains(username) == true) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
        binding.btnCommentParentLike.setOnClickListener { likeParentComment(username) }
    }

    private fun onBindChildCommentHolder(holder: ChildCommentHolder, position: Int) {
        val comment = comments[position]
        val binding = holder.binding

        binding.textCommentChildUsername.text = comment.username
        binding.textCommentChildTime.text = comment.createdAt?.getTimeAgo(binding.root.context)
        binding.textCommentChildContent.text = comment.content
        binding.textCommentChildLikesCount.text = comment.likes?.count().toString()

        binding.btnCommentChildLike.setImageResource(
            if (comment.likes?.contains(username) == true) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
        binding.btnCommentChildLike.setOnClickListener { likeChildComment(position, username) }
        binding.cardChildComment.setOnClickListener { }
    }

    override fun getItemCount(): Int = comments.count() + 1

    abstract fun likeParentComment(username: String)

    abstract fun likeChildComment(position: Int, username: String)

    companion object {
        const val ITEM_PARENT = 0
        const val ITEM_COMMENT = 1
    }

    inner class ParentCommentHolder(val binding: ItemPostCommentParentBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    inner class ChildCommentHolder(val binding: ItemPostCommentChildBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}