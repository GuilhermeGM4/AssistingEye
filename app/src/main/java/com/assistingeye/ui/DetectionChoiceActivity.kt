package com.assistingeye.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.assistingeye.databinding.ActivityDetectionChoiceBinding

class DetectionChoiceActivity: AppCompatActivity() {
    private val adcb: ActivityDetectionChoiceBinding by lazy {
        ActivityDetectionChoiceBinding.inflate(layoutInflater)
    }

    private val selectImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if(uri != null){
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(adcb.root)

        adcb.selectImageBt.setOnClickListener{
            //TODO abrir seletor de imagem
            selectImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}