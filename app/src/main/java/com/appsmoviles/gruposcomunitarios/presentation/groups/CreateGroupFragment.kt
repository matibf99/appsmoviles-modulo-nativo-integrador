package com.appsmoviles.gruposcomunitarios.presentation.groups

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.utils.storage.pickImageFromCameraIntent
import com.appsmoviles.gruposcomunitarios.utils.storage.pickImageFromGalleryIntent
import androidx.fragment.app.viewModels
import com.appsmoviles.gruposcomunitarios.databinding.FragmentCreateGroupBinding
import com.appsmoviles.gruposcomunitarios.utils.storage.getImageUriTaken

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File


class CreateGroupFragment : Fragment() {

    private val viewModel: CreateGroupFragmentViewModel by viewModels()

    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar!!.title = "Create new group"

        viewModel.imageUri.observe(requireActivity(), {
            Glide.with(requireContext())
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(binding.photoPreview)
        })

        binding.btnPickCamera.setOnClickListener {
            startActivityForResult(pickImageFromCameraIntent(requireActivity()), REQUEST_CAPTURE_IMAGE)
        }

        binding.btnPickStorage.setOnClickListener {
            startActivityForResult(pickImageFromGalleryIntent(), REQUEST_CAPTURE_IMAGE)
        }

        return view
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CAPTURE_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data?.data != null)
                        viewModel.setImageUri(data.data!!)
                    else
                        viewModel.setImageUri(getImageUriTaken(requireActivity()))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val TAG = "CreateGroupFragment"
        private const val REQUEST_CAPTURE_IMAGE = 1

        @JvmStatic
        fun newInstance() = CreateGroupFragment()
    }
}