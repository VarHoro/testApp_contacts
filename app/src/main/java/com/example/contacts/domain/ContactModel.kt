package com.example.contacts.domain

data class ContactModel(
    val firstName: String? = null,
    val secondName: String? = null,
    val phone: String = "",
    val ringtone: String? = null,
    val note: String? = null
)