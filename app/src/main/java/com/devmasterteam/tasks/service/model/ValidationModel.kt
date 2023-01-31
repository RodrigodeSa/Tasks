package com.devmasterteam.tasks.service.model

class ValidationModel(message: String = "") {

    private var status: Boolean = true
    private var validationMessage: String = ""

    init {
        if (message != "") {
            validationMessage = message
            status = false
        }
    }

    fun status() = status
    fun message() = validationMessage
    // CRIOU UMA FUNÇÃO PARA MESSAGE/STATUS PARA N EXPOR A VIRIÁVEL

}