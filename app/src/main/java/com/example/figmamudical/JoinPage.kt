package com.example.figmamudical

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentJoinPageBinding
import java.text.SimpleDateFormat
import java.util.*

class JoinPage : Fragment() {

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentJoinPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJoinPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // Assign the fragment
            joinpageFragment = this@JoinPage
        }
    }

    fun tryJoin() {
        val username: String = binding.joinTextUsername.text.toString()
        val password: String = binding.joinTextPassword.text.toString()
        val passwordCheck: String = binding.joinTextPasswordCheck.text.toString()
        val email: String = binding.joinTextEmail.text.toString()

        Log.d("CHECKER", "join try username=$username, password=$password, email=$email")

        viewModel.getUser(username).observe(this.viewLifecycleOwner) { selectedUser ->
            if(selectedUser != null){
                Log.d("CHECKER", "same id exists")
                Toast.makeText(activity, "동일한 아이디가 존재합니다", Toast.LENGTH_LONG).show()
                return@observe
            }
            else if(password != passwordCheck){
                Log.d("CHECKER", "password checker failed")
                Toast.makeText(activity, "비밀번호가 다릅니다", Toast.LENGTH_LONG).show()
                return@observe
            }
            else{
                val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                val dateString = formatter.format(Calendar.getInstance().time)
                Log.d("CHECKER", "now date = $dateString")
                viewModel.addUser(username, password, email, 0, 0, dateString, 0)
                Toast.makeText(activity, "회원가입 성공", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_joinPage_to_welcomePage)
            }
        }
    }
}