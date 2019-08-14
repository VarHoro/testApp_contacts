package com.example.contacts.presentation.listofcontacts

data class AdapterModel(
    val isContact: Boolean,
    val name: String?,
    val phone: String?,
    val letter: Char?
) {
    companion object {
        const val TYPE_LETTER = 1
        const val TYPE_CONTACT = 0
    }

    fun getType(): Int {
        return if (isContact) {
            TYPE_CONTACT
        } else {
            TYPE_LETTER
        }
    }
}