package com.manurung.covidapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import es.dmoral.toasty.Toasty
import java.io.IOException

class CallFragment : Fragment() {

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager.EXTRA_NO_CONNECTIVITY,
                false
            ) //cek koneksi internet, mengembalikan nilai true atau false
            if (notConnected) { //jika tidak ada koneksi internet
                disconnected()
            } else { //jika ada koneksi internet
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //memastikan apakah handphone memerlukan permission check dengan menggunakan nilai SDK
                    if (checkPermission(permissions)) {
                        getLocation()
                    } else {
                        requestPermissions(permissions, PERMISSION_REQUEST)
                    }
                } else {
                    getLocation()

                }
                connected()
            }
        }
    }

    //    inisialisasi untuk location tracker permission yang akan dicheck
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mcontext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_call, container, false)

        viewContact =
            v!!.findViewById<View>(R.id.recyclerViewContact) as RecyclerView // Text View Logout
        tvLogout = v!!.findViewById<View>(R.id.buttonLogout) as RelativeLayout // Text View Logout
        tvTeleponRumahSakit =
            v!!.findViewById<View>(R.id.textViewTeleponRumahSakit) as TextView // Text View Logout

        viewRefreshLayout = v!!.findViewById<View>(R.id.refreshLayoutCall) as SwipeRefreshLayout

        contentMain = v!!.findViewById<View>(R.id.linearLayoutCall) as LinearLayout
        contentBG = v!!.findViewById<View>(R.id.viewBGCall) as View
        noConnection = v!!.findViewById<View>(R.id.imageViewCall) as ImageView

        mHandler = Handler()

        //daftar gejala yang akan ditampilkan
        viewContact.layoutManager = LinearLayoutManager(mcontext, RecyclerView.VERTICAL, false)
        val contactList = ArrayList<ContactModel>()

        mRunnable = Runnable {
            if (activity != null) {
                locationName = (activity as MainActivity).getLocationName()

//                tvTeleponRumahSakit.text = locationName
                if (locationName != null) {
                    if (locationName!!.contains("Lampung")) {
                        contactList.clear()
                        contactList.add(
                            ContactModel(
                                "RSUD Dr H Abdul Moeloek\nBandar Lampung",
                                "(0721) 703312",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-5.402875, 105.258625)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSUD Dr H Bob Bazar SKM\nKabupaten Lampung Selatan",
                                "(0727) 322159",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-5.727398, 105.596486)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSUD Jend Ahmad Yani\nKota Metro",
                                "(0725) 41820",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-5.115439, 105.310137)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSD May Jend HM Ryacudu\nKotabumi",
                                "(0724) 22095",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )

                        //        mengirim daftar pencegahan ke adapter agar data-data dapat di integrasikan dengan layout pada fragment_call.xml
                        val contactAdapter = ContactAdapter(contactList)
                        viewContact.adapter = contactAdapter
                    } else if (locationName!!.contains("DKI Jakarta")) {
                        contactList.clear()
                        contactList.add(
                            ContactModel(
                                "RSPI Sulianti Saroso, Sunter\nJakarta Utara",
                                "(021) 6506559",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSPAD Gatot Soebroto\nJakarta Pusat.",
                                "(021) 3440693",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSUD Tarakan, Gambir\nJakarta Pusat",
                                "(021) 3503003",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSAL Mintoharjo\nJakarta Pusat",
                                "(021) 5703081",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSUD Cengkareng, Cengkareng\nJakarta Barat",
                                "(021) 54372874",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RS Pelni, Slipi\nJakarta Barat",
                                "(021) 5306901",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSUD Pasar Minggu\nJakarta Selatan",
                                "(021) 29059999",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSUP Fatmawati\nJakarta Selatan",
                                "(021) 7501524",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSU Bhayangkara Said Sukanto\nJakarta Timur",
                                "(021) 8093288",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSKD Duren Sawit\nJakarta Timur",
                                "(021) 86552365",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )
                        contactList.add(
                            ContactModel(
                                "RSUP Persahabatan, Jakarta Timur",
                                "(021) 4891708",
                                R.drawable.ic_local_hospital_black_24dp,
                                LatLng(-4.832420, 104.891416)
                            )
                        )

                        //        mengirim daftar pencegahan ke adapter agar data-data dapat di integrasikan dengan layout pada fragment_call.xml
                        val contactAdapter = ContactAdapter(contactList)
                        viewContact.adapter = contactAdapter
                    }
                }
            }

            //ketika refresh dijalankan
            viewRefreshLayout.isRefreshing = false //menghilangkan icon refresh
        }

        mHandler.postDelayed(
            mRunnable,
            (3000).toLong() // Delay 3 detik
        )
//        contactList.add(
//            ContactModel(
//                "RSUD Dr Zainoel Abidin\nBanda Aceh",
//                "(0651) 34562",
//                R.drawable.ic_local_hospital_black_24dp
//            )
//        )
//        contactList.add(
//            ContactModel(
//                "RSUD Cut Meutia, Lhokseumawe\nKabupaten Aceh Utara",
//                "(0645) 46334",
//                R.drawable.ic_local_hospital_black_24dp
//            )
//        )

        viewRefreshLayout.setOnRefreshListener {
            // perintah akan dijalankan dengan waktu yang ditentukan
            mHandler.postDelayed(
                mRunnable,
                (3000).toLong() // Delay 3 detik
            )
        }

        tvLogout.setOnClickListener(View.OnClickListener {
            firebaseAuth!!.signOut()

            val intent = Intent(mcontext, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            mcontext!!.startActivity(intent)
        })

        if (activity != null) {
            if ((activity as MainActivity).getLocationName() != locationName) {
                mHandler.postDelayed(
                    mRunnable,
                    (3000).toLong() // Delay 3 detik
                )
            }
        }

        return v
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = mcontext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (hasGps) {
            Log.d("CodeAndroidLocation", "hasGps")
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (location != null) {
                        try {
                            mHandler.postDelayed(
                                mRunnable,
                                (3000).toLong() // Delay 3 detik
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                }

                override fun onProviderEnabled(provider: String?) {

                }

                override fun onProviderDisabled(provider: String?) {

                }

            })

            val localGpsLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation != null)
                locationGps = localGpsLocation
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mcontext!!.registerReceiver(
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    //
    private fun disconnected() { //aksi yang dilakukan ketika tidak terdapat koneksi internet
        contentMain.visibility = View.INVISIBLE //all card view and recycler view not showing
        contentBG.visibility = View.INVISIBLE //blue square not showing
        noConnection.visibility = View.VISIBLE //no conection image showing
    }

    private fun connected() { //aksi yang dilakukan ketika terdapat koneksi internet
        contentMain.visibility = View.VISIBLE //all card view and recycler view showing
        contentBG.visibility = View.VISIBLE //blue square showing
        noConnection.visibility = View.INVISIBLE //no conection image not showing
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (mcontext!!.checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain =
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                            permissions[i]
                        )
                    if (requestAgain) {
                        Toasty.error(mcontext!!, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toasty.error(
                            mcontext!!,
                            "Go to settings and enable the permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (allSuccess)
                getLocation()

        }
    }

    companion object {
        private const val PERMISSION_REQUEST = 10

        var v: View? = null

        private lateinit var viewContact: RecyclerView
        private lateinit var tvLogout: RelativeLayout //button logout


        private lateinit var viewRefreshLayout: SwipeRefreshLayout
        private lateinit var contentMain: LinearLayout
        private lateinit var contentBG: View
        private lateinit var noConnection: ImageView
        private lateinit var tvTeleponRumahSakit: TextView

        var mcontext: Context? = null

        //    inisialisasi untuk refresh event
        private lateinit var mRunnable: Runnable
        private lateinit var mHandler: Handler

        //    inisialisasi untuk session account
        private var firebaseAuth: FirebaseAuth? = null
        private var firebaseUser: FirebaseUser? = null

        var locationName: String? = null
        var templocationName: String? = null

        //    inisialisasi untuk location tracker
        lateinit var locationManager: LocationManager
        private var hasGps = false
        private var locationGps: Location? = null
    }
}