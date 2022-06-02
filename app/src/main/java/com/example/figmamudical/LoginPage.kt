package com.example.figmamudical

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentLoginPageBinding
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext

class LoginPage : Fragment() {

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // Assign the fragment
            loginpageFragment = this@LoginPage
        }
    }

    fun findId(){
        Toast.makeText(activity, "아직 지원하지 않는 기능입니다.", Toast.LENGTH_LONG).show()
    }

    fun findPw(){
        Toast.makeText(activity, "아직 지원하지 않는 기능입니다.", Toast.LENGTH_LONG).show()
    }

    fun newUserJoin(){
        findNavController().navigate(R.id.action_loginPage_to_joinPage)
    }

    fun tryLogin(){
        val username: String = binding.loginTextUsername.text.toString()
        val password: String = binding.loginTextPassword.text.toString()
        Log.d("CHECKER", "login try id=$username, pw=$password")
        viewModel.getUser(username).observe(this.viewLifecycleOwner) { selectedUser ->
            if(selectedUser == null)
                Log.d("CHECKER", "user not found")
            else
                Log.d("CHECKER", "user found, pw == ${selectedUser.userPassword}")
            if(selectedUser != null && selectedUser.userPassword == password){
                Log.d("CHECKER", "login pw matched")
                Toast.makeText(activity, "로그인 성공", Toast.LENGTH_LONG).show()
                viewModel.setCurUsername(username)
                if(selectedUser.userChoice1 == 0)
                    findNavController().navigate(R.id.action_loginPage_to_tutorialPage)
                else
                    findNavController().navigate(R.id.action_loginPage_to_mainHomePage)
            }
            else{
                Toast.makeText(activity, "비밀번호가 다르거나 없는 아이디입니다", Toast.LENGTH_LONG).show()
            }
        }
    }

}