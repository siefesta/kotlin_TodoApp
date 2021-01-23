package com.example.kotlin_todoapp.ui.deleteallcompleted

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels

class DeleteAllCompletedDialogFragment : DialogFragment() {

    private val viewModel : DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
       AlertDialog.Builder(requireContext())
           .setTitle("삭제 확인")
           .setMessage("완료된 모든 일정을 삭제하시겠습니까?")
           .setNegativeButton("취소", null)
           .setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->
               //뷰모델 호출

           }
           .create()

}