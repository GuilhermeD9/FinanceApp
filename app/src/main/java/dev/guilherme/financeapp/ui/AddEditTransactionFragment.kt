package dev.guilherme.financeapp.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.guilherme.financeapp.R
import dev.guilherme.financeapp.data.Category
import dev.guilherme.financeapp.data.Transaction
import dev.guilherme.financeapp.databinding.FragmentAddEditTransactionBinding
import dev.guilherme.financeapp.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddEditTransactionFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels()

    private val args: AddEditTransactionFragmentArgs by navArgs()
    private var _binding: FragmentAddEditTransactionBinding? = null
    private val binding get() = _binding!!
    private val selectedDate = Calendar.getInstance()
    private var categories: List<Category> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf<String>())
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allCategories.collect { fetchedCategories ->
                categories = fetchedCategories
                val categoryNames = categories.map { it.name }
                categoryAdapter.clear()
                categoryAdapter.addAll(categoryNames)
                categoryAdapter.notifyDataSetChanged()
                checkEditModeAndPreselectCategory()
            }
        }

        if (args.transactionId != -1) {
            checkEditModeAndPreselectCategory()
        } else {
            updateDateButtonText()
        }

        binding.buttonDatePicker.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonSave.setOnClickListener {
            saveTransaction()
        }

        binding.buttonManageCategoriesShortcut.setOnClickListener {
            val action = AddEditTransactionFragmentDirections.actionAddEditTransactionFragmentToCategoryListFragment()
            findNavController().navigate(action)
        }
    }

    private fun checkEditModeAndPreselectCategory() {
        if (args.transactionId != -1) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getTransactionById(args.transactionId).collectLatest { transaction ->
                    transaction?.let {
                        binding.editTextDescription.setText(it.description)
                        binding.editTextValue.setText(it.value.toString())
                        if (it.type == "RECEITA") {
                            binding.radioButtonReceita.isChecked = true
                        } else {
                            binding.radioButtonDespesa.isChecked = true
                        }
                        selectedDate.timeInMillis = it.date
                        updateDateButtonText()
                        
                        val categoryPosition = categories.indexOfFirst { category -> category.id == it.categoryId }
                        if (categoryPosition != -1) {
                            binding.spinnerCategory.setSelection(categoryPosition)
                        }
                    }
                }
            }
        }
    }

    private fun saveTransaction() {
        val description = binding.editTextDescription.text.toString()
        val valueString = binding.editTextValue.text.toString().replace(',', '.')
        val selectedTypeId = binding.radioGroupType.checkedRadioButtonId
        val type = if (selectedTypeId == R.id.radio_button_receita) "RECEITA" else "DESPESA"
        val date = selectedDate.timeInMillis

        val selectedCategoryPosition = binding.spinnerCategory.selectedItemPosition
        if (selectedCategoryPosition < 0 || categories.isEmpty()) {
            Toast.makeText(context, "Por favor, selecione uma categoria", Toast.LENGTH_SHORT).show()
            return
        }
        val categoryId = categories[selectedCategoryPosition].id

        if (description.isNotEmpty() && valueString.isNotEmpty()) {
            val value = valueString.toDouble()
            val transaction = Transaction(args.transactionId.let { if (it == -1) 0 else it }, description, value, type, date, categoryId)

            if (args.transactionId != -1) {
                viewModel.update(transaction)
                Toast.makeText(context, "Transação atualizada!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insert(transaction)
                Toast.makeText(context, "Transação salva!", Toast.LENGTH_SHORT).show()
            }
            findNavController().popBackStack()
        } else {
            Toast.makeText(context, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateButtonText()
        }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateButtonText() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.buttonDatePicker.text = format.format(selectedDate.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}