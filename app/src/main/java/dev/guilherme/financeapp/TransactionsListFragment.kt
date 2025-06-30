package dev.guilherme.financeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.guilherme.financeapp.databinding.FragmentTransactionsListBinding
import kotlinx.coroutines.launch

class TransactionsListFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels {
        TransactionViewModelFactory((requireActivity().application as FinanceApplication).database.transactionDao())
    }

    private var _binding: FragmentTransactionsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TransactionAdapter(
            onItemClick = { transaction ->
                // TODO: Navegar para a tela de edição
                Toast.makeText(context, "Editar: ${transaction.description}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { transaction ->
                viewModel.delete(transaction)
                Toast.makeText(context, "Transação deletada", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewTransactions.adapter = adapter
        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allTransactions.collect { transactions ->
                adapter.submitList(transactions)
            }
        }

        binding.fabAddTransaction.setOnClickListener {
            // TODO: Navegar para a tela de adicionar
            Toast.makeText(context, "Adicionar nova transação", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}