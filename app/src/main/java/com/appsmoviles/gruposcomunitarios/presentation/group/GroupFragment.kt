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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appsmoviles.gruposcomunitarios.databinding.FragmentGroupBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.adapters.PostsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupFragment : Fragment() {

    private val args: GroupFragmentArgs by navArgs()

    private val viewModel: GroupViewModel by viewModels()

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

        viewModel.groupStatus.observe(requireActivity(), {
            when(it) {
                GroupStatus.Success -> {
                    if (viewModel.status.value == GroupPostsStatus.Success)
                        binding.progressGroupPosts.visibility = View.GONE

                    (activity as AppCompatActivity).supportActionBar?.title = viewModel.group.value!!.name
                    viewModel.getPosts()
                    Log.d(TAG, "onCreateView: success")
                }
                GroupStatus.Loading -> {
                    binding.progressGroupPosts.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading")
                }
                is GroupStatus.Error -> {
                    if (viewModel.status.value == GroupPostsStatus.Success)
                        binding.progressGroupPosts.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: error: ${it.message}")
                }
            }
        })
        
        viewModel.status.observe(requireActivity(), {
            when(it) {
                GroupPostsStatus.Success -> {
                    if (viewModel.groupStatus.value == GroupStatus.Success)
                        binding.progressGroupPosts.visibility = View.GONE
                    Log.d(TAG, "onCreateView: success")
                }
                GroupPostsStatus.Loading -> {
                    binding.progressGroupPosts.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading")
                }
                is GroupPostsStatus.Error -> {
                    if (viewModel.groupStatus.value == GroupStatus.Success)
                        binding.progressGroupPosts.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onCreateView: error: ${it.message}")
                }
            }
        })

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

        adapter = object : PostsAdapter(viewModel.username, viewModel.posts.value!!) {
            override fun onPostClickListener(position: Int) {
            }

            override fun onLikeListener(position: Int) {
                viewModel.likePost(position)
                adapter.notifyItemChanged(position)
            }

            override fun onOpenGroupListener(position: Int) {
            }
        }
        binding.recyclerViewGroupPosts.adapter = adapter

        viewModel.posts.observe(requireActivity(), {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

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
            android.R.id.home -> activity?.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "GroupFragment"
        
        @JvmStatic
        fun newInstance() = GroupFragment()
    }
}