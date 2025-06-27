package dev.guilherme.financeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class EditTransactionDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val descriptionEditText = view.findViewById<EditText>(R.id.edit_text_dialog_description)
        val valueEditText = view.findViewById<EditText>(R.id.edit_text_dialog_value)
        val saveButton = view.findViewById<Button>(R.id.button_dialog_save)
        val cancelButton = view.findViewById<Button>(R.id.button_dialog_cancel)

        val description = requireArguments().getString(ARG_DESCRIPTION)
        val value = requireArguments().getDouble(ARG_VALUE)
        descriptionEditText.setText(description)
        valueEditText.setText(value.toString())

        cancelButton.setOnClickListener {
            dismiss()
        }

        saveButton.setOnClickListener {
            val newDescription = descriptionEditText.text.toString()
            val newValueString = valueEditText.text.toString().replace(',', '.')

            if (newDescription.isNotEmpty() && newValueString.isNotEmpty()) {
                val newValue = newValueString.toDouble()

                val result = bundleOf(
                    RESULT_DESCRIPTION to newDescription,
                    RESULT_VALUE to newValue
                )
                setFragmentResult(REQUEST_KEY, result)
                dismiss()
            }
        }
    }
    companion object {
        const val TAG = "EditTransactionDialog"
        const val REQUEST_KEY = "edit_transaction_request"
        const val RESULT_DESCRIPTION = "result_description"
        const val RESULT_VALUE = "result_value"

        private const val ARG_DESCRIPTION = "arg_description"
        private const val ARG_VALUE = "arg_value"

        fun newInstance(description: String, value: Double): EditTransactionDialogFragment {
            val args = Bundle()
            args.putString(ARG_DESCRIPTION, description)
            args.putDouble(ARG_VALUE, value)

            val fragment = EditTransactionDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}