package com.example.kotlin_todoapp.ui.deleteallcompleted

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedFragment : DialogFragment(){

    private val viewModel : DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("모든 완료작업을 삭제하시겠습니까?")
            .setNegativeButton("취소", null)
            .setPositiveButton("예, 삭제합니다.") { _, _ ->
                viewModel.onConfirmClick()
            }
            .create()
}