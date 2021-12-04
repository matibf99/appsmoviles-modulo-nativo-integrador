package com.appsmoviles.gruposcomunitarios.presentation.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentHomeBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.UserStatus
import com.appsmoviles.gruposcomunitarios.presentation.adapters.PostsAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: initiating")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar!!.setTitle(R.string.fragment_home_title)

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHome.layoutManager = linearLayoutManager

        adapter = object : PostsAdapter(mainViewModel.user.value?.username ?: "", viewModel.posts.value!!) {
            override fun onPostClickListener(position: Int) {
                val post = viewModel.posts.value!![position]
                val action = HomeFragmentDirections.actionHomeFragmentToPostFragment(post)
                findNavController().navigate(action)
            }

            override fun onLikeListener(position: Int, username: String) {
                viewModel.likePost(position, username)
                adapter.notifyItemChanged(position)
            }

            override fun onOpenGroupListener(position: Int) {
                val groupId = viewModel.posts.value!![position].groupId
                val action = HomeFragmentDirections.actionHomeFragmentToGroupFragment(groupId = groupId)
                findNavController().navigate(action)
            }
        }
        binding.recyclerViewHome.adapter = adapter

        binding.swipeRefreshHome.setOnRefreshListener {
            viewModel.getPosts()
        }

        mainViewModel.userStatus.observe(viewLifecycleOwner, {
            when(it) {
                UserStatus.SUCCESS -> adapter.username = mainViewModel.user.value?.username ?: ""
                UserStatus.LOADING -> Log.d(TAG, "onCreateView: loading user")
                UserStatus.ERROR -> Log.d(TAG, "onCreateView: error in loading user")
                else -> Log.d(TAG, "onCreateView: prevent null")
            }
        })

        viewModel.status.observe(viewLifecycleOwner, {
            when(it) {
                HomePostsStatus.Success -> {
                    binding.progressHome.visibility = View.GONE
                    binding.swipeRefreshHome.isRefreshing = false
                    Log.d(TAG, "onCreateView: success")
                }
                HomePostsStatus.Loading -> {
                    binding.progressHome.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading")
                }
                is HomePostsStatus.Error -> {
                    binding.progressHome.visibility = View.GONE
                    binding.swipeRefreshHome.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: error: ${it.message}")
                }
            }
        })

        viewModel.posts.observe(viewLifecycleOwner, {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        (activity as MainActivity?)!!.displayHomeButton(false)
        super.onResume()
    }

    companion object {
        private const val TAG = "HomeFragment"

        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }
}