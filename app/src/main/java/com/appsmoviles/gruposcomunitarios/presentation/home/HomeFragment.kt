package com.appsmoviles.gruposcomunitarios.presentation.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentHomeBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.adapters.PostsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    val viewModel: HomeViewModel by viewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar!!.title = "Home"

        viewModel.status.observe(requireActivity(), {
            when(it) {
                HomePostsStatus.Success -> {
                    binding.progressHome.visibility = View.GONE
                    Log.d(TAG, "onCreateView: success")
                }
                HomePostsStatus.Loading -> {
                    binding.progressHome.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading")
                }
                is HomePostsStatus.Error -> {
                    binding.progressHome.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: error: ${it.message}")
                }
            }
        })

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHome.layoutManager = linearLayoutManager

        adapter = object : PostsAdapter(viewModel.username, viewModel.posts.value!!) {
            override fun onPostClickListener(position: Int) {
            }

            override fun onLikeListener(position: Int) {
                viewModel.likePost(position)
                adapter.notifyItemChanged(position)
            }

            override fun onOpenGroupListener(position: Int) {
                val groupId = viewModel.posts.value!![position].groupId
                val action = HomeFragmentDirections.actionHomeFragmentToGroupFragment(groupId = groupId)
                findNavController().navigate(action)
            }
        }
        binding.recyclerViewHome.adapter = adapter

        viewModel.posts.observe(requireActivity(), {
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
        fun newInstance(param1: String, param2: String) =
            HomeFragment()
    }
}