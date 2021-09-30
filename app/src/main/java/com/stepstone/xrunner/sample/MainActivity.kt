package com.stepstone.xrunner.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.stepstone.xrunner.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener {
            binding.content.dummyTextView.visibility = View.VISIBLE
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
