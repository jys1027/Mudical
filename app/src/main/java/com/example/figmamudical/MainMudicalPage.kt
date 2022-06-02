package com.example.figmamudical

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentMainMudicalPageBinding

class MainMudicalPage : Fragment() {

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentMainMudicalPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainMudicalPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        putWeekScheduleText()
        binding.apply {
            // Assign the fragment
            mainmudicalpageFragment = this@MainMudicalPage
        }
    }

    private fun putWeekScheduleText() {
        var weekString: MutableList<String>
        var segmentString: MutableList<String>
        var nowWeek: Int

        val curUsername = viewModel.getCurUsername()
        viewModel.getUser(curUsername).observe(this.viewLifecycleOwner) { selectedUser ->
            val (first, second) = getWeekString(selectedUser.joinDate)
            weekString = first
            nowWeek = second
            segmentString = getSegmentString(selectedUser.userChoice1, selectedUser.userChoice2)

            //여기도 마찬가지로 이쪽 block 밖에서 업데이트하려하면 concurrency 문제로 터진다
            binding.thisWeekText.text = weekString[nowWeek]
            binding.thisWeekSegment.text = segmentString[nowWeek]
        }
    }

    fun goToSegment() {
        var weekString: MutableList<String>
        var segmentString: MutableList<String>
        var nowWeek: Int
        val curUsername = viewModel.getCurUsername()
        viewModel.getUser(curUsername).observe(this.viewLifecycleOwner) { selectedUser ->
            val (first, second) = getWeekString(selectedUser.joinDate)
            weekString = first
            nowWeek = second
            segmentString = getSegmentString(selectedUser.userChoice1, selectedUser.userChoice2)

            if(weekString.isEmpty()) return@observe
            if(segmentString[nowWeek].contains("송라이팅"))
                findNavController().navigate(R.id.action_mainMudicalPage_to_songwritingPage)
            else if(segmentString[nowWeek].contains("즉흥악기연주"))
                findNavController().navigate(R.id.action_mainMudicalPage_to_instrumentplayPage)
        }
    }

    fun forceSegment(c: Int) {
        if(c==1)
            findNavController().navigate(R.id.action_mainMudicalPage_to_songwritingPage)
        if(c==2)
            findNavController().navigate(R.id.action_mainMudicalPage_to_instrumentplayPage)
    }

    fun barBelow(c: Int) {
        when(c) {
            1 -> findNavController().navigate(R.id.action_mainMudicalPage_to_mainHomePage)
            2 -> findNavController().navigate(R.id.action_mainMudicalPage_to_mainMyPage)
        }
    }

}