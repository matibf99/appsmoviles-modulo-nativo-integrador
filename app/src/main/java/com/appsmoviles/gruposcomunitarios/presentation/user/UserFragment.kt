package com.appsmoviles.gruposcomunitarios.presentation.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.appsmoviles.gruposcomunitarios.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth

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
        binding.passwordEditText.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }

        binding.loginButton.setOnClickListener {
            if(binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty() ) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(),"SE LOGEO",Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(requireContext(),"NO SE LOGEO",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        binding.nameEditText.setText(viewModel.name.value?.toString())
        binding.nameEditText.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }

        binding.surnameEditText.setText(viewModel.surname.value?.toString())
        binding.surnameEditText.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }

        binding.usernameEditText.setText(viewModel.username.value?.toString())
        binding.usernameEditText.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }

        binding.dniEditText.setText(viewModel.username.value?.toString())
        binding.usernameEditText.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }

        binding.singUpButton.setOnClickListener {
            viewModel.registerUser()
        }
        viewModel.status.observe(viewLifecycleOwner,{
            when(it){
                UserStatus.FAILED -> Toast.makeText(requireContext(), "FALLO EN EL LOG!!", Toast.LENGTH_SHORT).show()
                UserStatus.LOADED -> Toast.makeText(requireContext(), "LOGEADO!!", Toast.LENGTH_SHORT).show()
            }
        })

        return view
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