package com.example.kotlin_todoapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.kotlin_todoapp.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
) : ViewModel(){

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)    //날짜별 정렬
    val hideComleted = MutableStateFlow(false)


    private val taskFlow = searchQuery.flatMapLatest {
        taskDao.getTasks(it)
    }

    val tasks = taskFlow.asLiveData()
}

enum class SortOrder {
    BY_NAME, BY_DATE
}