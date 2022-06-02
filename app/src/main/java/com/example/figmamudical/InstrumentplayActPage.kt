package com.example.figmamudical

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
import com.example.figmamudical.databinding.FragmentInstrumentplayActPageBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Toast


class InstrumentplayActPage : Fragment() {

    private var songFinished = false
    private val isActive = mutableListOf(false,false,false,false,false,false)
    private val selectedInstrumentList = mutableListOf<Int>()
    private var instrument1MediaPlayer: MediaPlayer? = null
    private var instrument2MediaPlayer: MediaPlayer? = null
    private var instrument3MediaPlayer: MediaPlayer? = null
    private var instrument4MediaPlayer: MediaPlayer? = null
    private var instrument5MediaPlayer: MediaPlayer? = null
    private var songMediaPlayer: MediaPlayer? = null

    private var executorService: ExecutorService? = null

    private var seekPercentageLast = 0
    private var seekPercentageStep = 15

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentInstrumentplayActPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInstrumentplayActPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInstrumentList()
        putSelectedInstruments()
        binding.apply {
            // Assign the fragment
            instrumentplayactpageFragment = this@InstrumentplayActPage
        }

        binding.questionButton.setOnClickListener {
            val fbDialogue = Dialog(this.context!!)
            fbDialogue.window?.setBackgroundDrawable(ColorDrawable(Color.argb(100, 0, 0, 0)))
            val mudiQuestionLayout = MainActivity.getDialogContext(2)
            fbDialogue.setContentView(mudiQuestionLayout)
            fbDialogue.setCancelable(true)
            fbDialogue.show()
        }

        //Toast.makeText(this.context, "Initializing", Toast.LENGTH_SHORT).show()
        MainActivity.initEmotion()
        //Toast.makeText(this.context, "${MainActivity.getEmotion()}", Toast.LENGTH_SHORT).show()

        MainActivity.startCameraOnFragment(this.context, this.viewLifecycleOwner)
        MainActivity.cameraExecutor = Executors.newSingleThreadExecutor()
        MainActivity.toggleAudioThreading(this.context)
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

        val songImage = when(viewModel.getSong()){
            1->R.drawable.song_cover1
            2->R.drawable.song_cover2
            3->R.drawable.song_cover3
            4->R.drawable.song_cover4
            else->R.drawable.song_cover5
        }
        val songName = when(viewModel.getSong()){
            1->R.string.song1_title
            2->R.string.song2_title
            3->R.string.song3_title
            4->R.string.song4_title
            else->R.string.song5_title
        }
        val songTag = when(viewModel.getSong()){
            1->R.string.song1_tag
            2->R.string.song2_tag
            3->R.string.song3_tag
            4->R.string.song4_tag
            else->R.string.song5_tag
        }
        binding.instrumentplaySongCover.background = ResourcesCompat.getDrawable(resources, songImage, null)
        binding.sayText.text = resources.getString(R.string.please_start_song)
        binding.songName.text = resources.getString(songName)
        binding.tagText.text = resources.getString(songTag)
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

    fun playSong() {

        /* DEBUG
        if(MainActivity.tries > 0) {
            Toast.makeText(this.context, "Camera/MIC Running ${MainActivity.tries}", Toast.LENGTH_SHORT).show()
        }*/

        if(songMediaPlayer == null) {
            //val songFile = R.raw.sound_file_example
            val songFile = when(viewModel.getSong()) {
                1->R.raw.song1_inst
                2->R.raw.song2_inst
                3->R.raw.song3_inst
                4->R.raw.song4_inst
                else->R.raw.song5_inst
            }
            songMediaPlayer = MediaPlayer.create(context, songFile)
            songMediaPlayer?.start()
            executorService = Executors.newFixedThreadPool(1)
            executorService!!.execute(seekSong)
            songMediaPlayer?.setOnCompletionListener {
                songFinished = true
                binding.instrumentplayFinishButton.background = ResourcesCompat.getDrawable(resources, R.drawable.rectangle_small_selected, null)
                binding.sayText.text = resources.getString(R.string.please_start_song)
                executorService!!.shutdownNow()
                executorService = null
            }

        }
        else {
            songMediaPlayer?.start()
        }
    }
    fun stopSong() {
        songMediaPlayer?.reset()
        songMediaPlayer?.release()
        songMediaPlayer = null
        executorService!!.shutdownNow()
        executorService = null
    }
    fun pauseSong() {
        songMediaPlayer?.pause()
    }

    fun playInstrument(c: Int) {
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
            mediaPlayer?.start() // no need to call prepare(); create() does that for you
            //mediaPlayer?.pause()
            //mediaPlayer?.stop()
            //mediaPlayer?.release()
            //mediaPlayer = null
        }
    }

    private fun newRecommendedInstrument(rnd: Int) {

        MainActivity.getEmotion()

        val instrumentNames = mutableListOf(null, R.string.instrument1_name, R.string.instrument2_name, R.string.instrument3_name, R.string.instrument4_name, R.string.instrument5_name, R.string.instrument6_name, R.string.instrument7_name, R.string.instrument8_name)
        val chosenNames = mutableListOf<Int?>(null)
        var idx = 1
        repeat(8) {
            if(viewModel.getChosenInstrument(idx)) {
                chosenNames.add(instrumentNames[idx])
            }
            idx ++
        }
        activity?.runOnUiThread {
            binding.sayText.text = ("이번에는 " + resources.getString(chosenNames[rnd]!!) + " 연주~~")
        }
    }

    private val seekSong: Runnable = Runnable {
        while(true) {
            if(songMediaPlayer == null) break

            val totalDuration: Int = songMediaPlayer?.duration ?: 100
            val currentDuration: Int = songMediaPlayer?.currentPosition ?: 0
            val progressPercent: Int = (currentDuration.toDouble() / totalDuration.toDouble() * 1e2).toInt()

            Log.d("CHECKER", "now progression = $currentDuration / $totalDuration")
            Log.d("CHECKER", "last percent = $seekPercentageLast")
            Log.d("CHECKER", "percent = $progressPercent")

            if(currentDuration + seekPercentageStep >= totalDuration) break

            val rnd = (1..5).random()

            Log.d("CHECKER", "found random value = $rnd")

            if (progressPercent >= seekPercentageLast) {
                newRecommendedInstrument(rnd)
                seekPercentageLast += seekPercentageStep
            }
        }
    }

    fun finishSegment() {

        songFinished = true

        if(songFinished) {
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
            songMediaPlayer?.reset()
            songMediaPlayer?.release()
            songMediaPlayer = null
            val username = viewModel.getCurUsername()
            viewModel.getUser(username).observe(this.viewLifecycleOwner) { selectedUser ->
                viewModel.done(
                    selectedUser.id,
                    selectedUser.userName,
                    selectedUser.userPassword,
                    selectedUser.userEmail,
                    selectedUser.userChoice1,
                    selectedUser.userChoice2,
                    selectedUser.joinDate,
                    selectedUser.segmentsDone
                )
                MainActivity.toggleAudioThreading(this.context)
                findNavController().navigate(R.id.action_instrumentplayActPage_to_emotionAnalysisPage)
            }
        }
    }


}

