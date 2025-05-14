package com.example.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.app.R
class TermsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tableNumber = arguments?.getString("TABLE_NUMBER") ?: ""
        return inflater.inflate(R.layout.fragment_terms, container, false)
    }
}
