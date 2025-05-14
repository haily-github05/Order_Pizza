package com.example.app.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.app.R

class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val muiten = view.findViewById<ImageView>(R.id.muiten)
        muiten.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, OrderPizzaFragment())
                .addToBackStack(null)
                .commit()
        }

        val thongbao = view.findViewById<LinearLayout>(R.id.tabthongbao)
        thongbao.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, NotificationFragment())
                .addToBackStack(null)
                .commit()
        }

        val callIcon = view.findViewById<ImageView>(R.id.icon_lienhe)
        callIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:0865529572")
            startActivity(intent)
        }

        val fbIcon = view.findViewById<ImageView>(R.id.icon_fb)
        fbIcon.setOnClickListener {
            val fbIntent = Intent(Intent.ACTION_VIEW)
            fbIntent.data = Uri.parse("https://www.facebook.com/pizzahot")
            startActivity(fbIntent)
        }

        val emailIcon = view.findViewById<ImageView>(R.id.icon_email)
        emailIcon.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:pizzahot@example.com")
                putExtra(Intent.EXTRA_SUBJECT, "Liên hệ")
                putExtra(Intent.EXTRA_TEXT, "Xin chào, tôi cần hỗ trợ...")
            }
            startActivity(Intent.createChooser(emailIntent, "Chọn ứng dụng email"))
        }

    }
}
