package com.example.figmamudical

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentMainMyPageBinding

class MainMyPage : Fragment() {

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentMainMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val curUsername: String = viewModel.getCurUsername()
        var p1: Int
        var p2: Int
        var joinDate: String = resources.getString(R.string.temp_date)

        Log.d("CHECKER", "now temp joinDate = $joinDate")

        binding.mainMyPageUsernameText.text = curUsername
        viewModel.getUser(curUsername).observe(this.viewLifecycleOwner) { selectedUser ->
            p1 = selectedUser.userChoice1
            binding.mainMyPageUserPreference1.text = when(p1) {
                1->"#사회기술-개인교류"
                2->"#정서"
                3->"#인지"
                4->"#운동"
                else->"#모두"
            }
            p2 = selectedUser.userChoice2
            binding.mainMyPageUserPreference2.text = when(p2) {
                1->"#대중가요"
                2->"#클래식"
                3->"#팝송"
                else->"#모두"
            }
            joinDate = selectedUser.joinDate
            setProgressBar(selectedUser.segmentsDone)

            Log.d("CHECKER", "now found joinDate = $joinDate")

            //observe가 thread hold시킬 수 있음에 유의하자 변수 업데이트 순서가 코드 순서대로가 아니다
            val(first,second) = getWeekString(joinDate)
            val weekString: MutableList<String> = first
            val nowWeek: Int = second
            val segmentString: MutableList<String> = getSegmentString(p1, p2)

            binding.mainMyPageWeekText1.text = weekString[nowWeek]
            binding.mainMyPageWeekText2.text = weekString[nowWeek]
            binding.box65TextTop.text = weekString[nowWeek]
            binding.box65TextBottom.text = segmentString[nowWeek]
        }

        binding.apply {
            // Assign the fragment
            mainmypageFragment = this@MainMyPage
        }

    }

    private fun setProgressBar(d: Int) {
        binding.percentageDone.text = when(d) {
            0->""
            1->"20%"
            2->"40%"
            3->"60%"
            4->"80%"
            else->"100%"
        }
        val factor: Float = context?.resources?.displayMetrics?.density ?: 1F
        binding.rectangle159.layoutParams.width = (factor * (13+((300/5) * (if(d<5) d else 5)))).toInt()
    }

    fun goToLog() {
        //log page를 만들만큼 길지가 않아서 그냥 home으로 보낸다
        findNavController().navigate(R.id.action_mainMyPage_to_mainHomePage)
    }

    fun barBelow(c: Int) {
        when(c) {
            0 -> findNavController().navigate(R.id.action_mainMyPage_to_mainMudicalPage)
            1 -> findNavController().navigate(R.id.action_mainMyPage_to_mainHomePage)
        }
    }

}