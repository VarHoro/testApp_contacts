package com.example.contacts.presentation.listofcontacts

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.R
import com.example.contacts.databinding.ActivityContactsBinding
import com.example.contacts.presentation.onecontact.OneContactActivity
import kotlinx.android.synthetic.main.activity_contacts.*
import org.koin.android.viewmodel.ext.android.viewModel

class ContactsActivity : AppCompatActivity() {

    private val viewModel: ContactsViewModel by viewModel()
    private lateinit var adapter: ContactListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityContactsBinding>(this, R.layout.activity_contacts)
        binding.lifecycleOwner = this

        //toolbar
        setSupportActionBar(toolbar_contacts)

        //list of contacts in recycler view
        adapter = ContactListAdapter(this) { phone -> onItemClick(phone) }
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)

        //updateContact adapter if contacts changed
        viewModel.allContacts.observe(this, Observer { contacts ->
            if (contacts != null) {
                adapter.setContacts(contacts)
                if (contacts.isNotEmpty()) {
                    no_contacts_text_view.visibility = View.GONE
                } else {
                    no_contacts_text_view.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun onItemClick(phone: String) {
        OneContactActivity.start(this, phone)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add ->
                OneContactActivity.start(this)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(searchQuery: String?): Boolean {
                searchContacts(searchQuery)
                return false
            }

            override fun onQueryTextSubmit(searchQuery: String?): Boolean {
                searchContacts(searchQuery)
                return false
            }
        })
        return true
    }

    private fun searchContacts(query: String?) {
        viewModel.getBySearch(query).observe(this@ContactsActivity, Observer {
            adapter.setContacts(it)
        })
    }
}
