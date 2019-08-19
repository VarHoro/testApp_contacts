package com.example.contacts.presentation.onecontact

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_onecontact.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class OneContactActivity : AppCompatActivity() {

    private val viewModel: OneContactViewModel by viewModel()

    private var currentPhotoPath: String = ""

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityOnecontactBinding>(this, R.layout.activity_onecontact)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val bundle = intent.extras
        if (bundle != null) {
            checkExtras(bundle)
        }

        image_view.setOnClickListener { showBottomSheet() }

        //toolbar
        one_contact_toolbar.title = getString(R.string.edit)
        one_contact_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        one_contact_toolbar.setNavigationOnClickListener { finish() }
        setSupportActionBar(one_contact_toolbar)

        //select new ringtoneText
        ringtone_select.setOnClickListener { showDialogForRingtone() }

        //get noteText text
        note_select.setOnClickListener { showDialogForNote() }

        //deleteContact contact
        delete_button.setOnClickListener { showDialogForDelete() }
    }

    private fun showBottomSheet() {
        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
        val dialog = BottomSheetDialog(this)

        //for taking photos
        val takePhoto = view.findViewById<TextView>(R.id.take_photo)
        takePhoto.setOnClickListener {
            dialog.dismiss()
            dispatchTakePictureIntent()
        }

        //for choosing photos
        val choosePhoto = view.findViewById<TextView>(R.id.choose_photo)
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

    private fun showDialogForRingtone() {
        val listRingtones = resources.getStringArray(R.array.ringtones)
        var checkedItem = -1
        var checkedItemText = resources.getString(R.string.ringtone_name_text_view)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.select_ringtone)
            .setSingleChoiceItems(listRingtones, checkedItem) { _, i ->
                checkedItemText = listRingtones[i]
                checkedItem = i
            }
            .setNeutralButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton(R.string.select) { dialogInterface, _ ->
                ringtone_text_view.text = checkedItemText
                dialogInterface.dismiss()
            }
        builder.show()

    }

    private fun showDialogForNote() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.note_dialog, null)
        val note = view.findViewById<EditText>(R.id.note_edit_text)
        note.setText(note_text_view.text)
        builder.setTitle(R.string.add_note)
            .setView(view)
            .setNegativeButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton(R.string.select) { dialogInterface, _ ->
                if (note != null) {
                    note_text_view.text = note.text
                }
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showDialogForDelete() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.delete)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.delete) { dialogInterface, _ ->
                viewModel.deleteContact()
                dialogInterface.dismiss()
                finish()
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun checkExtras(bundle: Bundle) {
        val phone = bundle.getString(EXTRA_PHONE).toString()
        if (phone.isNotEmpty()) { //if this contact exist
            val flag = viewModel.getByPhone(phone)
            flag.observe(this, androidx.lifecycle.Observer {
                //when image path is ready -> glide into image_view
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

    //creating file for camera
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile = try {
                    createFileImage()
                } catch (e: IOException) {
                    Log.e("OneContactActivity", "Error: ${e.message}")
                    null
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.contacts.fileprovider",
                        photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createFileImage(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
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
                    if (phone_edit_text.text.length < MIN_PHONE_LENGTH) { //phone number should be at least 3 symbols
                        phone_edit_text.text.clear()
                        phone_edit_text.hint = String.format(getString(R.string.too_small), MIN_PHONE_LENGTH)
                        phone_edit_text.setHintTextColor(getColor(R.color.colorWarning))
                    } else {
                        //updateContact info
                        if (viewModel.isExistingContact.get()) {
                            viewModel.imageText.set(currentPhotoPath)
                            viewModel.updateContact()
                            finish()
                        } else {
                            //send data for addContact
                            viewModel.imageText.set(currentPhotoPath)
                            viewModel.addContact()
                            finish()
                        }
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
        const val MIN_PHONE_LENGTH = 3

        private const val EXTRA_PHONE = "com.example.contacts.presentation.onecontact.FNAME"
        fun start(context: Context, phone: String) {
            val intent = Intent(context, OneContactActivity::class.java).apply {
                putExtra(EXTRA_PHONE, phone)
            }
            context.startActivity(intent)
        }

    }
}

