package com.example.contacts.presentation.onecontact

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.contacts.R
import com.example.contacts.databinding.ActivityOnecontactBinding
import com.example.contacts.presentation.listofcontacts.ContactListAdapter.Companion.EXTRA_NAME
import com.example.contacts.presentation.listofcontacts.ContactsViewModel
import kotlinx.android.synthetic.main.activity_onecontact.*

class OneContactActivity : AppCompatActivity() {

    private lateinit var viewModel: OneContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(OneContactViewModel::class.java)

        val binding = DataBindingUtil.setContentView<ActivityOnecontactBinding>(this, R.layout.activity_onecontact)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val bundle = intent.extras
        if (bundle != null){
            val name = bundle.getString(EXTRA_NAME).toString()
            if (name.isNotEmpty()){
                viewModel.getByName(name)
                Toast.makeText(this, "Hello from $name", Toast.LENGTH_SHORT).show()
            }
        }

        //toolbar
        one_contact_toolbar.title = getString(R.string.edit)
        one_contact_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        one_contact_toolbar.setNavigationOnClickListener { finish() }
        setSupportActionBar(one_contact_toolbar)

        //select new ringtone
        val listRingtones = resources.getStringArray(R.array.ringtones)
        var checkedItem = -1
        var checkedItemText = resources.getString(R.string.ringtone_name_text_view)

        ringtone_select.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle(R.string.select_ringtone)
                .setSingleChoiceItems(listRingtones, checkedItem) { _, i ->
                    checkedItemText = listRingtones[i]
                    checkedItem = i
                }.setNeutralButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton(R.string.select) { dialogInterface, _ ->
                    ringtone_text_view.text = checkedItemText
                    dialogInterface.dismiss()
                }
            builder.show()
        }

        //get note text
        note_select.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle(R.string.add_note)
                .setView(LayoutInflater.from(this).inflate(R.layout.note_dialog, null))
            val noteText = findViewById<EditText>(R.id.note_edit_text)
            builder.setNegativeButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton(R.string.select) { dialogInterface, _ ->
                    if (noteText != null) {
                        note_text_view.text = noteText.text
                    }
                    dialogInterface.dismiss()
                }
            builder.show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_one_contact, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                val replyIntent = Intent()
                if (TextUtils.isEmpty(first_name_edit_text.text)) {
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                } else {
                    val name = first_name_edit_text.text.toString().plus(" ").plus(second_name_edit_view.text.toString())
                    val phone = phone_edit_text.text.toString()
                    val ringtone = ringtone_text_view.text.toString()
                    val note = note_text_view.text.toString()
                    replyIntent.putExtra(EXTRA_REPLY, name)
                        .putExtra(EXTRA_PHONE, phone)
                        .putExtra(EXTRA_RING, ringtone)
                        .putExtra(EXTRA_NOTE, note)
                    setResult(Activity.RESULT_OK, replyIntent)
                }
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_REPLY = "com.example.contacts.presentation.onecontact.REPLY"
        const val EXTRA_PHONE = "com.example.contacts.presentation.onecontact.PHONE"
        const val EXTRA_RING = "com.example.contacts.presentation.onecontact.RING"
        const val EXTRA_NOTE = "com.example.contacts.presentation.onecontact.NOTE"
    }
}

