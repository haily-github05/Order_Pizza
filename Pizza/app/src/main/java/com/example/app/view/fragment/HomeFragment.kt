package com.example.app.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.app.MenuPizzaFragment
import com.example.app.R
import com.example.app.adapter.HomeAdapter
import com.example.app.adapter.ImageAdapter
import com.example.app.databinding.FragmentHomeBinding
import com.example.app.model.Carts
import com.example.app.model.Products
import com.example.app.rest.RetrofitClient
import com.example.app.ui.SearchResultFragment
import com.example.app.viewmodel.CartViewModel
import com.example.app.viewmodel.ProductViewModel
import com.example.app.model.Feedback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID
import retrofit2.Response
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var handler: Handler
    private var currentPage = 0

    private lateinit var homeAdapter: HomeAdapter
    private lateinit var productViewModel: ProductViewModel
    private lateinit var cartViewModel: CartViewModel

    private val imageList = listOf(
        R.drawable.image_page,
        R.drawable.bia_pizza,
        R.drawable.bia_pizza2
    )

    private val slideRunnable = object : Runnable {
        override fun run() {
            if (::imageAdapter.isInitialized && ::viewPager.isInitialized) {
                currentPage = (currentPage + 1) % imageAdapter.itemCount
                viewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, 3000)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo ViewModel
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        cartViewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]

        // Khởi tạo adapter và RecyclerView
        homeAdapter = HomeAdapter(
            products = emptyList(),
            cartViewModel = cartViewModel,
            onAddToCart = { cartItem ->
                cartViewModel.addProduct(cartItem)
            }
        )
        binding.search.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, SearchResultFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.search1.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, SearchResultFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.RecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.RecyclerView.adapter = homeAdapter

        // Quan sát dữ liệu sản phẩm
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            val pizzaProducts = products.filter { it.type == "PIZZA" }
            homeAdapter.updateData(pizzaProducts)
        }

        productViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }

        // Gọi API lấy danh sách sản phẩm
        productViewModel.fetchProducts()

        // Cập nhật lời chào theo giờ
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..10 -> getString(R.string.greeting_morning)
            in 11..13 -> getString(R.string.greeting_noon)
            in 14..17 -> getString(R.string.greeting_afternoon)
            else -> getString(R.string.greeting_evening)
        }
        binding.greetingText.text = greeting

        // Hiển thị số bàn
        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val tableNumber = sharedPref.getString("TABLE_NUMBER", "") ?: ""
        binding.tableInfo.text = getString(R.string.table_info, tableNumber)
        if (cartViewModel.getTableNumber().isEmpty() && tableNumber.isNotEmpty()) {
            cartViewModel.setTableNumber(tableNumber)
        }
        binding.order.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, OrderPizzaFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.contact.setOnClickListener {
            val bottomSheet = ContactFragment()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
        binding.feedback.setOnClickListener {
            val bottomSheet = FeedbackFragment()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.add2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, MenuPizzaFragment())
                .addToBackStack(null)
                .commit()
        }

        // Sự kiện xem điều khoản
        binding.terms.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, TermsFragment())
                .addToBackStack(null)
                .commit()
        }

        // Gọi hỗ trợ
        // Gọi hỗ trợ
        binding.tvSupport.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.support_dialog_title)
                .setMessage(R.string.support_dialog_message)
                .setPositiveButton(R.string.support_dialog_call) { _, _ ->
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:19001182")
                    startActivity(intent)
                }
                .setNegativeButton(R.string.support_dialog_cancel, null)
                .show()
        }

        // Thiết lập quảng cáo ViewPager
        handler = Handler(Looper.getMainLooper())
        imageAdapter = ImageAdapter(imageList)
        viewPager = binding.viewPager
        viewPager.adapter = imageAdapter
        handler.postDelayed(slideRunnable, 3000)
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(slideRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(slideRunnable)
        _binding = null
    }

}
