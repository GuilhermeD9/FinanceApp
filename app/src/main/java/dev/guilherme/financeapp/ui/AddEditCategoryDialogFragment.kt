package dev.guilherme.financeapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import dev.guilherme.financeapp.R

class AddEditCategoryDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_edit_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleTextView = view.findViewById<TextView>(R.id.text_view_dialog_title)
        val nameEditText = view.findViewById<EditText>(R.id.edit_text_category_name)
        val saveButton = view.findViewById<Button>(R.id.button_dialog_save)
        val cancelButton = view.findViewById<Button>(R.id.button_dialog_cancel)

        val categoryId = arguments?.getInt(ARG_CATEGORY_ID, -1) ?: -1
        val categoryName = arguments?.getString(ARG_CATEGORY_NAME)

        if (categoryName != null) {
            titleTextView.text = "Editar Categoria"
            nameEditText.setText(categoryName)
        }

        cancelButton.setOnClickListener { dismiss() }

        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString()
            if (newName.isNotEmpty()) {
                val result = bundleOf(
                    RESULT_CATEGORY_ID to categoryId,
                    RESULT_CATEGORY_NAME to newName
                )
                setFragmentResult(REQUEST_KEY, result)
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        const val TAG = "AddEditCategoryDialog"
        const val REQUEST_KEY = "add_edit_category_request"
        const val RESULT_CATEGORY_ID = "result_id"
        const val RESULT_CATEGORY_NAME = "result_name"

        private const val ARG_CATEGORY_ID = "arg_id"
        private const val ARG_CATEGORY_NAME = "arg_name"

        fun newInstance(id: Int, name: String): AddEditCategoryDialogFragment {
            val args = Bundle()
            args.putInt(ARG_CATEGORY_ID, id)
            args.putString(ARG_CATEGORY_NAME, name)
            val fragment = AddEditCategoryDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

}