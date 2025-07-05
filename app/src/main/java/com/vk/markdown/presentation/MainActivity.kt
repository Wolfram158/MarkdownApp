package com.vk.markdown.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vk.markdown.R
import com.vk.markdown.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launchChooseFragment()
    }

    private fun launchChooseFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ChooseFragment.newInstance()).commit()
    }

}