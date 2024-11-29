package com.assistingeye.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.assistingeye.databinding.ActivityDetectionChoiceBinding

class DetectionChoiceActivity: AppCompatActivity() {
    private val adcb: ActivityDetectionChoiceBinding by lazy {
        ActivityDetectionChoiceBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(adcb.root)

        adcb.selectImageBt.setOnClickListener{
            //TODO abrir seletor de imagem
        }
    }
}