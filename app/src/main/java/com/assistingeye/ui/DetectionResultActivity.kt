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
import com.assistingeye.model.Constants.EXTRA_ALL_OBJECTS_LIST
import com.assistingeye.model.Constants.EXTRA_IMAGE_HEIGHT
import com.assistingeye.model.Constants.EXTRA_IMAGE_WIDTH
import com.assistingeye.model.Constants.EXTRA_REQUIRED_OBJECT
import com.assistingeye.model.DetectedObjectData

class DetectionResultActivity : AppCompatActivity() {
    private val adrb: ActivityDetectionResultBinding by lazy {
        ActivityDetectionResultBinding.inflate(layoutInflater)
    }

    private lateinit var requiredObject: DetectedObjectData
    private val detectedObjectsList: ArrayList<DetectedObjectData> = arrayListOf()
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0

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
            intent.getParcelableExtra(EXTRA_REQUIRED_OBJECT, DetectedObjectData::class.java)?.let {
                requiredObject = it
            }
            intent.getParcelableArrayListExtra(EXTRA_ALL_OBJECTS_LIST, DetectedObjectData::class.java)?.let {
                detectedObjectsList.addAll(it)
            }
        }
        else{
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<DetectedObjectData>(EXTRA_REQUIRED_OBJECT)?.let {
                requiredObject = it
            }
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<DetectedObjectData>(EXTRA_ALL_OBJECTS_LIST)?.let {
                detectedObjectsList.addAll(it)
            }
        }
        imageWidth = intent.getIntExtra(EXTRA_IMAGE_WIDTH, 0)
        imageHeight = intent.getIntExtra(EXTRA_IMAGE_HEIGHT, 0)
    }

    private fun makePositioningMessage(objectList: ArrayList<DetectedObjectData>, requestedObject: DetectedObjectData): String{
        var message = ""

        message += "Tenho ${requestedObject.confidence} de certeza que este item é um ${requestedObject.name}. "

        message += positionOnImage(requestedObject)

        for(obj in objectList){
            message += "\nO objeto ${obj.name} está"
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

    private fun positionOnImage(obj: DetectedObjectData): String{
        val imageCenterX: Float = imageWidth / 2f
        val imageCenterY: Float = imageHeight / 2f
        debugCoordinates(imageCenterX, imageCenterY, obj)

        var message: String = "O objeto está"

        if(obj.boundingBox.left < imageCenterX && obj.boundingBox.right > imageCenterX &&
            obj.boundingBox.top > imageCenterY && obj.boundingBox.bottom < imageCenterY)
            return " no centro da imagem"
        if(obj.boundingBox.right > imageCenterX)
            message += " a direita"
        if(obj.boundingBox.left < imageCenterX)
            message += " a esquerda"
        if(obj.boundingBox.top > imageCenterY)
            message += " abaixo"
        if(obj.boundingBox.bottom < imageCenterY)
            message += " acima"
        message += " da imagem."
        return message
    }

    private fun debugCoordinates(imageCenterX: Float, imageCenterY: Float, obj: DetectedObjectData){
        Log.d("debugCoordinates", "Image Width $imageWidth")
        Log.d("debugCoordinates", "Image Height $imageHeight")
        Log.d("debugCoordinates", "Image center: ($imageCenterX, $imageCenterY)")
        Log.d("debugCoordinates", "Object center: (${obj.boundingBox.centerX()}, ${obj.boundingBox.centerY()})")
        Log.d("debugCoordinates", "Object left: ${obj.boundingBox.left}")
        Log.d("debugCoordinates", "Object right: ${obj.boundingBox.right}")
        Log.d("debugCoordinates", "Object top: ${obj.boundingBox.top}")
        Log.d("debugCoordinates", "Object bottom: ${obj.boundingBox.bottom}")
        Log.d("debugCoordinates", "Object width: ${obj.boundingBox.width()}")
        Log.d("debugCoordinates", "Object height: ${obj.boundingBox.height()}")
    }
}