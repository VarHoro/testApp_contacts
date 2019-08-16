package com.example.contacts.presentation.listofcontacts

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.R
import com.example.contacts.domain.ContactModel
import com.example.contacts.presentation.listofcontacts.AdapterModel.Companion.TYPE_CONTACT
import com.example.contacts.presentation.listofcontacts.AdapterModel.Companion.TYPE_LETTER
import com.example.contacts.presentation.onecontact.OneContactActivity

class ContactListAdapter internal constructor(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var contacts = ArrayList<AdapterModel>()

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contactItemView: TextView = itemView.findViewById(R.id.contact_name_text_view)
        private val contactPhoneView: TextView = itemView.findViewById(R.id.phone_text_view)

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, OneContactActivity::class.java)
                intent.putExtra(EXTRA_PHONE, contactPhoneView.text.toString())
                context.startActivity(intent)
            }
        }

        fun bind(model: AdapterModel) {
            contactItemView.text = model.name
            contactPhoneView.text = model.phone
        }
    }

    inner class AlphabetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val charTextView: TextView = itemView.findViewById(R.id.header_text_view)

        fun bind(model: AdapterModel) {
            charTextView.text = model.letter.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LETTER -> {
                val v = inflater.inflate(R.layout.header_item, parent, false)
                AlphabetViewHolder(v)
            }
            else -> {
                val v = inflater.inflate(R.layout.contact_item, parent, false)
                ContactViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = contacts[position]
        if (current.isContact) {
            holder as ContactViewHolder
            holder.bind(current)
        } else {
            holder as AlphabetViewHolder
            holder.bind(current)
        }
    }

    private fun getFirstLetter(contact: ContactModel): Char {
        return if (contact.secondName != null && contact.secondName.isNotEmpty()) {
            contact.secondName[0]
        } else {
            '#'
        }
    }

    internal fun setContacts(contact: List<ContactModel>) {
        contacts.clear()
        if (contact.isNotEmpty()) {
            var currentLetter = getFirstLetter(contact[0])

            if (contact.size == 1) { //if there is only one record in contacts
                contacts.add(
                    AdapterModel(
                        isContact = false,
                        name = null,
                        phone = null,
                        letter = currentLetter
                    )
                )
                contacts.add(
                    AdapterModel(
                        isContact = true,
                        name = contact[0].firstName.plus(' ').plus(contact[0].secondName),
                        phone = contact[0].phone,
                        letter = null
                    )
                )
            } else { //for more contacts

                for (i in 0 until contact.size - 1) {
                    if (i == 0) { //first goes letter
                        contacts.add(
                            AdapterModel(
                                isContact = false,
                                name = null,
                                phone = null,
                                letter = currentLetter
                            )
                        )
                    }
                    if (currentLetter != getFirstLetter(contact[i + 1])) { //if next record starts with different letter add header
                        contacts.add(
                            AdapterModel(
                                isContact = true,
                                name = contact[i].firstName.plus(' ').plus(contact[i].secondName),
                                phone = contact[i].phone,
                                letter = null
                            )
                        )
                        currentLetter = getFirstLetter(contact[i + 1])
                        contacts.add(
                            AdapterModel(
                                isContact = false,
                                name = null,
                                phone = null,
                                letter = currentLetter
                            )
                        )
                    } else { //if next letter is the same - add next record
                        contacts.add(
                            AdapterModel(
                                isContact = true,
                                name = contact[i].firstName.plus(' ').plus(contact[i].secondName),
                                phone = contact[i].phone,
                                letter = null
                            )
                        )
                    }
                }
                //adding the last contact
                contacts.add(
                    AdapterModel(
                        isContact = true,
                        name = contact[contact.size - 1].firstName.plus(' ').plus(contact[contact.size - 1].secondName),
                        phone = contact[contact.size - 1].phone,
                        letter = null
                    )
                )
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = contacts.size

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        when (contacts[position].getType()) {
            TYPE_LETTER -> viewType = TYPE_LETTER
            TYPE_CONTACT -> viewType = TYPE_CONTACT
        }
        return viewType
    }

    companion object {
        const val EXTRA_PHONE = "com.example.contacts.presentation.listofcontacts.FNAME"
    }
}