package com.vk.markdown.presentation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vk.markdown.databinding.FragmentMarkdownBinding
import com.vk.markdown.net.NetDownloader
import kotlin.concurrent.thread

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkdownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        markdownFileViewModel.downloadImage(
            "https://raw.githubusercontent.com/arkivanov/" +
                    "MVIKotlin/master/docs/media/logo/landscape/png/mvikotlin_coloured.png"
        ) { bytes ->
            bytes?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                handler.post {
                    binding.imageTest.setImageBitmap(bitmap)
                }
            }
        }
//        val contract = ActivityResultContracts.OpenDocument()
//        val launcher = registerForActivityResult(contract) {
//            val text = it?.let { uri ->
//                context?.contentResolver?.openInputStream(uri).use { istream ->
//                    istream?.bufferedReader().use { br -> br?.readText() }
//                }
//            }
//            binding.test.text = text?.let {
//                buildFromString(it)
//            }
//        }
//        launcher.launch(arrayOf("*/*"))
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