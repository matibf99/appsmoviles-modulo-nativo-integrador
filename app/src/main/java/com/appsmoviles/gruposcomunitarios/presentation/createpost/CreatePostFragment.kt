package com.appsmoviles.gruposcomunitarios.presentation.createpost

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentCreatePostBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.utils.FieldStatus
import com.appsmoviles.gruposcomunitarios.utils.permissions.PermissionsManager
import com.appsmoviles.gruposcomunitarios.utils.storage.getImageUriTakenWithCamera
import com.appsmoviles.gruposcomunitarios.utils.storage.pickImageFromCameraIntent
import com.appsmoviles.gruposcomunitarios.utils.storage.pickImageFromGalleryIntent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private val viewModel: CreatePostViewModel by viewModels()
    private val args: CreatePostFragmentArgs by navArgs()

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        viewModel.setGroup(args.group)
        (activity as AppCompatActivity).supportActionBar!!.title =
            "${resources.getString(R.string.fragment_create_post_title)} (${viewModel.group.value!!.name})"

        binding.editPostTitle.editText?.setText(viewModel.title.value ?: "")
        binding.editPostContent.editText?.setText(viewModel.content.value ?: "")

        viewModel.createPostStatus.observe(viewLifecycleOwner, {
            when (it) {
                CreatePostStatus.Successful -> {
                    binding.progressCreatePost.visibility = View.GONE
                    findNavController().popBackStack()
                }
                CreatePostStatus.Loading -> {
                    binding.progressCreatePost.visibility = View.VISIBLE
                }
                is CreatePostStatus.Error -> {
                    binding.progressCreatePost.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        it.message ?: resources.getString(R.string.crate_post_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

        viewModel.imageUri.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.layoutPostCreateImage.visibility = View.VISIBLE

                val filename = DocumentFile.fromSingleUri(requireContext(), it)?.name
                binding.postCreateImageName.text = filename ?: "image.jpeg"

                Glide.with(requireContext())
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(binding.postCreateImagePreview)
            } else {
                binding.layoutPostCreateImage.visibility = View.GONE
            }
        })

        viewModel.location.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.layoutPostLocation.visibility = View.VISIBLE

                val location = viewModel.location.value!!
                val locationText = "${location.latitude}, ${location.longitude}"
                binding.textPostLocationInfo.text = locationText
            } else {
                binding.layoutPostLocation.visibility = View.GONE
            }
        })

        binding.btnPostCreateRemoveImage.setOnClickListener {
            viewModel.setImageUri(null)
        }

        binding.btnPostCreateRemoveLocation.setOnClickListener {
            viewModel.removeLocation()
        }

        binding.editPostTitle.editText?.doAfterTextChanged {
            viewModel.setTitle(it.toString())
        }

        viewModel.fieldTitleStatus.observe(viewLifecycleOwner, {
            when (it) {
                FieldStatus.EMPTY -> binding.editPostTitle.error =
                    getString(R.string.create_post_validation_title_invalid)
                else -> binding.editPostTitle.isErrorEnabled = false
            }
        })

        binding.editPostContent.editText?.doAfterTextChanged {
            viewModel.setContent(it.toString())
        }

        binding.btnPostCreateAddImageCamera.setOnClickListener {
            startActivityForResult(
                pickImageFromCameraIntent(requireActivity().applicationContext),
                REQUEST_CAPTURE_IMAGE
            )
        }

        binding.btnPostCreateAddImageStorage.setOnClickListener {
            startActivityForResult(
                pickImageFromGalleryIntent(),
                REQUEST_CAPTURE_IMAGE
            )
        }

        binding.btnPostAddLocation.setOnClickListener {
            if (PermissionsManager.isLocationPermissionGranted(requireContext()))
                viewModel.getLocation()
            else
                PermissionsManager.requestLocationPermission(requireActivity())
        }

        binding.btnCreatePost.setOnClickListener {
            if (viewModel.isFormValid()) {
                val imageUri = viewModel.imageUri.value

                if (imageUri != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        viewModel.imageUri.value
                    )
                    viewModel.createPost(bitmap)
                } else {
                    viewModel.createPost()
                }
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CAPTURE_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data?.data != null)
                        viewModel.setImageUri(data.data!!)
                    else
                        viewModel.setImageUri(getImageUriTakenWithCamera(requireActivity().applicationContext))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionsManager.LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            when (grantResults.first()) {
                PackageManager.PERMISSION_GRANTED -> viewModel.getLocation()
                else -> Log.d(TAG, "onRequestPermissionsResult: permission denied")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "CreatePostFragment"
        private const val REQUEST_CAPTURE_IMAGE = 1

        fun newInstance() = CreatePostFragment()
    }

}