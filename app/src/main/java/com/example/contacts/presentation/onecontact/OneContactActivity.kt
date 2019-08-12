package com.example.contacts.presentation.onecontact

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.example.contacts.R

class OneContactActivity : AppCompatActivity() {

    private lateinit var editFirstNameView: EditText
    private lateinit var editPhoneView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_contact)

        val toolbar = findViewById<Toolbar>(R.id.one_contact_toolbar)
        toolbar.title = getString(R.string.edit)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener{finish()}
        setSupportActionBar(toolbar)

        editFirstNameView = findViewById(R.id.first_name_edit_text)
        editPhoneView = findViewById(R.id.phone_edit_text)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_one_contact, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_save -> {
                val replyIntent = Intent()
                if (TextUtils.isEmpty(editFirstNameView.text)) {
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                } else {
                    val name = editFirstNameView.text.toString()
                    val phone = editPhoneView.text.toString()
                    replyIntent.putExtra(EXTRA_REPLY, name)
                    replyIntent.putExtra(EXTRA_PHONE, phone)
                    setResult(Activity.RESULT_OK, replyIntent)
                }
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object{
        const val EXTRA_REPLY = "com.example.contacts.presentation.onecontact.REPLY"
        const val EXTRA_PHONE = "com.example.contacts.presentation.onecontact.PHONE"
        const val EXTRA_SECOND = "com.example.contacts.presentation.onecontact.SECOND"
        const val EXTRA_NOTE = "com.example.contacts.presentation.onecontact.NOTE"
    }
}

