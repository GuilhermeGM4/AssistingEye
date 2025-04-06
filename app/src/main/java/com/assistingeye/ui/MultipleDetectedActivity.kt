package com.assistingeye.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.assistingeye.databinding.ActivityMultipleDetectedBinding
import com.assistingeye.model.DetectedObjectData
import com.assistingeye.ui.adapters.RequiredObjectAdapter

class MultipleDetectedActivity : AppCompatActivity() {
    private val amd: ActivityMultipleDetectedBinding by lazy {
        ActivityMultipleDetectedBinding.inflate(layoutInflater)
    }

    private val requiredObjectList: ArrayList<DetectedObjectData> = arrayListOf()
    private val detectedObjectsList: ArrayList<DetectedObjectData> = arrayListOf()
    private val requiredObjectsMutableList: MutableList<DetectedObjectData> = arrayListOf()

    private val objectAdapter: RequiredObjectAdapter by lazy {
        RequiredObjectAdapter(this, requiredObjectList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amd.root)

        receiveExtras()

        populateListView()

        amd.objectLV.adapter = objectAdapter

//        TODO Create activity to display the required object and make the following code send to it
//        amd.objectLV.setOnItemClickListener{ _, _, position, _ ->
//            Intent(this, nextActivity).apply {
//                putExtra("REQUIRED_OBJECT", requiredObjectsMutableList[position])
//                startActivity(this)
//            }
//        }

        amd.backBt.setOnClickListener {
            finish()
        }
    }

    private fun receiveExtras(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("REQUIRED_OBJECT", DetectedObjectData::class.java)?.let {
                requiredObjectList.addAll(it)
            }
            intent.getParcelableArrayListExtra("ALL_OBJECTS", DetectedObjectData::class.java)?.let {
                detectedObjectsList.addAll(it)
            }
        }
        else{
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<DetectedObjectData>("REQUIRED_OBJECT")?.let {
                requiredObjectList.addAll(it)
            }
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<DetectedObjectData>("ALL_OBJECTS")?.let {
                detectedObjectsList.addAll(it)
            }
        }
    }

    private fun populateListView(){
        runOnUiThread{
            requiredObjectsMutableList.clear()
            requiredObjectsMutableList.addAll(requiredObjectList)
            objectAdapter.notifyDataSetChanged()
        }
    }
}