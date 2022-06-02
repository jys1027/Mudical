package com.example.figmamudical

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.figmamudical.databinding.FragmentTutorialPageBinding
import android.util.Log
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController


class TutorialPage : Fragment() {

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var selectedBox1 = 0
    private var selectedBox2 = 0
    private var doNotChange = false

    private var tutorialState: Int = 0

    private var _binding: FragmentTutorialPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTutorialPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tutorialState = 0
        applyBindingChanges()
        binding.apply {
            // Assign the fragment
            tutorialpageFragment = this@TutorialPage
        }
    }

    private fun applyBindingChanges() {
        //val rectangleBox = getAndroidDrawable("@drawable/rectangle_56_shape")
        //val rectangleBox = resources.getDrawable(R.drawable.rectangle_56_shape, null) //higher api
        val rectangleBox = ResourcesCompat.getDrawable(resources, R.drawable.select_box, null) //both low and high api
        val saveButton = ResourcesCompat.getDrawable(resources, R.drawable.rectangle_gray, null) //both low and high api

        binding.tutorialMudiText.text = findTutorialMudiText(tutorialState)
        binding.tutorialChooseText1.text = findTutorialChooseText1(tutorialState)
        binding.tutorialChooseText2.text = findTutorialChooseText2(tutorialState)
        binding.tutorialChooseText3.text = findTutorialChooseText3(tutorialState)
        binding.tutorialChooseText4.text = findTutorialChooseText4(tutorialState)
        binding.tutorialChooseText5.text = findTutorialChooseText5(tutorialState)
        binding.saveButtonText.text = if(tutorialState == 4 || tutorialState == 7) resources.getString(R.string.save) else ""

        if(doNotChange)
            return

        if(binding.saveButtonText.text == "")
            binding.saveButton.background = null
        else
            binding.saveButton.background = saveButton

        if(binding.tutorialChooseText1.text == "")
            binding.tutorialChooseBox1.background = null
        else
            binding.tutorialChooseBox1.background = rectangleBox
        if(binding.tutorialChooseText2.text == "")
            binding.tutorialChooseBox2.background = null
        else
            binding.tutorialChooseBox2.background = rectangleBox
        if(binding.tutorialChooseText3.text == "")
            binding.tutorialChooseBox3.background = null
        else
            binding.tutorialChooseBox3.background = rectangleBox
        if(binding.tutorialChooseText4.text == "")
            binding.tutorialChooseBox4.background = null
        else
            binding.tutorialChooseBox4.background = rectangleBox
        if(binding.tutorialChooseText5.text == "")
            binding.tutorialChooseBox5.background = null
        else
            binding.tutorialChooseBox5.background = rectangleBox

    }

    private fun findTutorialMudiText(state: Int): String {
        return when (tutorialState) {
            4 -> "그럼, (${viewModel.getCurUsername()})님이 관심 있으신\n음악치료 영역은 무엇인지 알려주세요!"
            6 -> "뮤디가 (${viewModel.getCurUsername()}) 님에게 꼭 맞는 활동을\n" +
                    "찾을 수 있게 (${viewModel.getCurUsername()}) 님에 대해\n" +
                    "알려주실 수 있으신가요?"
            8 -> {
                "그럼 알려주신 정보를 바탕으로\n(${viewModel.getCurUsername()}) 님과 꼭 맞는 커리큘럼을 생성할게요!"
            }
            else -> resources.getStringArray(R.array.arrayTutorialMudi)[state]
        }
    }
    private fun findTutorialChooseText1(state: Int): String {
        return resources.getStringArray(R.array.arrayTutorialChoose1)[state]
    }
    private fun findTutorialChooseText2(state: Int): String {
        return resources.getStringArray(R.array.arrayTutorialChoose2)[state]
    }
    private fun findTutorialChooseText3(state: Int): String {
        return resources.getStringArray(R.array.arrayTutorialChoose3)[state]
    }
    private fun findTutorialChooseText4(state: Int): String {
        return resources.getStringArray(R.array.arrayTutorialChoose4)[state]
    }
    private fun findTutorialChooseText5(state: Int): String {
        return resources.getStringArray(R.array.arrayTutorialChoose5)[state]
    }

    fun chooseBox(boxId: Int){
        doNotChange = false
        when (tutorialState) {
            0 -> {
                selectedBox1 = 0
                if(boxId == 1 || boxId == 2)
                    tutorialState = 1
            }
            1-> {
                if(boxId == 1)
                    tutorialState = 2
                else if(boxId == 2)
                    tutorialState = 4
            }
            2 -> {
                tutorialState = 3
            }
            3 -> {
                if(boxId == 1 || boxId == 2)
                    tutorialState = 4
            }
            4 -> {
                when(boxId) {
                    1,2,3,4,5-> {
                        val rectangleBox = ResourcesCompat.getDrawable(resources, R.drawable.select_box, null) //both low and high api
                        val rectangleBox2 = ResourcesCompat.getDrawable(resources, R.drawable.selected_box, null) //both low and high api
                        fun getBox(w: Int): Drawable? { return if(boxId == w) rectangleBox2 else rectangleBox }
                        binding.tutorialChooseBox1.background = getBox(1)
                        binding.tutorialChooseBox2.background = getBox(2)
                        binding.tutorialChooseBox3.background = getBox(3)
                        binding.tutorialChooseBox4.background = getBox(4)
                        binding.tutorialChooseBox5.background = getBox(5)
                        binding.saveButton.background = ResourcesCompat.getDrawable(resources, R.drawable.rectangle_selected, null)
                        selectedBox1 = boxId
                        Log.d("CHECKER", "select1 = $selectedBox1")
                        doNotChange = true
                    }
                    6-> {
                        val curUsername = viewModel.getCurUsername()
                        viewModel.getUser(curUsername).observe(this.viewLifecycleOwner) { selectedUser ->
                            viewModel.updateUser(
                                selectedUser.id,
                                selectedUser.userName,
                                selectedUser.userPassword,
                                selectedUser.userEmail,
                                selectedBox1,
                                selectedUser.userChoice2,
                                selectedUser.joinDate,
                                selectedUser.segmentsDone
                            )
                            Log.d("CHECKER", "val = " + selectedUser.userChoice1.toString())
                        }
                        tutorialState = 5
                    }
                }
            }
            5 -> {
                selectedBox2 = 0
                tutorialState = 6
            }
            6 -> {
                if(boxId == 1){
                    tutorialState = 7
                }
                else if(boxId == 2){
                    val curUsername = viewModel.getCurUsername()
                    viewModel.getUser(curUsername).observe(this.viewLifecycleOwner) { selectedUser ->
                        viewModel.updateUser(
                            selectedUser.id,
                            selectedUser.userName,
                            selectedUser.userPassword,
                            selectedUser.userEmail,
                            selectedUser.userChoice1,
                            4,
                            selectedUser.joinDate,
                            selectedUser.segmentsDone
                        )
                        Log.d("CHECKER","val2 = " + selectedUser.userChoice1.toString() + selectedUser.userChoice2.toString())
                    }
                    tutorialState = 8
                }
            }
            7 -> {
                when(boxId) {
                    1,2,3,4-> {
                        val rectangleBox = ResourcesCompat.getDrawable(resources, R.drawable.select_box, null) //both low and high api
                        val rectangleBox2 = ResourcesCompat.getDrawable(resources, R.drawable.selected_box, null) //both low and high api
                        fun getBox(w: Int): Drawable? { return if(boxId == w) rectangleBox2 else rectangleBox }
                        binding.tutorialChooseBox1.background = getBox(1)
                        binding.tutorialChooseBox2.background = getBox(2)
                        binding.tutorialChooseBox3.background = getBox(3)
                        binding.tutorialChooseBox4.background = getBox(4)
                        binding.saveButton.background = ResourcesCompat.getDrawable(resources, R.drawable.rectangle_selected, null)
                        selectedBox2 = boxId
                        Log.d("CHECKER", "select2 = $selectedBox2")
                        doNotChange = true
                    }
                    6-> {
                        val curUsername = viewModel.getCurUsername()
                        viewModel.getUser(curUsername).observe(this.viewLifecycleOwner) { selectedUser ->
                            viewModel.updateUser(
                                selectedUser.id,
                                selectedUser.userName,
                                selectedUser.userPassword,
                                selectedUser.userEmail,
                                selectedUser.userChoice1,
                                selectedBox2,
                                selectedUser.joinDate,
                                selectedUser.segmentsDone
                            )
                            Log.d("CHECKER","val2 = " + selectedUser.userChoice1.toString() + selectedUser.userChoice2.toString())
                        }
                        tutorialState = 8
                    }
                }
            }
            8 -> {
                Toast.makeText(activity, "커리큘럼을 생성합니다!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_tutorialPage_to_mainHomePage)
            }
        }

        applyBindingChanges()

    }

}