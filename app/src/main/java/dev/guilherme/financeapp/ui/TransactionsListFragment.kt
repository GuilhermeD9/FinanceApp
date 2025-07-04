package dev.guilherme.financeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.guilherme.financeapp.databinding.FragmentTransactionsListBinding
import dev.guilherme.financeapp.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionsListFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels()

    private var _binding: FragmentTransactionsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TransactionAdapter(
            onItemClick = { transaction ->
                val action = TransactionsListFragmentDirections
                    .actionTransactionsListFragmentToAddEditTransactionFragment(transaction.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { transaction ->
                viewModel.delete(transaction)
                Toast.makeText(context, "Transação deletada", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewTransactions.adapter = adapter
        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allTransactionsWithCategory.collect { transactions ->
                adapter.submitList(transactions)
            }
        }

        binding.fabAddTransaction.setOnClickListener {
            val action = TransactionsListFragmentDirections
                .actionTransactionsListFragmentToAddEditTransactionFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}