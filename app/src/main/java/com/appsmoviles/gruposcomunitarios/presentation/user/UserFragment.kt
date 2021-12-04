package com.appsmoviles.gruposcomunitarios.presentation.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.appsmoviles.gruposcomunitarios.databinding.FragmentUserBinding

@AndroidEntryPoint
class UserFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()

    // Only is valid between onCreateView and onDestroyView
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root


        (activity as AppCompatActivity).supportActionBar!!.title = "User"

        binding.emailEditText.setText(viewModel.email.value?.toString())
        binding.emailEditText.doAfterTextChanged {
            viewModel.setEmail(it.toString())
        }
        binding.passwordEditText.setText(viewModel.password.value?.toString())
        binding.emailEditText.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }

        binding.loginButton.setOnClickListener {
            viewModel.signIn()
        }
        binding.singUpButton.setOnClickListener {
            viewModel.registerUser()
        }
        viewModel.status.observe(viewLifecycleOwner,{
            when(it){
                UserStatus.LOADED -> Toast.makeText(requireContext(), "LOGEADO!!", Toast.LENGTH_SHORT).show()
                UserStatus.FAILED -> Toast.makeText(requireContext(), "FALLO EN EL LOG!!", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }

    private fun takeDatesOfUsers() {

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment()
    }
}