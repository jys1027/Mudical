package com.example.figmamudical

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentEmotionAnalysisPageBinding
import kotlinx.coroutines.launch


class EmotionAnalysisPage : Fragment() {

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentEmotionAnalysisPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmotionAnalysisPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            // Assign the fragment
            emotionanalysispageFragment = this@EmotionAnalysisPage
        }

        val emotionList = MainActivity.getEmotion()

        Toast.makeText(this.context, "emotion result = $emotionList", Toast.LENGTH_LONG).show()

        if(emotionList[0] >= 0.0) {
            Log.d("emotion", "emotion result = $emotionList")
            binding.emotionBar1.layoutParams.width = (emotionList[0] * 500 + 1).toInt()
            binding.emotionBar2.layoutParams.width = (emotionList[1] * 500 + 1).toInt()
            binding.emotionBar3.layoutParams.width = (emotionList[2] * 500 + 1).toInt()
            binding.emotionBar4.layoutParams.width = (emotionList[3] * 500 + 1).toInt()
            binding.emotionBar5.layoutParams.width = (emotionList[4] * 500 + 1).toInt()
        }
        else{
            Log.d("emotion", "emotion not analyzed")
            binding.emotionBar1.layoutParams.width = (0.2 * 500 + 1).toInt()
            binding.emotionBar2.layoutParams.width = (0.2 * 500 + 1).toInt()
            binding.emotionBar3.layoutParams.width = (0.2 * 500 + 1).toInt()
            binding.emotionBar4.layoutParams.width = (0.2 * 500 + 1).toInt()
            binding.emotionBar5.layoutParams.width = (0.2 * 500 + 1).toInt()
        }

        //Toast.makeText(this.context, "analyzing", Toast.LENGTH_LONG).show()

        binding.commentText.text = resources.getString(
            when {
                emotionList[0] > 0.5 -> R.string.example_comment_1
                emotionList[1] > 0.5 -> R.string.example_comment_2
                emotionList[2] > 0.5 -> R.string.example_comment_3
                emotionList[3] > 0.5 -> R.string.example_comment_4
                else -> R.string.example_comment_5
            }
        )

        lifecycleScope.launch {
            binding.commentText.text = resources.getString(
                when {
                    emotionList[0] > 0.5 -> R.string.example_comment_1
                    emotionList[1] > 0.5 -> R.string.example_comment_2
                    emotionList[2] > 0.5 -> R.string.example_comment_3
                    emotionList[3] > 0.5 -> R.string.example_comment_4
                    else -> R.string.example_comment_5
                }
            )
        }

        val mudiEmotion = when {
            emotionList[0] > 0.5 -> R.drawable.mudi
            emotionList[1] > 0.5 -> R.drawable.mudi_sad
            emotionList[2] > 0.5 -> R.drawable.mudi_angry
            emotionList[3] > 0.5 -> R.drawable.mudi_fear
            else -> R.drawable.mudi_calm
        }
        binding.mudiEmotionAnalysis.background = ResourcesCompat.getDrawable(resources, mudiEmotion, null)
    }

    fun finishSegment() {
        findNavController().navigate(R.id.action_emotionAnalysisPage_to_mainHomePage)
    }
}