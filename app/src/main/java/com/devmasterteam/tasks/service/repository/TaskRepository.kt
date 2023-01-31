package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient
import com.devmasterteam.tasks.service.repository.remote.TaskService

class TaskRepository(context: Context): BaseRepository(context) {

    private val remote = RetrofitClient.getService(TaskService::class.java)

    fun list(listener: APIListener<List<TaskModel>>) {
        isConnection(listener)
        executeCall(remote.list(), listener)
    }

    fun listNext(listener: APIListener<List<TaskModel>>) {
        isConnection(listener)
        executeCall(remote.listNext(), listener)
    }

    fun listOverDue(listener: APIListener<List<TaskModel>>) {
        isConnection(listener)
        executeCall(remote.listOverdue(), listener)
    }

    fun create(task: TaskModel, listener: APIListener<Boolean>) {
        isConnection(listener)
        val call = remote.create(task.priorityId, task.description, task.dueDate, task.complete)
        executeCall(call, listener)
    }

    fun delete(id: Int, listener: APIListener<Boolean>) {
        isConnection(listener)
        executeCall(remote.delete(id), listener)
    }

    fun complete(id: Int, listener: APIListener<Boolean>) {
        isConnection(listener)
        executeCall(remote.complete(id), listener)
    }

    fun undo(id: Int, listener: APIListener<Boolean>) {
        isConnection(listener)
        executeCall(remote.undo(id), listener)
    }

    fun load(id: Int, listener: APIListener<TaskModel>) {
        isConnection(listener)
        executeCall(remote.load(id), listener)
    }

    fun upload(task: TaskModel, listener: APIListener<Boolean>) {
        isConnection(listener)
        val call = remote.update(
            task.id, task.priorityId, task.description, task.dueDate, task.complete)
        executeCall(call, listener)
    }

}