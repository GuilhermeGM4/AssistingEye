package com.assistingeye.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.assistingeye.databinding.ActivityDetectionChoiceBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
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
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val MAX_FONT_SIZE = 96F
    }

    private val selectImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if(uri != null){
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch(Dispatchers.IO) {
                val bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }

                if (bitmap != null) {
                    runOnUiThread {
                        runObjectDetection(bitmap)
                    }
                } else {
                    Toast.makeText(this@DetectionChoiceActivity, "Falha ao carregar a imagem", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(adcb.root)

        adcb.selectImageBt.setOnClickListener{
            selectImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun runObjectDetection(bitmap: Bitmap){
        Log.d("runObjectDetection", "Object detection started")
        val image = TensorImage.fromBitmap(bitmap)

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(BaseOptions.builder().build())
            .setMaxResults(10)
            .setScoreThreshold(0.3f)
            .build()
        Log.i("runObjectDetection", "Options generated.")
        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "2.tflite",
            options
        )
        Log.i("runObjectDetection", "Detector created.")

        val results: List<Detection> = detector.detect(image)
        debugPrint(results)
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