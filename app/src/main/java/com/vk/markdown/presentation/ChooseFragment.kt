package com.vk.markdown.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vk.markdown.R
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

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.chooseFileButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    MarkdownFragment.newInstance(MarkdownFragment.BY_FILE_SYSTEM)
                ).addToBackStack(null).commit()
        }
        binding.chooseFileByLinkButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    MarkdownFragment.newInstance(binding.editMdUrl.text.toString())
                ).addToBackStack(null).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() =
            ChooseFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}