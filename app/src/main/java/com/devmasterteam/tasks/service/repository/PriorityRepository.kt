package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.repository.local.TaskDatabase
import com.devmasterteam.tasks.service.repository.remote.PriorityService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient


class PriorityRepository(context: Context) : BaseRepository(context) {

    private val remote = RetrofitClient.getService(PriorityService::class.java)
    private val database = TaskDatabase.getDatabase(context).priorityDAO()

//  CACHE-> Utilizada quando se quer reduzir o número de chamadas ao bd (otimização/ganho performace)
//  Se a variável fosse criada vinculada a instancia do PriorityRepository toda vez que a mesma fosse
// chamada se renovaria os valores, por isso criamos em static para manter as mesmas informações
// fixas na cache. Não pertence mais do escopo da instancia
    companion object {
        private val cache = mutableMapOf<Int, String>()
        fun getDescription(id: Int): String {
            return cache[id] ?: ""
        }
        fun setDescription(id: Int, str: String) { cache[id] = str }
    }

    fun getDescription(id: Int): String {
//      Escopo da classe
        val cached = PriorityRepository.getDescription(id)
        return if (cached == "") {
            val description = database.getDescription(id)
            setDescription(id, description)
            description
        } else {
            cached
        }
    }

    fun list(listener: APIListener<List<PriorityModel>>) {
        isConnection(listener)
        executeCall(remote.list(), listener)
    }

    fun list(): List<PriorityModel> {
        return database.list()
    }

    fun save(list: List<PriorityModel>) {
//      Apagando o grau de atualidades e inserindo novamente para que esteja sempre atualizada
        database.clear()
        database.save(list)
    }
}

