package com.vk.markdown

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vk.markdown.databinding.FragmentChooseBinding

class ChooseFragment : Fragment() {
    private var _binding: FragmentChooseBinding? = null
    private val binding: FragmentChooseBinding
        get() = _binding ?: throw RuntimeException("FragmentChooseBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseFileButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    MarkdownFragment()
                ).addToBackStack(null).commit()
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            ChooseFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}