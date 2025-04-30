package com.assistingeye.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.assistingeye.databinding.ActivityDetectionChoiceBinding
import com.assistingeye.model.Constants.EXTRA_ALL_OBJECTS_LIST
import com.assistingeye.model.Constants.EXTRA_IMAGE_HEIGHT
import com.assistingeye.model.Constants.EXTRA_IMAGE_WIDTH
import com.assistingeye.model.Constants.EXTRA_REQUIRED_OBJECT
import com.assistingeye.model.Constants.EXTRA_REQUIRED_OBJECT_LIST
import com.assistingeye.model.DetectedObjectData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class DetectionChoiceActivity: AppCompatActivity() {
    private val adcb: ActivityDetectionChoiceBinding by lazy {
        ActivityDetectionChoiceBinding.inflate(layoutInflater)
    }

    companion object {
        const val TAG = "TFLite - ODT"
        const val MIN_CONFIDENCE_ACCEPTANCE: Float = 0.5f
    }

    private val selectImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if(uri != null){
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch(Dispatchers.IO) {
                val bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }

                runOnUiThread {
                    if (bitmap != null) {
                        runObjectDetection(bitmap)
                    } else {
                        showAttentionMessage("Falha ao carregar a imagem")
                    }
                }
            }
        }
        else{
            showAttentionMessage("Nenhuma imagem selecionada")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(adcb.root)

        adcb.selectImageBt.setOnClickListener{
            if(adcb.resultTextTV.visibility != INVISIBLE)
                adcb.resultTextTV.visibility = INVISIBLE
            selectImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        adcb.backBt.setOnClickListener{
            finish()
        }
    }

    private fun runObjectDetection(bitmap: Bitmap){
        Log.d("runObjectDetection", "Object detection started")
        val image = TensorImage.fromBitmap(bitmap)

        val objectET: EditText = adcb.objectET
        val selectImageBt = adcb.selectImageBt

        val objectName: String = objectET.text.toString()

        objectET.isEnabled = false
        selectImageBt.isEnabled = false
        showAttentionMessage("Procurando objeto na imagem...")

        lifecycleScope.launch(Dispatchers.Default) {
            val options = ObjectDetector.ObjectDetectorOptions.builder()
                .setBaseOptions(BaseOptions.builder().build())
                .setMaxResults(10)
                .setScoreThreshold(0.3f)
                .build()
            Log.d("runObjectDetection", "Options generated.")
            val detector = ObjectDetector.createFromFileAndOptions(
                this@DetectionChoiceActivity,
                "2.tflite",
                options
            )
            Log.i("runObjectDetection", "Detector created.")

            val results: List<Detection> = detector.detect(image)
            debugPrint(results)
            debugCheckPositioning(results)

            runOnUiThread {
                drawResult(objectName.lowercase(), results, bitmap.width, bitmap.height)
                objectET.isEnabled = true
                selectImageBt.isEnabled = true
            }
        }
    }

    private fun drawResult(objectName: String, results: List<Detection>, imageWidth: Int, imageHeight: Int){
        val allObjectList: ArrayList<DetectedObjectData> = arrayListOf()
        val requestedObjectList: ArrayList<DetectedObjectData> = arrayListOf()

        for((i, obj) in results.withIndex()) {
            for((j, category) in obj.categories.withIndex()){
                if(category.label == objectName && category.score >= MIN_CONFIDENCE_ACCEPTANCE) {
                    requestedObjectList.add(
                        DetectedObjectData(
                        category.score * 100,
                        obj.boundingBox,
                        category.label
                        )
                    )
                }
                allObjectList.add(
                    DetectedObjectData(
                        category.score * 100,
                        obj.boundingBox,
                        category.label
                    )
                )
            }
        }

        if (requestedObjectList.size > 1) {
            Intent(this, MultipleDetectedActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_REQUIRED_OBJECT_LIST, requestedObjectList)
                putParcelableArrayListExtra(EXTRA_ALL_OBJECTS_LIST, allObjectList)
                putExtra(EXTRA_IMAGE_WIDTH, imageWidth)
                putExtra(EXTRA_IMAGE_HEIGHT, imageHeight)
                startActivity(this)
            }

            adcb.resultTextTV.visibility = INVISIBLE
            return
        }
        if (requestedObjectList.size == 1){
            Intent(this, DetectionResultActivity::class.java).apply {
                putExtra(EXTRA_REQUIRED_OBJECT, requestedObjectList[0])
                putParcelableArrayListExtra(EXTRA_ALL_OBJECTS_LIST, allObjectList)
                putExtra(EXTRA_IMAGE_WIDTH, imageWidth)
                putExtra(EXTRA_IMAGE_HEIGHT, imageHeight)
                startActivity(this)
            }

            adcb.resultTextTV.visibility = INVISIBLE
            return
        }
        if (objectName == ""){
            Intent(this, MultipleDetectedActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_REQUIRED_OBJECT_LIST, allObjectList)
                putParcelableArrayListExtra(EXTRA_ALL_OBJECTS_LIST, allObjectList)
                putExtra(EXTRA_IMAGE_WIDTH, imageWidth)
                putExtra(EXTRA_IMAGE_HEIGHT, imageHeight)
                startActivity(this)
            }

            adcb.resultTextTV.visibility = INVISIBLE
            return
        }
        showAttentionMessage("Nenhum $objectName foi encontrado na imagem")
        return
    }

    private fun showAttentionMessage(message: String){
        Log.d("showAttentionMessage", message)
        if (adcb.resultTextTV.visibility != VISIBLE)
            adcb.resultTextTV.visibility = VISIBLE
        adcb.resultTextTV.text = message
    }

    private fun debugCheckPositioning(results: List<Detection>){
        for((i, obj) in results.withIndex()){
            if(i == results.size - 1){
                break
            }
            val box = obj.boundingBox
            val nextBox = results[i + 1].boundingBox
            val nextElement = results[i+1].categories[0]

            for((j, category) in obj.categories.withIndex()){
                if(nextBox.left > box.right){
                    if(nextBox.right < box.right){
                        Log.d(TAG, "Object $i ${nextElement.label} is to the left of ${category.label}")
                    }
                    if(nextBox.right > box.right){
                        Log.d(TAG, "Object $i ${nextElement.label} is to the right of ${category.label}")
                    }
                }
                if (nextBox.right < box.left){
                    if(nextBox.left > box.left){
                        Log.d(TAG, "Object $i ${nextElement.label} is to the right of ${category.label}")
                    }
                    if(nextBox.left < box.left){
                        Log.d(TAG, "Object $i ${nextElement.label} is to the left of ${category.label}")
                    }
                }
                if (nextBox.top > box.bottom){
                    Log.d(TAG, "Object $i ${nextElement.label} is below of ${category.label}")
                }
                if (nextBox.bottom < box.top){
                    Log.d(TAG, "Object $i ${nextElement.label} is above of ${category.label}")
                }
            }
        }
    }

    private fun debugPrint(results : List<Detection>) {
        for ((i, obj) in results.withIndex()) {
            val box = obj.boundingBox

            Log.d(TAG, "Detected object: $i ")
            Log.d(TAG, "  boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")

            for ((j, category) in obj.categories.withIndex()) {
                Log.d(TAG, "    Label $j: ${category.label}")
                val confidence: Int = category.score.times(100).toInt()
                Log.d(TAG, "    Confidence: ${confidence}%")
            }
        }
    }
}