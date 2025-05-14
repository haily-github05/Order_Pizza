package com.example.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.adapter.MenuAdapter
import com.example.app.databinding.FragmentSearchResultBinding
import com.example.app.model.Carts
import com.example.app.viewmodel.CartViewModel
import com.example.app.viewmodel.SearchViewModel

class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: MenuAdapter
//    private lateinit var cartViewModel: CartViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("SearchFragment", "Khởi tạo SearchResultFragment")
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        adapter = MenuAdapter(
            products = emptyList(),
            cartViewModel = cartViewModel,
            onAddToCart = { cartItem: Carts ->
                cartViewModel.addProduct(cartItem)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        Log.d("SearchFragment", "RecyclerView: layoutManager và adapter đã được gán")

        setupSearchBar()
        observeViewModel()
    }

    private fun setupSearchBar() {
        val suggestionAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        binding.searchBar.setAdapter(suggestionAdapter)
        binding.searchBar.threshold = 1

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                Log.d("SearchFragment", "Tìm kiếm với query: $query")
                viewModel.searchProducts(query)
            }
        })

        binding.searchBar.setOnItemClickListener { _, _, position, _ ->
            val selectedName = suggestionAdapter.getItem(position)
            Log.d("SearchFragment", "Gợi ý được chọn: $selectedName")
            binding.searchBar.setText(selectedName)
            viewModel.searchProducts(selectedName ?: "")
        }
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            Log.d("SearchFragment", "Cập nhật RecyclerView với ${products.size} sản phẩm - $products")
            adapter.updateData(products)
        }

        viewModel.suggestions.observe(viewLifecycleOwner) { suggestions ->
            Log.d("SearchFragment", "Cập nhật gợi ý: ${suggestions.size} mục - $suggestions")
            val suggestionAdapter = binding.searchBar.adapter as ArrayAdapter<String>
            suggestionAdapter.clear()
            suggestionAdapter.addAll(suggestions)
            suggestionAdapter.notifyDataSetChanged()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("SearchFragment", "Trạng thái tải: $isLoading")
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Log.d("SearchFragment", "Lỗi: $error")
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}