package com.assistingeye.ui

import android.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.assistingeye.databinding.ActivityOptionsBinding
import java.util.Locale

class OptionsActivity: AppCompatActivity() {
    private val aob: ActivityOptionsBinding by lazy {
        ActivityOptionsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(aob.root)

        populateSpinner()

        aob.backBt.setOnClickListener {
            finish()
        }

        aob.saveBt.setOnClickListener {
            saveConfiguration()
        }
    }
    
    private fun populateSpinner(){
        val languages = arrayOf("Português", "English")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, languages)
        val languageSp = aob.languageSp
        languageSp.adapter = adapter

        checkAppLanguage()
    }

    private fun checkAppLanguage(){
        Locale.getDefault().language.also { language ->
            Log.d("Language", language)
            when(language){
                "pt" -> aob.languageSp.setSelection(0)
                "en" -> aob.languageSp.setSelection(1)
            }
        }
    }

    private fun saveConfiguration(){
        var language = aob.languageSp.selectedItem.toString()
        when(language){
            "Português" -> language = "pt"
            "English" -> language = "en"
        }
        setAppLanguage(language)
    }

    private fun setAppLanguage(language: String){
        val sharedPreferences = getSharedPreferences("AssistingEye", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("language", language)
        editor.apply()

        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        recreate()
    }

}