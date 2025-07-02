package dev.guilherme.financeapp.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dev.guilherme.financeapp.FinanceApplication
import dev.guilherme.financeapp.R
import dev.guilherme.financeapp.data.Transaction
import dev.guilherme.financeapp.databinding.FragmentAddEditTransactionBinding
import dev.guilherme.financeapp.viewmodel.TransactionViewModel
import dev.guilherme.financeapp.viewmodel.TransactionViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditTransactionFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels {
        val database = (requireActivity().application as FinanceApplication).database
        TransactionViewModelFactory(database.transactionDao(), database.categoryDao())
    }

    private val args: AddEditTransactionFragmentArgs by navArgs()
    private var _binding: FragmentAddEditTransactionBinding? = null
    private val binding get() = _binding!!
    private val selectedDate = Calendar.getInstance()

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

        if (args.transactionId != -1) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getTransactionById(args.transactionId).collect { transaction ->
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
                    }
                }
            }
        } else {
            updateDateButtonText()
        }

        binding.buttonDatePicker.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonSave.setOnClickListener {
            saveTransaction()
        }

    }

    private fun saveTransaction() {
        val description = binding.editTextDescription.text.toString()
        val valueString = binding.editTextValue.text.toString().replace(',', '.')
        val selectedTypeId = binding.radioGroupType.checkedRadioButtonId
        val type = if (selectedTypeId == R.id.radio_button_receita) "RECEITA" else "DESPESA"
        val date = selectedDate.timeInMillis

        if (description.isNotEmpty() && valueString.isNotEmpty()) {
            val value = valueString.toDouble()

            if (args.transactionId != -1) {
                val updatedTransaction = Transaction(args.transactionId, description, value, type, date)
                viewModel.update(updatedTransaction)
                Toast.makeText(context, "Transação atualizada!", Toast.LENGTH_SHORT).show()
            } else {
                val newTransaction = Transaction(description = description, value = value, type = type, date = date)
                viewModel.insert(newTransaction)
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