package com.assistingeye.ui

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.assistingeye.databinding.ActivityOptionsBinding
import com.assistingeye.model.Constants.EXTRA_LANGUAGE_CHANGED
import java.util.Locale

class OptionsActivity: AppCompatActivity() {
    private val aob: ActivityOptionsBinding by lazy {
        ActivityOptionsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(aob.root)

        populateSpinner()

        setLanguage()

        aob.backBt.setOnClickListener {
            Intent().apply {
                putExtra(EXTRA_LANGUAGE_CHANGED, true)
                setResult(Activity.RESULT_OK, this)
            }
            finish()
        }

        aob.saveBt.setOnClickListener {
            saveConfiguration()
        }
    }
    
    private fun populateSpinner(){
        val languages = arrayOf("Português", "English")
        val adapter = ArrayAdapter(this, com.assistingeye.R.layout.spinner_highcontrast_selected_item, languages)
        adapter.setDropDownViewResource(com.assistingeye.R.layout.spinner_highcontrast_dropdown_item)

        val languageSp = aob.languageSp
        languageSp.adapter = adapter

        checkAppLanguage()
    }

    private fun checkAppLanguage(){
        when(resources.configuration.locales.get(0).language){
            "pt" -> aob.languageSp.setSelection(0)
            "en" -> aob.languageSp.setSelection(1)
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

    private fun setLanguage(){
        val locale = getSharedPreferences("AssistingEye", MODE_PRIVATE).let { sharedPreferences ->
            val language = sharedPreferences.getString("language", "pt")
            Locale(language)
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }
}