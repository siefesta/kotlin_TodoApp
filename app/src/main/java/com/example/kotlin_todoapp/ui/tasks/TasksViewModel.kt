package com.example.kotlin_todoapp.ui.tasks

import androidx.arch.core.executor.TaskExecutor
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.kotlin_todoapp.data.PreferencesManager
import com.example.kotlin_todoapp.data.SortOrder
import com.example.kotlin_todoapp.data.Task
import com.example.kotlin_todoapp.data.TaskDao
import com.example.kotlin_todoapp.ui.ADD_TASK_RESULT_OK
import com.example.kotlin_todoapp.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(

    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,

    @Assisted private val state : SavedStateHandle

) : ViewModel(){

    val searchQuery = state.getLiveData("searchQuery", "")  //정렬 필터 : 검색어

    val preferencesFlow = preferencesManager.preferecesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val taskFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow

    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch{
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task : Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task : Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result : Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("추가되었습니다.")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("변경되었습니다.")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
    }


    sealed class TasksEvent {
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task : Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task : Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg : String) : TasksEvent()
        object NavigateToDeleteAllCompletedScreen : TasksEvent()
    }
}


