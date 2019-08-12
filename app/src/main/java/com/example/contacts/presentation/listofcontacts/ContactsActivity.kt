package com.example.contacts.presentation.listofcontacts

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.R
import com.example.contacts.data.Contact
import com.example.contacts.presentation.onecontact.OneContactActivity

class ContactsActivity : AppCompatActivity() {

    companion object {
        const val newContactActivityRequestCode = 1
    }

    private lateinit var viewModel : ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = ContactListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProviders.of(this).get(ContactsViewModel::class.java)
        viewModel.allContacts.observe(this, Observer{contacts ->
            contacts?.let { adapter.setContacts(it) }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newContactActivityRequestCode && resultCode == Activity.RESULT_OK){
            data?.let {
                val contact = Contact(
                    it.getStringExtra(OneContactActivity.EXTRA_REPLY),
                    it.getStringExtra(OneContactActivity.EXTRA_PHONE),
                    it.getStringExtra(OneContactActivity.EXTRA_RING),
                    it.getStringExtra(OneContactActivity.EXTRA_NOTE))
                viewModel.insert(contact)
            }
        } else {
            Toast.makeText(applicationContext, R.string.empty_not_saved, Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_add ->{
                val intent = Intent(this@ContactsActivity, OneContactActivity::class.java)
                startActivityForResult(intent, newContactActivityRequestCode)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
