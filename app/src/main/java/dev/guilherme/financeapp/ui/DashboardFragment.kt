package dev.guilherme.financeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.guilherme.financeapp.FinanceApplication
import dev.guilherme.financeapp.R
import dev.guilherme.financeapp.databinding.FragmentDashboardBinding
import dev.guilherme.financeapp.viewmodel.TransactionViewModel
import dev.guilherme.financeapp.viewmodel.TransactionViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels {
        TransactionViewModelFactory((requireActivity().application as FinanceApplication).database.transactionDao())
    }
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardState.collectLatest { state ->
                val formatadorMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                binding.textViewSaldo.text = formatadorMoeda.format(state.saldo)
                binding.textViewReceitas.text = formatadorMoeda.format(state.totalReceitas)
                binding.textViewDespesas.text = formatadorMoeda.format(state.totalDespesas)
            }
        }
        binding.buttonVerTransacoes.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_transactionsListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}