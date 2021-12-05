package com.appsmoviles.gruposcomunitarios.presentation.createcomment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsmoviles.gruposcomunitarios.domain.entities.Post
import com.appsmoviles.gruposcomunitarios.domain.entities.PostComment
import com.appsmoviles.gruposcomunitarios.domain.usecases.CreateCommentUseCase
import com.appsmoviles.gruposcomunitarios.utils.FieldStatus
import com.appsmoviles.gruposcomunitarios.utils.Res
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateCommentStatus(val message: String? = null) {
    object Success : CreateCommentStatus()
    object Loading : CreateCommentStatus()
    class Error(message: String?) : CreateCommentStatus(message)
}

@HiltViewModel
class CreateCommentViewModel @Inject constructor(
    private val createCommentUseCase: CreateCommentUseCase
) : ViewModel() {

    private val _post: MutableLiveData<Post> = MutableLiveData()
    val post: LiveData<Post> get() = _post

    private val _commentParent: MutableLiveData<PostComment?> = MutableLiveData()
    val commentParent: LiveData<PostComment?> get() = _commentParent

    private val _commentStatus: MutableLiveData<CreateCommentStatus> = MutableLiveData()
    val commentStatus: LiveData<CreateCommentStatus> get() = _commentStatus

    private val _commentFormStatus: MutableLiveData<FieldStatus> = MutableLiveData(FieldStatus.EMPTY)
    val commentFormStatus: LiveData<FieldStatus> get() = _commentFormStatus

    private val _commentContent: MutableLiveData<String> = MutableLiveData()
    val commentContent: LiveData<String> get() = _commentContent

    fun setPost(post: Post) {
        _post.value = post
    }

    fun setCommentParent(postComment: PostComment?) {
        _commentParent.value = postComment
    }

    fun setCommentContent(content: String) {
        _commentContent.value = content
        validateContent()
    }

    fun isFormValid(): Boolean {
        validateContent()
        return commentFormStatus.value == FieldStatus.VALID
    }

    private fun validateContent() {
        if (commentContent.value?.isEmpty() == true || commentContent.value == null)
            _commentFormStatus.value = FieldStatus.EMPTY
        else
            _commentFormStatus.value = FieldStatus.VALID
    }

    fun createComment(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            createCommentUseCase.createComment(
                groupId = post.value?.groupId!!,
                postId = post.value?.documentId!!,
                username = username,
                content = commentContent.value!!,
                parentId = commentParent.value?.documentId
            ).collect {
                when(it) {
                    is Res.Success -> _commentStatus.postValue(CreateCommentStatus.Success)
                    is Res.Loading -> _commentStatus.postValue(CreateCommentStatus.Loading)
                    is Res.Error -> _commentStatus.postValue(CreateCommentStatus.Error(it.message))
                }
            }
        }
    }
}