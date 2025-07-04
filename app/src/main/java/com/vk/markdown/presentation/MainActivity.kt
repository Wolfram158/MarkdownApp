package com.vk.markdown.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vk.markdown.R
import com.vk.markdown.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

//    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        binding.root.setOnApplyWindowInsetsListener { v, insets ->
//            val imeHeight = insets.getInsets(WindowInsets.Type.ime()).bottom;
//            binding.root.setPadding(0, 0, 0, imeHeight)
//            insets
//        }
        launchChooseFragment()
    }

    private fun launchChooseFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ChooseFragment.newInstance()).commit()
    }

}