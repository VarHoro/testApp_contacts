package com.example.contacts.presentation.onecontact

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.contacts.R
import com.example.contacts.databinding.ActivityOnecontactBinding
import com.example.contacts.presentation.listofcontacts.ContactListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_onecontact.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class OneContactActivity : AppCompatActivity() {

    private val viewModel: OneContactViewModel by viewModel { parametersOf(this.application) }

    var currentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityOnecontactBinding>(this, R.layout.activity_onecontact)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val bundle = intent.extras
        if (bundle != null) {
            val phone = bundle.getString(ContactListAdapter.EXTRA_PHONE).toString()
            if (phone.isNotEmpty()) {
                val flag = viewModel.getByPhone(phone)
                flag.observe(this, androidx.lifecycle.Observer {
                    if (it) {
                        currentPhotoPath = viewModel.imageText.get().toString()
                        if (currentPhotoPath.isNotEmpty()) {
                            Glide.with(this)
                                .load(currentPhotoPath)
                                .into(image_view)
                        }
                    }
                })
            }
        }

        image_view.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val takePhoto = view.findViewById<TextView>(R.id.take_photo)
            val choosePhoto = view.findViewById<TextView>(R.id.choose_photo)
            val dialog = BottomSheetDialog(this)
            takePhoto.setOnClickListener {
                dialog.dismiss()
                dispatchTakePictureIntent()
            }
            choosePhoto.setOnClickListener {
                val choosePhotoIntent = Intent(Intent.ACTION_PICK)
                choosePhotoIntent.type = "image/*"
                choosePhotoIntent.action = Intent.ACTION_GET_CONTENT
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
            if (!viewModel.noteText.get().isNullOrEmpty()) {
                note.setText(viewModel.noteText.get())
            }
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

    //creating file for camera
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val photoFile = createFileImage()
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "com.example.contacts.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: IOException) {
                Log.e("OneContactActivity", "Error: ${e.message}")
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createFileImage(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    Glide.with(this)
                        .load(currentPhotoPath)
                        .into(image_view)
                }
                REQUEST_IMAGE_CHOOSE -> {
                    currentPhotoPath = data?.data.toString()
                    Glide.with(this)
                        .load(currentPhotoPath)
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
                    //update info
                    if (viewModel.isExistingContact.get()) {
                        viewModel.imageText.set(currentPhotoPath)
                        viewModel.update()
                        finish()
                    } else {
                        //send data for insert
                        viewModel.imageText.set(currentPhotoPath)
                        viewModel.insert()
                        finish()
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_CHOOSE = 2
        const val REQUEST_PERMISSION = 123
    }
}

