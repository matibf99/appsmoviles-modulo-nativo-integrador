package com.appsmoviles.gruposcomunitarios.presentation.post

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsmoviles.gruposcomunitarios.databinding.FragmentPostBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.UserStatus
import com.appsmoviles.gruposcomunitarios.utils.helpers.MapLocation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : Fragment() {

    private val args: PostFragmentArgs by navArgs()

    private val viewModel: PostViewModel by viewModels()
    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        viewModel.setPost(args.post)
        (activity as AppCompatActivity).supportActionBar?.title = viewModel.post.value?.title ?: ""

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPost.layoutManager = linearLayoutManager

        adapter = object : PostAdapter(
            mainViewModel.user.value?.username ?: "",
            viewModel.post.value!!,
            viewModel.parentComments.value!!,
            viewModel.allComments.value!!
        ) {
            override fun likePost(username: String) {
                if (username.isNotEmpty()) {
                    viewModel.likePost(username)
                    adapter.notifyItemChanged(0)
                }
            }

            override fun likeComment(position: Int, username: String) {
                if (username.isNotEmpty()) {
                    viewModel.likeComment(position, username)
                    adapter.notifyItemChanged(position + 1)
                }
            }

            override fun onOpenGroupListener() {
                val groupId = viewModel.post.value!!.groupId
                val action = PostFragmentDirections.actionPostFragmentToGroupFragment(groupId = groupId)
                findNavController().navigate(action)
            }

            override fun onOpenCommentListener(position: Int) {
                val parent = viewModel.parentComments.value!![position]
                val childComments = viewModel.allComments.value!!
                    .filter { it.parent == parent.documentId }
                    .toTypedArray()

                val action = PostFragmentDirections.actionPostFragmentToPostCommentsFragment(childComments, parent, viewModel.post.value!!)
                findNavController().navigate(action)
            }

            override fun onOpenImageListener() {
                val post = viewModel.post.value!!
                val action = PostFragmentDirections.actionPostFragmentToPhotoFragment(
                    imageUrl = post.photo ?: "",
                    title = post.title
                )
                findNavController().navigate(action)
            }

            override fun onOpenLocationListener() {
                val post = viewModel.post.value!!
                val latitude = post.latitude
                val longitude = post.longitude

                if (latitude != null && longitude != null) {
                    val action = PostFragmentDirections.actionPostFragmentToMapFragment(
                        MapLocation(
                            latitude.toDouble(),
                            longitude.toDouble()
                        )
                    )
                    findNavController().navigate(action)
                }
            }
        }
        binding.recyclerViewPost.adapter = adapter

        binding.swipeRefreshPost.setOnRefreshListener {
            viewModel.refresh()
        }

        mainViewModel.userStatus.observe(viewLifecycleOwner, {
            when(it) {
                UserStatus.SUCCESS -> {
                    binding.fabAddComment.visibility = View.VISIBLE
                    adapter.username = mainViewModel.user.value?.username ?: ""
                }
                UserStatus.LOADING -> {
                    binding.fabAddComment.visibility = View.GONE
                    Log.d(TAG, "onCreateView: loading user")
                }
                UserStatus.ERROR -> {
                    binding.fabAddComment.visibility = View.GONE
                    Log.d(TAG, "onCreateView: error loading user")
                }
                else -> Log.d(TAG, "onCreateView: unknown state")
            }
        })

        viewModel.commentsStatus.observe(viewLifecycleOwner, {
            when(it) {
                PostCommentsStatus.Success -> {
                    if (viewModel.postStatus.value !is PostStatus.Loading) {
                        binding.progressPost.visibility = View.GONE
                        binding.swipeRefreshPost.isRefreshing = false
                    }

                    adapter.parentComments = viewModel.parentComments.value!!
                    adapter.allComments = viewModel.allComments.value!!
                    adapter.notifyDataSetChanged()

                    Log.d(TAG, "onCreateView: success loading comments")
                }
                PostCommentsStatus.Loading -> {
                    binding.progressPost.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading comments")
                }
                is PostCommentsStatus.Error -> {
                    if (viewModel.postStatus.value !is PostStatus.Loading) {
                        binding.progressPost.visibility = View.GONE
                        binding.swipeRefreshPost.isRefreshing = false
                    }

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: error loading comments: ${it.message}")
                }
            }

            viewModel.postStatus.observe(viewLifecycleOwner, {
                when(it) {
                     PostStatus.Success -> {
                        if (viewModel.postStatus.value !is PostStatus.Loading) {
                            binding.progressPost.visibility = View.GONE
                            binding.swipeRefreshPost.isRefreshing = false
                        }

                         adapter.post = viewModel.post.value!!
                         adapter.notifyItemChanged(0)

                         Log.d(TAG, "onCreateView: success loading post")
                     }
                    PostStatus.Loading -> {
                        Log.d(TAG, "onCreateView: loading post")
                    }
                    is PostStatus.Error -> {
                        if (viewModel.postStatus.value !is PostStatus.Loading) {
                            binding.progressPost.visibility = View.GONE
                            binding.swipeRefreshPost.isRefreshing = false
                        }

                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        Log.d(TAG, "onCreateView: error loading post: ${it.message}")
                    }
                }
            })

        })

        binding.fabAddComment.setOnClickListener {
            val post = viewModel.post.value!!
            val action = PostFragmentDirections.actionPostFragmentToCreateCommentFragment(post)

            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onResume() {
        (activity as MainActivity?)!!.displayHomeButton(true)
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "PostFragment"

        fun newInstance() = PostFragment()
    }
}