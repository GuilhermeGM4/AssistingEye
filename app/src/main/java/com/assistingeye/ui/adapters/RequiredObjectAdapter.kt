package com.assistingeye.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.assistingeye.R
import com.assistingeye.databinding.TileDetectedObjectBinding
import com.assistingeye.model.DetectedObjectData

class RequiredObjectAdapter(
    context: Context,
    private val requiredObjectList: ArrayList<DetectedObjectData>
): ArrayAdapter<DetectedObjectData>(context, R.layout.tile_detected_object, requiredObjectList) {
    private data class ObjectTileHolder(val nameTV: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var tdo: TileDetectedObjectBinding

        val detectedObject = requiredObjectList[position]

        var objectTile = convertView
        if(objectTile == null){
            tdo = TileDetectedObjectBinding.inflate(
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                parent,
                false
            )

            objectTile = tdo.root

            val newObjectTileHolder = ObjectTileHolder(
                tdo.nameTV
            )

            objectTile.tag = newObjectTileHolder
        }

        val holder = objectTile.tag as ObjectTileHolder
        holder.let {
            with(detectedObject){
                it.nameTV.text = name
            }
        }

        return objectTile
    }
}