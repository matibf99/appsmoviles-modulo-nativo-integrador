package com.appsmoviles.gruposcomunitarios.presentation.postcomments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentPostCommentsBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.UserStatus
import com.appsmoviles.gruposcomunitarios.presentation.post.PostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCommentsFragment : Fragment() {

    private val args: PostCommentsFragmentArgs by navArgs()

    private val viewModel: PostCommentsViewModel by viewModels()
    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    private var _binding: FragmentPostCommentsBinding? = null
    private val binding: FragmentPostCommentsBinding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: PostCommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostCommentsBinding.inflate(inflater, container, false)

        viewModel.setPost(args.post)
        viewModel.setParent(args.parent)
        viewModel.setComments(args.comments.toList())

        Log.d(TAG, "onCreateView: parent: ${args.parent}")

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewComments.layoutManager = linearLayoutManager

        adapter = object : PostCommentsAdapter(
            mainViewModel.user.value?.username ?: "",
            viewModel.parent.value!!,
            viewModel.comments.value!!
        ) {
            override fun likeParentComment(username: String) {
                viewModel.likeParentComment(username)
                adapter.notifyItemChanged(0)
            }

            override fun likeChildComment(position: Int, username: String) {
                viewModel.likeChildComment(position, username)
                adapter.notifyItemChanged(position + 1)
            }

        }
        binding.recyclerViewComments.adapter = adapter

        viewModel.parent.observe(requireActivity(), {
            adapter.parent = it
            adapter.notifyItemChanged(0)
        })

        viewModel.comments.observe(requireActivity(), {
            adapter.comments = it
            adapter.notifyDataSetChanged()
        })

        mainViewModel.userStatus.observe(requireActivity(), {
            when(it) {
                UserStatus.SUCCESS -> {
                    adapter.username = mainViewModel.user.value?.username ?: ""
                    adapter.notifyDataSetChanged()
                }
                UserStatus.LOADING -> Log.d(TAG, "onCreateView: loading user")
                UserStatus.ERROR -> Log.d(TAG, "onCreateView: error loading user")
                else -> Log.d(TAG, "onCreateView: unknown state")
            }
        })

        binding.fabAddComment.setOnClickListener {
            val post = viewModel.post.value!!
            val parent = viewModel.parent.value!!
            val action = PostCommentsFragmentDirections.actionPostCommentsFragmentToCreateCommentFragment(post, parent)

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
        private const val TAG = "PostCommentsFragment"

        fun newInstance() = PostCommentsFragment()
    }

}