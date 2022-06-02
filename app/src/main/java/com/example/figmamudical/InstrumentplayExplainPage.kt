package com.example.figmamudical

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.figmamudical.databinding.FragmentInstrumentplayExplainPageBinding


class InstrumentplayExplainPage : Fragment() {

    private val viewModel: UserdataViewModel by activityViewModels {
        UserdataViewModelFactory(
            (activity?.application as UserdataApplication).database.userdataDao()
        )
    }

    private var _binding: FragmentInstrumentplayExplainPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInstrumentplayExplainPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        putUserNameText()
        binding.apply {
            // Assign the fragment
            instrumentplayexplainpageFragment = this@InstrumentplayExplainPage
        }
    }

    private fun putUserNameText() {
        binding.explainText.text =
        (
            "악기 사진을 선택하면, 악기의 소리가 들릴 거예요!\n" +
            "악기마다 다 특이하고 신기한 소리가 나니까, 잘 들어보고 즉흥악기연주 활동에서 함께할 악기들을 골라주세요!\n" +
            "앞에서 골랐던 노래와 어울릴 것 같은 소리를 찾아도 좋고, (" + viewModel.getCurUsername() + ")님이 마음이 편해지는 소리를 고르셔도 좋아요\n" +
            "그럼 악기 소리를 들으러 가 볼까요?"
        )
    }

    fun understand() {
        findNavController().navigate(R.id.action_instrumentplayExplainPage_to_instrumentplayChoosePage)
    }

}