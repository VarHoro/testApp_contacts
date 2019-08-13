package com.example.contacts.app

import android.app.Application
import com.example.contacts.data.ContactsDataSourceImpl
import com.example.contacts.domain.ContactsDataSource
import com.example.contacts.domain.Interactor
import com.example.contacts.domain.InteractorImpl
import com.example.contacts.presentation.listofcontacts.ContactsViewModel
import com.example.contacts.presentation.onecontact.OneContactViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val contactsModule = module {
    single<ContactsDataSource> { (application: Application, scope: CoroutineScope) -> ContactsDataSourceImpl(application, scope) }
    single<Interactor> { InteractorImpl(get()) }

    viewModel { (application: Application) -> ContactsViewModel(application) }
    viewModel {(application: Application) -> OneContactViewModel(application)}
}