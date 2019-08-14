package com.example.contacts.presentation.onecontact

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.contacts.domain.ContactModel
import com.example.contacts.domain.Interactor
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class OneContactViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val interactor: Interactor by inject { parametersOf(application) }

    val isExistingContact = ObservableBoolean(false)
    val imageText = ObservableField("")
    val firstNameText = ObservableField("")
    val secondNameText = ObservableField("")
    val phoneText = ObservableField("")
    val noteText = ObservableField("")
    val ringtoneText = ObservableField("Default")

    private lateinit var contactModel: LiveData<ContactModel>
    private lateinit var observer: Observer<ContactModel>

    fun getByPhone(getPhone: String): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        liveData.postValue(false)
        contactModel = interactor.getByPhone(getPhone)
        observer = Observer { contactModel ->
            imageText.set(contactModel.image)
            firstNameText.set(contactModel.firstName)
            secondNameText.set(contactModel.secondName)
            phoneText.set(contactModel.phone)
            noteText.set(contactModel.note)
            ringtoneText.set(contactModel.ringtone)
            isExistingContact.set(true)
            liveData.postValue(true)
            dataReceived()
        }
        contactModel.observeForever(observer)
        return liveData
    }

    fun insert() {
        val contact = ContactModel(
            firstName = firstNameText.get(),
            secondName = secondNameText.get(),
            phone = phoneText.get().toString(),
            ringtone = ringtoneText.get(),
            image = imageText.get(),
            note = noteText.get()
        )
        interactor.insert(contact)
    }

    private fun dataReceived() {
        contactModel.removeObserver(observer)
    }

    fun update() {
        val contact = ContactModel(
            image = imageText.get(),
            firstName = firstNameText.get(),
            secondName = secondNameText.get(),
            phone = phoneText.get() ?: "",
            note = noteText.get(),
            ringtone = ringtoneText.get()
        )
        if (contactModel.value != contact) {
            interactor.update(contact)
        }
    }

    fun delete() {
        val model: ContactModel? = contactModel.value
        if (model != null) {
            interactor.delete(model)
        }
    }
}