package com.assistingeye.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.assistingeye.R
import com.assistingeye.databinding.ActivityDetectionResultBinding
import com.assistingeye.model.DetectedObjectData

class DetectionResultActivity : AppCompatActivity() {
    private val adrb: ActivityDetectionResultBinding by lazy {
        ActivityDetectionResultBinding.inflate(layoutInflater)
    }

    private lateinit var requiredObject: DetectedObjectData
    private val detectedObjectsList: ArrayList<DetectedObjectData> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(adrb.root)

        receiveExtras()

        adrb.resultTextTV.text = makePositioningMessage(detectedObjectsList, requiredObject)

        adrb.backBt.setOnClickListener {
            finish()
        }
    }

    private fun receiveExtras(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("REQUIRED_OBJECT", DetectedObjectData::class.java)?.let {
                requiredObject = it
            }
            intent.getParcelableArrayListExtra("ALL_OBJECTS", DetectedObjectData::class.java)?.let {
                detectedObjectsList.addAll(it)
            }
        }
        else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<DetectedObjectData>("REQUIRED_OBJECT")?.let {
                requiredObject = it
            }
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<DetectedObjectData>("ALL_OBJECTS")?.let {
                detectedObjectsList.addAll(it)
            }
        }
    }

    private fun makePositioningMessage(objectList: ArrayList<DetectedObjectData>, requestedObject: DetectedObjectData): String{
        var message = ""
        for(obj in objectList){
            message += "O objeto ${obj.name} está"
            Log.d("makePositioningMessage", "Object list: $objectList")
            if(obj.name != requestedObject.name){
                if(obj.boundingBox.left > requestedObject.boundingBox.right || (
                            obj.boundingBox.left < requestedObject.boundingBox.right &&
                                    obj.boundingBox.right > requestedObject.boundingBox.right
                            ))
                    message += " a direita"
                else if (obj.boundingBox.right < requestedObject.boundingBox.left || (
                            obj.boundingBox.right > requestedObject.boundingBox.left &&
                                    obj.boundingBox.left < requestedObject.boundingBox.left
                            ))
                    message += " a esquerda"

                if (obj.boundingBox.top > requestedObject.boundingBox.bottom || (
                            obj.boundingBox.top < requestedObject.boundingBox.bottom &&
                                    obj.boundingBox.bottom > requestedObject.boundingBox.bottom
                            ))
                    message += " abaixo"
                if (obj.boundingBox.bottom < requestedObject.boundingBox.top || (
                            obj.boundingBox.bottom > requestedObject.boundingBox.top &&
                                    obj.boundingBox.top < requestedObject.boundingBox.top
                            ))
                    message += " acima"
            }
            message += " do objeto ${requestedObject.name} \n"
        }
        return message
    }
}