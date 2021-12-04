package com.appsmoviles.gruposcomunitarios.presentation.postcomments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.utils.time.getTimeAgo

abstract class PostCommentsAdapter(
    var username: String,
    var parent: PostComment,
    var comments: List<PostComment>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> PARENT
            else -> COMMENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == PARENT)
            ParentCommentHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_comment_parent, parent, false))
        else
            ChildCommentHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_comment_child, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ParentCommentHolder)
           onBindParentCommentHolder(holder)
        else if (holder is ChildCommentHolder)
            onBindChildCommentHolder(holder, position - 1)
    }

    private fun onBindParentCommentHolder(holder: ParentCommentHolder) {
        holder.tvUsername.text = parent.username
        holder.tvTime.text = parent.createdAt?.getTimeAgo()
        holder.tvCommentsCount.text = comments.count().toString()
        holder.tvContent.text = parent.content
        holder.tvLikesCount.text = parent.likes?.count().toString()

        holder.btnLike.setImageResource(
            if (parent.likes?.contains(username) == true) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
        holder.btnLike.setOnClickListener { likeParentComment(username) }
    }

    private fun onBindChildCommentHolder(holder: ChildCommentHolder, position: Int) {
        val comment = comments[position]
        holder.tvUsername.text = comment.username
        holder.tvTime.text = comment.createdAt?.getTimeAgo()
        holder.tvContent.text = comment.content
        holder.tvLikesCount.text = comment.likes?.count().toString()

        holder.btnLike.setImageResource(
            if (comment.likes?.contains(username) == true) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
        holder.btnLike.setOnClickListener { likeChildComment(position, username) }
        holder.card.setOnClickListener {  }
    }

    override fun getItemCount(): Int = comments.count() + 1

    abstract fun likeParentComment(username: String)

    abstract fun likeChildComment(position: Int, username: String)

    companion object {
        const val PARENT = 0
        const val COMMENT = 1
    }

    inner class ParentCommentHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.card_parent_comment)
        val tvUsername: TextView = view.findViewById(R.id.text_comment_parent_username)
        val tvTime: TextView = view.findViewById(R.id.text_comment_parent_time)
        val tvContent: TextView = view.findViewById(R.id.text_comment_parent_content)
        val tvCommentsCount: TextView = view.findViewById(R.id.text_comment_parent_comments_count)
        val tvLikesCount: TextView = view.findViewById(R.id.text_comment_parent_likes_count)
        val btnLike: ImageButton = view.findViewById(R.id.btn_comment_parent_like)
    }

    inner class ChildCommentHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: ConstraintLayout = view.findViewById(R.id.card_child_comment)
        val tvUsername: TextView = view.findViewById(R.id.text_comment_child_username)
        val tvTime: TextView = view.findViewById(R.id.text_comment_child_time)
        val tvContent: TextView = view.findViewById(R.id.text_comment_child_content)
        val tvLikesCount: TextView = view.findViewById(R.id.text_comment_child_likes_count)
        val btnLike: ImageButton = view.findViewById(R.id.btn_comment_child_like)
    }

}