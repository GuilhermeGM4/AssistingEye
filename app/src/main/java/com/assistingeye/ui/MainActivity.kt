package com.assistingeye.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.assistingeye.R
import com.assistingeye.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var oarl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        oarl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                recreate()
                Log.d("MainActivity", "Language changed")
            }
        }

        amb.startDetectionBt.setOnClickListener {
            val intent = Intent(this, DetectionChoiceActivity::class.java)
            startActivity(intent)
        }
        amb.optionsBt.setOnClickListener {
            val intent = Intent(this, OptionsActivity::class.java)
            oarl.launch(intent)
        }
    }
}