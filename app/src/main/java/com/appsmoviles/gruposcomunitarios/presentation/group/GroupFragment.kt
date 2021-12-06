package com.appsmoviles.gruposcomunitarios.presentation.group

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentGroupBinding
import com.appsmoviles.gruposcomunitarios.domain.entities.Group
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.UserStatus
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

    private var menu: Menu? = null

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

        mainViewModel.userStatus.observe(viewLifecycleOwner, {
            when(it) {
                UserStatus.SUCCESS -> {
                    binding.createPostFab.visibility = View.VISIBLE
                    adapter.username = mainViewModel.user.value?.username ?: ""

                    Log.d(TAG, "onCreateView: user success")
                }
                UserStatus.LOADING -> {
                    binding.createPostFab.visibility = View.GONE
                    Log.d(TAG, "onCreateView: user loading")
                }
                UserStatus.ERROR -> {
                    binding.createPostFab.visibility = View.GONE
                    Log.d(TAG, "onCreateView: user error")
                }
            }
        })

        args.let {
            if (it.group != null)
                viewModel.setGroup(it.group!!)
            else if (it.groupId != null)
                viewModel.setGroupId(it.groupId!!)
        }

        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewGroupPosts.layoutManager = linearLayoutManager
        binding.recyclerViewGroupPosts.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
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
            when (it) {
                GroupStatus.Success -> {
                    if (viewModel.status.value !is GroupPostsStatus.Loading)
                        binding.progressGroupPosts.visibility = View.GONE

                    viewModel.getPosts()
                    (activity as AppCompatActivity).supportActionBar?.title =
                        viewModel.group.value!!.name
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
            when (it) {
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

        viewModel.group.observe(viewLifecycleOwner, {
            Log.d(TAG, "onCreateView: group changed")
            changeSubscribeMenuItem(it)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_group, menu)
        this.menu = menu

        val group = viewModel.group.value
        if (group != null)
            changeSubscribeMenuItem(group)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
            R.id.menu_like -> viewModel.subscribeToGroup(mainViewModel.user.value?.username ?: "")
        }

        return super.onOptionsItemSelected(item)
    }

    private fun changeSubscribeMenuItem(group: Group) {
        val username = mainViewModel.user.value?.username ?: ""
        val isSubscribed = group.subscribed?.contains(username) == true

        val menuItem = this.menu?.findItem(R.id.menu_like)
        menuItem?.apply {
            if (isSubscribed) {
                setTitle(R.string.menu_like)
                setIcon(R.drawable.ic_baseline_favorite_24)
            } else {
                setTitle(R.string.menu_unlike)
                setIcon(R.drawable.ic_baseline_favorite_border_24)
            }
        }
    }

    companion object {
        private const val TAG = "GroupFragment"

        @JvmStatic
        fun newInstance() = GroupFragment()
    }
}