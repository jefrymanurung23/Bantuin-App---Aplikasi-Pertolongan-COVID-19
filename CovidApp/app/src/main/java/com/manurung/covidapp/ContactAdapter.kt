package com.manurung.covidapp

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

class ContactAdapter(var contactList: ArrayList<ContactModel>) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    class ViewHolder(inflater: LayoutInflater, viewGroup: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_contact, viewGroup, false)) {

        fun bind(contactModel: ContactModel) {
            val itemContact = itemView.findViewById<LinearLayout>(R.id.linearLayoutContactItem)
            val contactNama = itemView.findViewById<TextView>(R.id.textViewContactName)
            val contactTelepon = itemView.findViewById<TextView>(R.id.textViewContactNumber)
            val contactFoto = itemView.findViewById<ImageView>(R.id.imageContact)
            val buttonCall = itemView.findViewById<RelativeLayout>(R.id.buttonCall)

            val dialog = Dialog(itemContact.context)
            dialog.setContentView(R.layout.dialog_contact)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            contactNama.text = contactModel.contactNama
            contactTelepon.text = contactModel.contactTelepon
            contactFoto.setImageResource(contactModel.contactFoto)

            buttonCall.setOnClickListener(View.OnClickListener {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:" + contactModel.contactTelepon)
                if (ActivityCompat.checkSelfPermission(
                        itemContact.context,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@OnClickListener
                }
                itemContact.context.startActivity(intent)
            })

            itemContact.setOnClickListener(View.OnClickListener {
                val dialogContactNama = dialog.findViewById<TextView>(R.id.dialog_name)
                val dialogContactTelepon = dialog.findViewById<TextView>(R.id.dialog_phone)
                val dialogTelepon = dialog.findViewById<RelativeLayout>(R.id.dialog_btn_call)
                val dialogMaps = dialog.findViewById<RelativeLayout>(R.id.dialog_btn_maps)
                val viewPager = (itemContact.context as MainActivity).findViewById<ViewPager>(R.id.viewPagerContent)

                dialogContactNama.text = contactModel.contactNama
                dialogContactTelepon.text = contactModel.contactTelepon

                dialog.show()

                dialogTelepon.setOnClickListener(View.OnClickListener {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:" + contactModel.contactTelepon)
                    if (ActivityCompat.checkSelfPermission(
                            itemContact.context,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@OnClickListener
                    }
                    itemContact.context.startActivity(intent)
                })

                dialogMaps.setOnClickListener(View.OnClickListener {
//                    Toast.makeText(itemContact.context, "MAPS", Toast.LENGTH_SHORT).show()
                    viewPager.setCurrentItem(3, true)
                    (itemContact.context as MainActivity).setLatLong(contactModel.latLng)
                    dialog.dismiss()
                })

            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
        val contactModel = contactList[position]
        holder.bind(contactModel)
    }
}