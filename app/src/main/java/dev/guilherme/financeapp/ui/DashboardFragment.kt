package dev.guilherme.financeapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import dev.guilherme.financeapp.FinanceApplication
import dev.guilherme.financeapp.R
import dev.guilherme.financeapp.data.CategoryTotal
import dev.guilherme.financeapp.databinding.FragmentDashboardBinding
import dev.guilherme.financeapp.viewmodel.TransactionViewModel
import dev.guilherme.financeapp.viewmodel.TransactionViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels {
        val database = (requireActivity().application as FinanceApplication).database
        TransactionViewModelFactory(database.transactionDao(), database.categoryDao())
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.expenseByCategory.collectLatest { categoryTotals ->
                setupPieChart(categoryTotals)
            }
        }
        binding.buttonVerTransacoes.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_transactionsListFragment)
        }
    }

    private fun setupPieChart(data: List<CategoryTotal>) {
        if (data.isEmpty()) {
            binding.pieChart.visibility = View.GONE
            return
        }
        binding.pieChart.visibility = View.VISIBLE

        val entries = ArrayList<PieEntry>()
        for (item in data) {
            entries.add(PieEntry(item.total.toFloat(), item.categoryName))
        }

        val dataSet = PieDataSet(entries, "Despesas por Categoria")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.purple_200),
            ContextCompat.getColor(requireContext(), R.color.teal_700),
            Color.CYAN,
            Color.MAGENTA,
            Color.YELLOW,
        )
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(binding.pieChart))

        binding.pieChart.apply {
            this.data = pieData
            setUsePercentValues(true)
            description.text = "Distribuição de Despesas"
            isDrawHoleEnabled = true
            holeRadius = 58f
            transparentCircleRadius = 61f
            legend.isEnabled = false
            animateY(1200)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}