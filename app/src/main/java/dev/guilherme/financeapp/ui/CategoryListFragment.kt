package dev.guilherme.financeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.guilherme.financeapp.data.Category
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
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryAdapter = CategoryAdapter(
            onEditClick = { category ->
                val dialog = AddEditCategoryDialogFragment.newInstance(category.id, category.name)
                dialog.show(parentFragmentManager, AddEditCategoryDialogFragment.TAG)
            },
            onDeleteClick = { category ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Deletar Categoria")
                    .setMessage("Tem certeza que deseja deletar a categoria '${category.name}'? Isso não pode ser desfeito.")
                    .setPositiveButton("Deletar") { _, _ ->
                        viewModel.delete(category)
                        Toast.makeText(context, "Categoria deletada", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
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
            val dialog = AddEditCategoryDialogFragment()
            dialog.show(parentFragmentManager, AddEditCategoryDialogFragment.TAG)
        }

        parentFragmentManager.setFragmentResultListener(AddEditCategoryDialogFragment.REQUEST_KEY, this) { _, bundle ->
            val id = bundle.getInt(AddEditCategoryDialogFragment.RESULT_CATEGORY_ID, -1)
            val name = bundle.getString(AddEditCategoryDialogFragment.RESULT_CATEGORY_NAME)!!

            if (id != -1) {
                viewModel.update(Category(id, name))
                Toast.makeText(context, "Categoria atualizada", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insert(Category(name = name))
                Toast.makeText(context, "Categoria adicionada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}