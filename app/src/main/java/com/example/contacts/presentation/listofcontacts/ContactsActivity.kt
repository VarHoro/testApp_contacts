package com.example.contacts.presentation.listofcontacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.contacts.R
import org.koin.android.viewmodel.ext.android.viewModel

class ContactsActivity : AppCompatActivity() {

    private val viewModel : ContactsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

    }
}
