package dev.guilherme.financeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.guilherme.financeapp.R
import dev.guilherme.financeapp.databinding.FragmentTransactionsListBinding
import dev.guilherme.financeapp.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionsListFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels()
    private val args: TransactionsListFragmentArgs by navArgs()

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
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.transactions_list_menu, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.setSearchQuery(newText.orEmpty())
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.setTypeFilter(args.filterType)


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
            viewModel.allTransactionsWithCategory.collectLatest { transactions ->
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
        viewModel.setTypeFilter("ALL")
        _binding = null
    }
}