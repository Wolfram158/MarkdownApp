package com.vk.markdown.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vk.markdown.databinding.FragmentMarkdownBinding

class MarkdownFragment : Fragment() {
    private var _binding: FragmentMarkdownBinding? = null
    private val binding: FragmentMarkdownBinding
        get() = _binding ?: throw RuntimeException("FragmentMarkdownBinding is null")

    private val viewModelFactory by lazy {
        (requireActivity().application as App).getViewModelFactory()
    }

    private val markdownFileViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MarkdownFileViewModel::class.java]
    }

    private val builder by lazy {
        (requireActivity().application as App).getBuilder().also { it.setContext(requireContext()) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkdownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contract = ActivityResultContracts.OpenDocument()
        val launcher = registerForActivityResult(contract) {
            val text = it?.let { uri ->
                context?.contentResolver?.openInputStream(uri).use { istream ->
                    istream?.bufferedReader().use { br -> br?.readText() }
                }
            }
            text?.let {
                builder.buildFromString(it, binding.scrollableLayout)
            }
        }
        launcher.launch(arrayOf("*/*"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            MarkdownFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}