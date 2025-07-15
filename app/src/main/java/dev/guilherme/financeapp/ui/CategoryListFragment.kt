package dev.guilherme.financeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.guilherme.financeapp.databinding.FragmentCategoryListBinding
import dev.guilherme.financeapp.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryListFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels()

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryAdapter = CategoryAdapter(
            onEditClick = { category ->
                Toast.makeText(context, "Editar: ${category.name}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { category ->
            Toast.makeText(context, "Deletar: ${category.id}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allCategories.collectLatest { categories ->
                categoryAdapter.submitList(categories)
            }
        }

        binding.fabAddCategory.setOnClickListener{
            Toast.makeText(context, "Adicionar nova categoria", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}