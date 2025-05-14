package com.example.app.view.activity

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.app.rest.RetrofitClient
import com.example.app.view.fragment.Login1Fragment
import com.example.app.view.fragment.Login2Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HelloApp : AppCompatActivity() {

    private var isVietnamese = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello_app)

        // Khởi tạo các tab
        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)

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

        val languageButton = findViewById<Button>(R.id.languageButton)
        languageButton.setOnClickListener {
            isVietnamese = !isVietnamese
            if (isVietnamese) {
                languageButton.text = "English"
                tab1.text = "Khách vãng lai"
                tab2.text = "Khách thành viên"
            } else {
                languageButton.text = "Tiếng Việt"
                tab1.text = "Guest"
                tab2.text = "Member"
            }
        }
    }

    // Hàm làm nổi bật tab khi chọn
    private fun highlightTab(selectedTab: TextView) {
        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)

        // Đặt lại kiểu cho tất cả các tab
        tab1.setBackgroundResource(R.drawable.tab_unselected_bg)
        tab1.setTextColor(resources.getColor(android.R.color.black))
        tab2.setBackgroundResource(R.drawable.tab_unselected_bg)
        tab2.setTextColor(resources.getColor(android.R.color.black))

        // Nổi bật tab được chọn
        selectedTab.setBackgroundResource(R.drawable.tab_selected_bg)
        selectedTab.setTextColor(resources.getColor(android.R.color.white))
    }

}
