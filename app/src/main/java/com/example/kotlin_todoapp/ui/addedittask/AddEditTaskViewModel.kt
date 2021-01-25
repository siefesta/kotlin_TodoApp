package com.example.kotlin_todoapp.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_todoapp.data.Task
import com.example.kotlin_todoapp.data.TaskDao
import com.example.kotlin_todoapp.ui.ADD_TASK_RESULT_OK
import com.example.kotlin_todoapp.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(

    private val taskDao: TaskDao,

    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")    //할일 목록 가져오기

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val addTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addTaskEventChannel.receiveAsFlow()

    fun onSaveClick(){
        //내용 공백 시 등록불가
        if (taskName.isBlank()) {
            showInvalidInputMessage("내용을 입력해 주세요.")
            return
        }

        if (task != null){
            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updatedTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportance)
            createTask(newTask)
        }
    }

    private fun createTask(task : Task) = viewModelScope.launch {
        taskDao.insert(task)
        addTaskEventChannel.send(AddEditTaskEvent.NavigationBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updatedTask(task : Task) = viewModelScope.launch {
        taskDao.update(task)
        addTaskEventChannel.send(AddEditTaskEvent.NavigationBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage (val msg: String) : AddEditTaskEvent()
        data class NavigationBackWithResult(val result : Int) : AddEditTaskEvent()
    }

}