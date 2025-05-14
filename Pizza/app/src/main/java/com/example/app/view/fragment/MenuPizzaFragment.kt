package com.example.app

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.adapter.MenuAdapter
import com.example.app.databinding.FragmentMenuPizzaBinding
import com.example.app.model.Carts
import com.example.app.model.Products
import com.example.app.viewmodel.CartViewModel
import com.example.app.viewmodel.ProductViewModel

class MenuPizzaFragment : Fragment() {

    private var _binding: FragmentMenuPizzaBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabMap: Map<TextView, String>
    private var currentSelectedTab: TextView? = null
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: MenuAdapter
    private lateinit var cartViewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuPizzaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartViewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]

        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        adapter = MenuAdapter(
            products = emptyList(),
            cartViewModel = cartViewModel,
            onAddToCart = { cartItem: Carts ->
                cartViewModel.addProduct(cartItem)
            }
        )
        binding.offerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.offerRecyclerView.adapter = adapter

        tabMap = mapOf(
            binding.tabMua1Tang1 to "MUA1TANG1",
            binding.tabMua1Duoc3 to "MUA1TANG2",
            binding.tabCombo to "COMBO",
            binding.tabPizza to "PIZZA",
            binding.tabDouong to "DRINK",
            binding.tabMybox to "MYBOX",
            binding.tabMenu49k to "PIZZA49K",
            binding.tabPasta to "PASTA"
        )

        setupTabListeners()

        viewModel.products.observe(viewLifecycleOwner) { products ->
            if (currentSelectedTab == null) {
                val defaultCategory = "MUA1TANG1"
                val defaultTab = tabMap.entries.find { it.value == defaultCategory }?.key
                defaultTab?.let {
                    updateRecyclerView(defaultCategory, products)
                    highlightTab(it)
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }

        viewModel.fetchProducts()
    }

    private fun setupTabListeners() {
        for ((tab, category) in tabMap) {
            tab.setOnClickListener {
                viewModel.products.value?.let { products ->
                    updateRecyclerView(category, products)
                    highlightTab(tab)
                }
            }
        }
    }

    private fun updateRecyclerView(category: String, products: List<Products>) {
        val filteredProducts = products.filter { it.type == category }
        adapter.updateData(filteredProducts)
        Log.d("MenuPizzaFragment", "Category: $category, Products count: ${filteredProducts.size}")
    }

    private fun highlightTab(selectedTab: TextView) {
        if (currentSelectedTab == selectedTab) return

        currentSelectedTab?.setBackgroundResource(R.drawable.tab_unselected_bg)
        currentSelectedTab?.setTextColor(Color.BLACK)

        selectedTab.setBackgroundResource(R.drawable.tab_selected_bg)
        selectedTab.setTextColor(Color.WHITE)

        currentSelectedTab = selectedTab
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}