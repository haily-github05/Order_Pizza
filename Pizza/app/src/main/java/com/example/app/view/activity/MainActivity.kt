package com.example.app.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.app.MenuPizzaFragment
import com.example.app.R
import com.example.app.rest.RetrofitClient.apiService
import com.example.app.rest.RetrofitClient.cartApi
import com.example.app.view.fragment.CartFragment
import com.example.app.view.fragment.HomeFragment
import com.example.app.view.fragment.NotificationFragment
import com.example.app.view.fragment.OrderPizzaFragment
import com.example.app.view.fragment.ReviewFragment
import com.example.app.view.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocale()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Ánh xạ DrawerLayout và NavigationView
        mDrawerLayout = findViewById(R.id.main)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Xử lý nút thông báo
        val thongBaoButton = findViewById<ImageView>(R.id.thongbao)
        thongBaoButton.setOnClickListener {
            loadFragment(NotificationFragment())
        }

        // Ánh xạ BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_menu -> MenuPizzaFragment()
                R.id.nav_cart -> CartFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }

        // Hiển thị HomeFragment mặc định
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Hiển thị toast chào mừng
        val phone = intent.getStringExtra("phone")
        if (!phone.isNullOrEmpty()) {
            Toast.makeText(this, "Xin chào: $phone", Toast.LENGTH_SHORT).show()
        }

        // Xử lý WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_toolbar -> {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_menu -> loadFragment(MenuPizzaFragment())
            R.id.nav_cart -> loadFragment(CartFragment())
            R.id.nav_order -> loadFragment(OrderPizzaFragment())
            R.id.nav_user -> loadFragment(UserFragment())
            R.id.nav_review -> loadFragment(ReviewFragment())
            R.id.language -> {
                switchLanguage()
            }
            R.id.nav_logout -> {
                showLogoutConfirmationDialog()
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.END)
        return true
    }
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_confirm_title))
            .setMessage(getString(R.string.logout_confirm_message))
            .setPositiveButton(getString(R.string.logout_confirm_yes)) { _, _ ->
                clearCartAndLogout()
            }
            .setNegativeButton(getString(R.string.logout_confirm_no), null)
            .show()
    }


    private fun clearCartAndLogout() {
        // Lấy số bàn từ SharedPreferences
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val tableNumber = sharedPref.getString("TABLE_NUMBER", "")

        if (tableNumber.isNullOrEmpty()) {
            Toast.makeText(this, "Số bàn không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        // Chuẩn bị body cho API
        val body = mapOf("table_number" to tableNumber)

        // Gọi API xóa giỏ hàng từ server
        cartApi.clearCart(body).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("ClearCart", "Xóa giỏ hàng thành công")
                    sharedPref.edit().clear().apply()
                    val intent = Intent(this@MainActivity, HelloApp::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Log.e("ClearCart", "Lỗi server: ${response.code()}")
                    Toast.makeText(this@MainActivity, "Không thể xóa giỏ hàng (Lỗi server)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ClearCart", "Gọi API thất bại: ${t.message}")
                Toast.makeText(this@MainActivity, "Không thể kết nối tới server", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun switchLanguage() {
        val currentLang = getSharedPreferences("Settings", MODE_PRIVATE)
            .getString("language", "vi") ?: "vi"

        val newLang = if (currentLang == "vi") "en" else "vi"

        val editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("language", newLang)
        editor.apply()

        setLocale(newLang)
        recreate()
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    private fun loadLocale() {
        val sharedPref = getSharedPreferences("Settings", MODE_PRIVATE)
        val lang = sharedPref.getString("language", "vi")
        setLocale(lang ?: "vi")
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }
}