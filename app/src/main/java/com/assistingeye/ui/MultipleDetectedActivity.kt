package com.assistingeye.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.assistingeye.databinding.ActivityMultipleDetectedBinding
import com.assistingeye.model.Constants.EXTRA_ALL_OBJECTS_LIST
import com.assistingeye.model.Constants.EXTRA_IMAGE_HEIGHT
import com.assistingeye.model.Constants.EXTRA_IMAGE_WIDTH
import com.assistingeye.model.Constants.EXTRA_REQUIRED_OBJECT
import com.assistingeye.model.Constants.EXTRA_REQUIRED_OBJECT_LIST
import com.assistingeye.model.DetectedObjectData
import com.assistingeye.ui.adapters.RequiredObjectAdapter

class MultipleDetectedActivity : AppCompatActivity() {
    private val amd: ActivityMultipleDetectedBinding by lazy {
        ActivityMultipleDetectedBinding.inflate(layoutInflater)
    }

    private val requiredObjectList: ArrayList<DetectedObjectData> = arrayListOf()
    private val detectedObjectsList: ArrayList<DetectedObjectData> = arrayListOf()
    private val requiredObjectsMutableList: MutableList<DetectedObjectData> = arrayListOf()

    private var imageWidth: Int = 0
    private var imageHeight: Int = 0

    private val objectAdapter: RequiredObjectAdapter by lazy {
        RequiredObjectAdapter(this, requiredObjectList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amd.root)

        receiveExtras()

        populateListView()

        amd.objectLV.adapter = objectAdapter

        amd.objectLV.setOnItemClickListener{ _, _, position, _ ->
            Intent(this, DetectionResultActivity::class.java).apply {
                putExtra(EXTRA_REQUIRED_OBJECT, requiredObjectList[position])
                putParcelableArrayListExtra(EXTRA_ALL_OBJECTS_LIST, detectedObjectsList)
                putExtra(EXTRA_IMAGE_WIDTH, imageWidth)
                putExtra(EXTRA_IMAGE_HEIGHT, imageHeight)
                startActivity(this)
            }
        }

        amd.backBt.setOnClickListener {
            finish()
        }
    }

    private fun receiveExtras(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(EXTRA_REQUIRED_OBJECT_LIST, DetectedObjectData::class.java)?.let {
                requiredObjectList.addAll(it)
            }
            intent.getParcelableArrayListExtra(EXTRA_ALL_OBJECTS_LIST, DetectedObjectData::class.java)?.let {
                detectedObjectsList.addAll(it)
            }
        }
        else{
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<DetectedObjectData>(EXTRA_REQUIRED_OBJECT_LIST)?.let {
                requiredObjectList.addAll(it)
            }
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<DetectedObjectData>(EXTRA_ALL_OBJECTS_LIST)?.let {
                detectedObjectsList.addAll(it)
            }
        }
        imageWidth = intent.getIntExtra(EXTRA_IMAGE_WIDTH, 0)
        imageHeight = intent.getIntExtra(EXTRA_IMAGE_HEIGHT, 0)
    }

    private fun populateListView(){
        runOnUiThread{
            requiredObjectsMutableList.clear()
            requiredObjectsMutableList.addAll(requiredObjectList)
            objectAdapter.notifyDataSetChanged()
        }
    }
}