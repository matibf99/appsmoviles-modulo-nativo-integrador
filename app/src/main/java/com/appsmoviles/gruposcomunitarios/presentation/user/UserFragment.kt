package com.appsmoviles.gruposcomunitarios.presentation.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentUserBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.UserStatus
import com.appsmoviles.gruposcomunitarios.utils.locale.LocaleManager
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class UserFragment : Fragment() {

    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar!!.title = "User"

        mainViewModel.loadUser()

        mainViewModel.userStatus.observe(viewLifecycleOwner, {
            when(it) {
                UserStatus.SUCCESS -> {
                    binding.progressUser.visibility = View.GONE
                    Log.d(TAG, "onCreateView: user registered")
                }
                UserStatus.LOADING -> {
                    binding.progressUser.visibility = View.VISIBLE
                    Log.d(TAG, "onCreateView: loading user")
                }
                UserStatus.ERROR -> {
                    binding.progressUser.visibility = View.GONE
                    Log.d(TAG, "onCreateView: error loading user")
                }
            }
        })

        mainViewModel.user.observe(viewLifecycleOwner, {
            if (it != null) {
                val name = "${it.name} ${it.surname}"
                binding.userName.text = name
                binding.userEmail.text = it.email
                binding.userUsername.text = it.username

                Glide.with(requireContext())
                    .load(it.photo)
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.userPhoto)

                binding.userLayoutRegistered.visibility = View.VISIBLE
                binding.userLayoutNoRegistered.visibility = View.GONE
            } else {
                binding.userLayoutNoRegistered.visibility = View.VISIBLE
                binding.userLayoutRegistered.visibility = View.GONE
            }
        })

        binding.userPhoto.setOnClickListener {
            val imageUri = mainViewModel.user.value?.photo ?: ""
            val action = UserFragmentDirections.actionUserFragmentToPhotoFragment(imageUri)
            findNavController().navigate(action)
        }

        binding.btnUserRegister.setOnClickListener {
            val action = UserFragmentDirections.actionUserFragmentToUserRegisterFragment()
            findNavController().navigate(action)
        }

        binding.btnUserLogIn.setOnClickListener {
            val action = UserFragmentDirections.actionUserFragmentToUserLoginFragment()
            findNavController().navigate(action)
        }

        binding.btnUserLogOut.setOnClickListener {
            mainViewModel.logOut()
        }

        return view
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        (activity as MainActivity?)!!.displayHomeButton(false)
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_user, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_language -> {
                showChangeLanguageDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showChangeLanguageDialog() {
        val options = resources.getStringArray(R.array.language_options)
        var position = 0

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_language_title)
            .setSingleChoiceItems(options, 0) { dialog, which ->
                position = which
            }
            .setPositiveButton(R.string.dialog_positive_button_select) { _, _ ->
                when(position) {
                    0 -> LocaleManager.setLocale(requireContext(), LocaleManager.LOCALE_ES)
                    1 -> LocaleManager.setLocale(requireContext(), LocaleManager.LOCALE_EN)
                    2 -> LocaleManager.setLocale(requireContext(), LocaleManager.LOCALE_PT)
                }

                showDialogRestartApp()
            }
            .setNegativeButton(R.string.dialog_negative_button_cancel, null)
            .show()
    }

    private fun showDialogRestartApp() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_language_changed_title)
            .setMessage(R.string.select_language_changed_content)
            .setCancelable(false)
            .setPositiveButton(R.string.select_language_changed_positive_action) { _, _ ->
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton(R.string.dialog_negative_button_cancel, null)
            .show()
    }

    companion object {
        private const val TAG = "UserFragment"

        @JvmStatic
        fun newInstance() = UserFragment()
    }
}