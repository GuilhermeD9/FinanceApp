package dev.guilherme.financeapp

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
import dev.guilherme.financeapp.databinding.FragmentAddEditTransactionBinding
import kotlinx.coroutines.launch

class AddEditTransactionFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels {
        TransactionViewModelFactory((requireActivity().application as FinanceApplication).database.transactionDao())
    }

    private val args: AddEditTransactionFragmentArgs by navArgs()
    private var _binding: FragmentAddEditTransactionBinding? = null
    private val binding get() = _binding!!

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
                    }
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            val description = binding.editTextDescription.text.toString()
            val valueString = binding.editTextValue.text.toString().replace(',', '.')

            if (description.isNotEmpty() && valueString.isNotEmpty()) {
                val value = valueString.toDouble()

                if (args.transactionId != -1) {
                    val updatedTransaction = Transaction(args.transactionId, description, value)
                    viewModel.update(updatedTransaction)
                    Toast.makeText(context, "Transação atualizada!", Toast.LENGTH_SHORT).show()
                } else {
                    val newTransaction = Transaction(description = description, value = value)
                    viewModel.insert(newTransaction)
                    Toast.makeText(context, "Transação salva!", Toast.LENGTH_SHORT).show()
                }
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}