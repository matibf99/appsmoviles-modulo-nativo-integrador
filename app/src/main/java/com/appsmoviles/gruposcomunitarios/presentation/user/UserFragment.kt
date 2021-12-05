package com.appsmoviles.gruposcomunitarios.presentation.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appsmoviles.gruposcomunitarios.databinding.FragmentUserBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainAcitivityViewModel
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import com.appsmoviles.gruposcomunitarios.presentation.UserStatus

class UserFragment : Fragment() {

    private val mainViewModel: MainAcitivityViewModel by activityViewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

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

                binding.userLayoutRegistered.visibility = View.VISIBLE
                binding.userLayoutNoRegistered.visibility = View.GONE
            } else {
                binding.userLayoutNoRegistered.visibility = View.VISIBLE
                binding.userLayoutRegistered.visibility = View.GONE
            }
        })

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

    companion object {
        private const val TAG = "UserFragment"

        @JvmStatic
        fun newInstance() = UserFragment()
    }
}