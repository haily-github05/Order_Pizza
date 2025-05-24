package com.example.app.view.activity

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.app.view.fragment.Login1Fragment
import com.example.app.view.fragment.Login2Fragment
import java.util.Locale

class HelloApp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocale()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello_app)

        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val languageButton = findViewById<Button>(R.id.languageButton)

        tab1.text = getString(R.string.tab_guest)
        tab2.text = getString(R.string.tab_member)
        languageButton.text = getString(R.string.btn_language)

        highlightTab(tab1)

        tab1.setOnClickListener {
            highlightTab(tab1)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Login1Fragment())
                .commit()
        }

        tab2.setOnClickListener {
            highlightTab(tab2)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Login2Fragment())
                .commit()
        }

        languageButton.setOnClickListener {
            switchLanguage()
        }
    }

    private fun switchLanguage() {
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val currentLang = prefs.getString("language", "vi") ?: "vi"
        val newLang = if (currentLang == "vi") "en" else "vi"

        prefs.edit().putString("language", newLang).apply()
        setLocale(newLang)
        recreate()
    }

    private fun loadLocale() {
        val lang = getSharedPreferences("Settings", MODE_PRIVATE).getString("language", "vi")
        setLocale(lang ?: "vi")
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun highlightTab(selectedTab: TextView) {
        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)

        tab1.setBackgroundResource(R.drawable.tab_unselected_bg)
        tab1.setTextColor(resources.getColor(android.R.color.black))
        tab2.setBackgroundResource(R.drawable.tab_unselected_bg)
        tab2.setTextColor(resources.getColor(android.R.color.black))

        selectedTab.setBackgroundResource(R.drawable.tab_selected_bg)
        selectedTab.setTextColor(resources.getColor(android.R.color.white))
    }
}

