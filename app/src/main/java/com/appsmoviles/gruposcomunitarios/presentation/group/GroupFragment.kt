package com.appsmoviles.gruposcomunitarios.presentation.group

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
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.databinding.FragmentGroupBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.adapters.PostsAdapter
import com.appsmoviles.gruposcomunitarios.presentation.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupFragment : Fragment() {

    private val args: GroupFragmentArgs by navArgs()

    private val viewModel: GroupViewModel by viewModels()
    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = ""

        args.let {
            if (it.group != null)
                viewModel.setGroup(it.group!!)
            else if (it.groupId != null)
                viewModel.setGroupId(it.groupId!!)
        }

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewGroupPosts.layoutManager = linearLayoutManager
        binding.recyclerViewGroupPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val firstVisible = linearLayoutManager.findFirstCompletelyVisibleItemPosition()

                if (firstVisible == 0)
                    binding.createPostFab.extend()
                else
                    binding.createPostFab.shrink()
            }
        })

        adapter = object : PostsAdapter(
            mainViewModel.user.value?.username ?: "",
            viewModel.posts.value!!
        ) {
            override fun onPostClickListener(position: Int) {
                val post = viewModel.posts.value!![position]
                val action = GroupFragmentDirections.actionGroupFragmentToPostFragment(post)
                findNavController().navigate(action)
            }

            override fun onLikeListener(position: Int, username: String) {
                viewModel.likePost(position, username)
                adapter.notifyItemChanged(position)
            }

            override fun onOpenGroupListener(position: Int) {
            }

            override fun onOpenImageListener(position: Int) {
                val post = viewModel.posts.value!![position]
                val action = GroupFragmentDirections.actionGroupFragmentToPhotoFragment(
                    imageUrl = post.photo ?: "",
                    title = post.title
                )
                findNavController().navigate(action)
            }
        }
        binding.recyclerViewGroupPosts.adapter = adapter

        viewModel.groupStatus.observe(viewLifecycleOwner, {
            Log.d(TAG, "onCreateView: observing groupStatus")
            when(it) {
                GroupStatus.Success -> {
                    if (viewModel.status.value !is GroupPostsStatus.Loading)
                        binding.progressGroupPosts.visibility = View.GONE

                    viewModel.getPosts()
                    (activity as AppCompatActivity).supportActionBar?.title = viewModel.group.value!!.name
                    Log.d(TAG, "onCreateView: success loading group")
                }
                GroupStatus.Loading -> {
                    binding.progressGroupPosts.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading group")
                }
                is GroupStatus.Error -> {
                    if (viewModel.status.value !is GroupPostsStatus.Loading)
                        binding.progressGroupPosts.visibility = View.GONE

                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: error loading group: ${it.message}")
                }
            }
        })

        viewModel.status.observe(viewLifecycleOwner, {
            Log.d(TAG, "onCreateView: observing status")
            when(it) {
                GroupPostsStatus.Success -> {
                    if (viewModel.groupStatus.value !is GroupStatus.Loading)
                        binding.progressGroupPosts.visibility = View.GONE

                    binding.swipeRefreshGroup.isRefreshing = false
                    Log.d(TAG, "onCreateView: success loading posts")
                }
                GroupPostsStatus.Loading -> {
                    binding.progressGroupPosts.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading loading posts")
                }
                is GroupPostsStatus.Error -> {
                    if (viewModel.groupStatus.value !is GroupStatus.Loading)
                        binding.progressGroupPosts.visibility = View.GONE

                    binding.swipeRefreshGroup.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: error posts: ${it.message}")
                }
            }
        })

        viewModel.posts.observe(viewLifecycleOwner, {
            Log.d(TAG, "onCreateView: observing posts")
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

        binding.swipeRefreshGroup.setOnRefreshListener {
            Log.d(TAG, "onCreateView: refreshing")
            viewModel.getPosts()
        }

        binding.createPostFab.setOnClickListener {
            val group = viewModel.group.value!!
            val action = GroupFragmentDirections.actionGroupFragmentToCreatePostFragment(group)
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        (activity as MainActivity?)!!.displayHomeButton(true)
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "GroupFragment"
        
        @JvmStatic
        fun newInstance() = GroupFragment()
    }
}