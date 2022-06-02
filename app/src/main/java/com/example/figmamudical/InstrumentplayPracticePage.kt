package com.example.figmamudical

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentInstrumentplayPracticePageBinding


class InstrumentplayPracticePage : Fragment() {

    private val isActive = mutableListOf(false,false,false,false,false,false)
    private val selectedInstrumentList = mutableListOf<Int>()
    private var instrument1MediaPlayer: MediaPlayer? = null
    private var instrument2MediaPlayer: MediaPlayer? = null
    private var instrument3MediaPlayer: MediaPlayer? = null
    private var instrument4MediaPlayer: MediaPlayer? = null
    private var instrument5MediaPlayer: MediaPlayer? = null

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentInstrumentplayPracticePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInstrumentplayPracticePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInstrumentList()
        putSelectedInstruments()
        binding.apply {
            // Assign the fragment
            instrumentplaypracticepageFragment = this@InstrumentplayPracticePage
        }

        binding.questionButton.setOnClickListener {
            val fbDialogue = Dialog(this.context!!)
            fbDialogue.window?.setBackgroundDrawable(ColorDrawable(Color.argb(100, 0, 0, 0)))
            fbDialogue.setContentView(R.layout.segment_question_box)
            fbDialogue.setCancelable(true)
            fbDialogue.show()
        }
    }

    private fun putSelectedInstruments() {
        val instrumentImages = mutableListOf(null, R.drawable.instrument1, R.drawable.instrument2, R.drawable.instrument3, R.drawable.instrument4, R.drawable.instrument5, R.drawable.instrument6, R.drawable.instrument7, R.drawable.instrument8)
        val instrumentNames = mutableListOf(null, R.string.instrument1_name, R.string.instrument2_name, R.string.instrument3_name, R.string.instrument4_name, R.string.instrument5_name, R.string.instrument6_name, R.string.instrument7_name, R.string.instrument8_name)

        val chosenImages = mutableListOf<Int?>(null)
        val chosenNames = mutableListOf<Int?>(null)
        var idx = 1
        repeat(8) {
            Log.d("CHECKER", "using $idx-th instrument = ${viewModel.getChosenInstrument(idx)}")
            if(viewModel.getChosenInstrument(idx)) {
                chosenImages.add(instrumentImages[idx])
                chosenNames.add(instrumentNames[idx])
            }
            idx ++
        }
        idx = 1
        repeat(5) {
            when(idx) {
                1->binding.instrument1Image
                2->binding.instrument2Image
                3->binding.instrument3Image
                4->binding.instrument4Image
                else->binding.instrument5Image
            }.background = ResourcesCompat.getDrawable(resources, chosenImages[idx]!!, null)
            when(idx) {
                1->binding.instrument1Text
                2->binding.instrument2Text
                3->binding.instrument3Text
                4->binding.instrument4Text
                else->binding.instrument5Text
            }.text = resources.getString(chosenNames[idx]!!)
            idx ++
        }
    }

    private fun getInstrumentList() {
        selectedInstrumentList.add(0)
        var idx = 1
        repeat(8) {
            if(viewModel.getChosenInstrument(idx))
                selectedInstrumentList.add(idx)
            idx ++
        }
    }

    fun playInstrument(c: Int) {
        //글자도 toggle, 악기소리도 toggle (여러 악기 동시에 소리날 수 있음)
        if(isActive[c]){
            when(c) {
                1->binding.instrument1Text
                2->binding.instrument2Text
                3->binding.instrument3Text
                4->binding.instrument4Text
                else->binding.instrument5Text
            }.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            isActive[c] = false
            when(c) {
                1 -> {
                    instrument1MediaPlayer?.reset()
                    instrument1MediaPlayer?.release()
                    instrument1MediaPlayer = null
                }
                2 -> {
                    instrument2MediaPlayer?.reset()
                    instrument2MediaPlayer?.release()
                    instrument2MediaPlayer = null
                }
                3 -> {
                    instrument3MediaPlayer?.reset()
                    instrument3MediaPlayer?.release()
                    instrument3MediaPlayer = null
                }
                4 -> {
                    instrument4MediaPlayer?.reset()
                    instrument4MediaPlayer?.release()
                    instrument4MediaPlayer = null
                }
                else -> {
                    instrument5MediaPlayer?.reset()
                    instrument5MediaPlayer?.release()
                    instrument5MediaPlayer = null
                }
            }
        }
        else {
            when(c) {
                1->binding.instrument1Text
                2->binding.instrument2Text
                3->binding.instrument3Text
                4->binding.instrument4Text
                else->binding.instrument5Text
            }.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            isActive[c] = true
            val soundFile = when (selectedInstrumentList[c]) {
                1 -> R.raw.instrument1_sound
                2 -> R.raw.instrument2_sound
                3 -> R.raw.instrument3_sound
                4 -> R.raw.instrument4_sound
                5 -> R.raw.instrument5_sound
                6 -> R.raw.instrument6_sound
                7 -> R.raw.instrument7_sound
                else -> R.raw.instrument8_sound
            }
            when (c) {
                1 -> instrument1MediaPlayer = MediaPlayer.create(context, soundFile)
                2 -> instrument2MediaPlayer = MediaPlayer.create(context, soundFile)
                3 -> instrument3MediaPlayer = MediaPlayer.create(context, soundFile)
                4 -> instrument4MediaPlayer = MediaPlayer.create(context, soundFile)
                else -> instrument5MediaPlayer = MediaPlayer.create(context, soundFile)
            }
            val mediaPlayer = when (c) {
                1 -> instrument1MediaPlayer
                2 -> instrument2MediaPlayer
                3 -> instrument3MediaPlayer
                4 -> instrument4MediaPlayer
                else -> instrument5MediaPlayer
            }
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }
    }

    fun goToActPage() {
        instrument1MediaPlayer?.reset()
        instrument1MediaPlayer?.release()
        instrument1MediaPlayer = null
        instrument2MediaPlayer?.reset()
        instrument2MediaPlayer?.release()
        instrument2MediaPlayer = null
        instrument3MediaPlayer?.reset()
        instrument3MediaPlayer?.release()
        instrument3MediaPlayer = null
        instrument4MediaPlayer?.reset()
        instrument4MediaPlayer?.release()
        instrument4MediaPlayer = null
        instrument5MediaPlayer?.reset()
        instrument5MediaPlayer?.release()
        instrument5MediaPlayer = null
        findNavController().navigate(R.id.action_instrumentplayPracticePage_to_instrumentplayActPage)
    }

}