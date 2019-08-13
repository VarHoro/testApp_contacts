package com.example.contacts.presentation.onecontact

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.contacts.R
import com.example.contacts.databinding.ActivityOnecontactBinding
import com.example.contacts.presentation.listofcontacts.ContactListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_onecontact.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.contact_item.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class OneContactActivity : AppCompatActivity() {

    private val viewModel: OneContactViewModel by viewModel { parametersOf(this.application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityOnecontactBinding>(this, R.layout.activity_onecontact)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val bundle = intent.extras
        if (bundle != null) {
            val phone = bundle.getString(ContactListAdapter.EXTRA_PHONE).toString()
            if (phone.isNotEmpty()) {
                viewModel.getByPhone(phone)
            }
        }

        image_view.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val takePhoto = view.findViewById<TextView>(R.id.take_photo)
            val choosePhoto = view.findViewById<TextView>(R.id.choose_photo)
            val dialog = BottomSheetDialog(this)
            takePhoto.setOnClickListener {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        dialog.dismiss()
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
            choosePhoto.setOnClickListener {
                val choosePhotoIntent = Intent(Intent.ACTION_PICK)
                choosePhotoIntent.type = "image/*"
                dialog.dismiss()
                startActivityForResult(choosePhotoIntent, REQUEST_IMAGE_CHOOSE)
            }
            dialog.setContentView(view)
            dialog.show()
        }

        //toolbar
        one_contact_toolbar.title = getString(R.string.edit)
        one_contact_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        one_contact_toolbar.setNavigationOnClickListener { finish() }
        setSupportActionBar(one_contact_toolbar)

        //select new ringtoneText
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

        //get noteText text
        note_select.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.note_dialog, null)
            val note = view.findViewById<EditText>(R.id.note_edit_text)
            builder.setTitle(R.string.add_note).setView(view)
            builder.setNegativeButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton(R.string.select) { dialogInterface, _ ->
                    if (note != null) {
                        note_text_view.text = note.text
                    }
                    dialogInterface.dismiss()
                }
            builder.show()
        }

        //delete contact
        delete_button.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.delete)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.delete) { dialogInterface, _ ->
                    viewModel.delete()
                    dialogInterface.dismiss()
                    finish()
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    Glide.with(this)
                        .load(data?.extras?.get("data"))
                        .into(image_view)
                }
                REQUEST_IMAGE_CHOOSE -> {
                    Glide.with(this)
                        .load(data?.data)
                        .into(image_view)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_one_contact, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                if (phone_edit_text.text.isEmpty()) {
                    Toast.makeText(this, R.string.phone_is_empty, Toast.LENGTH_LONG).show()
                } else {
                    if (viewModel.isExistingContact.get()) {
                        viewModel.firstNameText.set(first_name_edit_text.text.toString())
                        viewModel.secondNameText.set(second_name_edit_view.text.toString())
                        viewModel.ringtoneText.set(ringtone_text_view.text.toString())
                        viewModel.phoneText.set(phone_edit_text.text.toString())
                        viewModel.noteText.set(note_text_view.text.toString())
                        viewModel.update()
                        finish()
                    } else {
                        val replyIntent = Intent()
                        if (TextUtils.isEmpty(first_name_edit_text.text)) {
                            setResult(Activity.RESULT_CANCELED, replyIntent)
                        } else {
                            val fname = first_name_edit_text.text.toString()
                            val sname = second_name_edit_view.text.toString()
                            val phone = phone_edit_text.text.toString()
                            val ringtone = ringtone_text_view.text.toString()
                            val note = note_text_view.text.toString()
                            replyIntent.putExtra(EXTRA_FNAME, fname)
                                .putExtra(EXTRA_SNAME, sname)
                                .putExtra(EXTRA_PHONE, phone)
                                .putExtra(EXTRA_RING, ringtone)
                                .putExtra(EXTRA_NOTE, note)
                            setResult(Activity.RESULT_OK, replyIntent)
                        }
                        finish()
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_FNAME = "com.example.contacts.presentation.onecontact.FNAME"
        const val EXTRA_SNAME = "com.example.contacts.presentation.onecontact.SNAME"
        const val EXTRA_PHONE = "com.example.contacts.presentation.onecontact.PHONE"
        const val EXTRA_RING = "com.example.contacts.presentation.onecontact.RING"
        const val EXTRA_NOTE = "com.example.contacts.presentation.onecontact.NOTE"
        const val EXTRA_IMG = "com.example.contacts.presentation.onecontact.IMG"
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_CHOOSE = 2
    }
}

