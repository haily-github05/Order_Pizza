package com.example.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.app.R
class NotificationFragment : Fragment() {

    private lateinit var tabKhuyenMai: TextView
    private lateinit var tabCuaBan: TextView
    private lateinit var container: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabKhuyenMai = view.findViewById(R.id.tabTinkhuyemai)
        tabCuaBan = view.findViewById(R.id.tabTincuaban)
        container = view.findViewById(R.id.tabContentContainer)

        setActiveTab(tabKhuyenMai, tabCuaBan)
        loadContent(R.layout.view_khuyen_mai)

        tabKhuyenMai.setOnClickListener {
            setActiveTab(tabKhuyenMai, tabCuaBan)
            loadContent(R.layout.view_khuyen_mai)
        }

        tabCuaBan.setOnClickListener {
            setActiveTab(tabCuaBan, tabKhuyenMai)
            loadContent(R.layout.view_cua_ban)
        }
    }

    private fun setActiveTab(active: TextView, inactive: TextView) {
        active.setBackgroundResource(R.drawable.tab_selected_bg)
        active.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))

        inactive.setBackgroundResource(R.drawable.tab_unselected_bg)
        inactive.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
    }

    private fun loadContent(layoutId: Int) {
        container.removeAllViews()
        val contentView = layoutInflater.inflate(layoutId, container, false)
        container.addView(contentView)
    }
}

