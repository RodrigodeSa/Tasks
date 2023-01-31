package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PersonRepository
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val personRepository = PersonRepository(application.applicationContext)
    private val securityPreferences = SecurityPreferences(application.applicationContext)
    private val priorityRepository = PriorityRepository(application.applicationContext)
    private val _loginMutableLiveData = MutableLiveData<ValidationModel>()
    private val _loggedUser = MutableLiveData<Boolean>()

    val liveDataLogin: LiveData<ValidationModel> = _loginMutableLiveData
    val loggedUser: LiveData<Boolean> = _loggedUser

    /**
     * Faz login usando API
     */
//FAZ CONEXÃO COM LOGIN(PersonRepository) ATRAVÉS DE CLASSE ANÔNIMA(object : )(CHAMA O MÉTODO, TENDO
//  ACESSO AO RESULT, MAS É NECESSÁRIO QUE O doLogin CONSTRUA A REGRA DE NEGÓCIO)
    fun doLogin(email: String, password: String) {
        personRepository.login(email, password, object : APIListener<PersonModel> {
            override fun onSuccess(result: PersonModel) {
//              Salva os dados do usuário para o mesmo se manter logado
                securityPreferences.store(TaskConstants.SHARED.TOKEN_KEY, result.token)
                securityPreferences.store(TaskConstants.SHARED.PERSON_KEY, result.personKey)
                securityPreferences.store(TaskConstants.SHARED.PERSON_NAME, result.name)

                RetrofitClient.addHeaders(result.token, result.personKey)

                _loginMutableLiveData.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _loginMutableLiveData.value = ValidationModel(message)
            }
        })
    }

    fun verifyLoggedUser() {
        val token = securityPreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val person = securityPreferences.get(TaskConstants.SHARED.PERSON_KEY)
//      Atualiza o header caso passe dias sem acesar
        RetrofitClient.addHeaders(token, person)

        val logged = (token != "" && person != "")
        _loggedUser.value = logged

        if (!logged) {
            priorityRepository.list(object : APIListener<List<PriorityModel>> {
                override fun onSuccess(result: List<PriorityModel>) {
                    priorityRepository.save(result)
                }

                override fun onFailure(message: String) {
                    val s = ""
                }
            })
        }
    }

}