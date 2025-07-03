package com.vk.markdown.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vk.markdown.R
import com.vk.markdown.databinding.FragmentMarkdownBinding

class MarkdownFragment : Fragment() {
    private var _binding: FragmentMarkdownBinding? = null
    private val binding: FragmentMarkdownBinding
        get() = _binding ?: throw RuntimeException("FragmentMarkdownBinding is null")

    private val handler = Handler(getMainLooper())

    private val viewModelFactory by lazy {
        (requireActivity().application as App).getViewModelFactory()
    }

    private val markdownFileViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MarkdownFileViewModel::class.java]
    }

    private val builder by lazy {
        (requireActivity().application as App).getBuilder().also { it.setContext(requireContext()) }
    }

    private val wayOrLink by lazy {
        arguments?.run {
            getString(WAY)
        }
    }

    private var runnable: Runnable? = null
    private var text: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkdownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            observeViewModel()
            setOnChangeModeListener()
            loadFile()
        }
    }

    private fun setOnChangeModeListener() {
        binding.editButton.setOnClickListener {
            it?.visibility = View.GONE
            binding.saveButton.visibility = View.VISIBLE
            binding.scrollableLayout.removeAllViews()
            binding.editMd.visibility = View.VISIBLE
            text?.let {
                binding.editMd.setText(it, TextView.BufferType.EDITABLE)
            }
        }
        binding.saveButton.setOnClickListener {
            it?.visibility = View.GONE
            binding.editButton.visibility = View.VISIBLE
            binding.editMd.visibility = View.GONE
            text = binding.editMd.text.toString()
            text?.let { text ->
                builder.buildFromString(text, binding.scrollableLayout)
            }
        }
    }

    private fun observeViewModel() {
        markdownFileViewModel.signal.observe(viewLifecycleOwner) {
            when (it) {
                is MarkdownFileViewModel.Result.Error -> {
                    Toast.makeText(
                        context,
                        getString(R.string.error_occurred_when_load),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is MarkdownFileViewModel.Result.Initial -> {}
                is MarkdownFileViewModel.Result.Success -> {}
            }
        }
    }

    private fun loadFile() {
        if (wayOrLink == BY_FILE_SYSTEM) {
            val contract = ActivityResultContracts.OpenDocument()
            val launcher = registerForActivityResult(contract) {
                text = it?.let { uri ->
                    context?.contentResolver?.openInputStream(uri).use { istream ->
                        istream?.bufferedReader().use { br -> br?.readText() }
                    }
                }
                text?.let {
                    builder.buildFromString(it, binding.scrollableLayout)
                }
            }
            launcher.launch(arrayOf("*/*"))
        } else {
            wayOrLink?.let { link ->
                markdownFileViewModel.loadFile(link) { text ->
                    this.text = text
                    runnable = object : Runnable {
                        override fun run() {
                            builder.buildFromString(text, binding.scrollableLayout)
                        }
                    }
                    runnable?.let {
                        handler.post(it)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        builder.setContext(null)
        runnable?.let {
            handler.removeCallbacks(it)
        }
    }

    companion object {
        const val WAY = "way_of_getting_file"
        const val BY_FILE_SYSTEM = "by_file_system"

        fun newInstance(param: String) =
            MarkdownFragment().apply {
                arguments = Bundle().apply {
                    putString(WAY, param)
                }
            }
    }
}