package com.manurung.covidapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.*
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
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException


class HomeFragment : Fragment() {
    //fungsi untuk aksi yang dilakukan ketika tidak ditemukan atau ditemukannya koneksi internet
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager.EXTRA_NO_CONNECTIVITY,
                false
            ) //cek koneksi internet, mengembalikan nilai true atau false
            if (notConnected) { //jika tidak ada koneksi internet
                disconnected()
                Toasty.warning(
                    mcontext!!,
                    "Aktifkan Koneksi Internet dan GPS",
                    Toast.LENGTH_SHORT
                ).show()
            } else { //jika ada koneksi internet
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //memastikan apakah handphone memerlukan permission check dengan menggunakan nilai SDK
                    if (checkPermission(permissions)) {
                        getLocation()
                        Handler().postDelayed({
                            fetchData()
                        }, 2000)
                    } else {
                        requestPermissions(permissions, PERMISSION_REQUEST)
                        requestPermissions(permissions, REQUEST_CALL)
                    }
                } else {
                    getLocation()
                    Handler().postDelayed({
                        fetchData()
                    }, 2000)

                }
                connected()
            }
        }
    }

    //    inisialisasi untuk location tracker permission yang akan dicheck
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CALL_PHONE
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

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false)

        textView = v!!.findViewById<View>(R.id.txtLocation) as TextView //Text View Lokasi
        tvTerinfeksi =
            v!!.findViewById<View>(R.id.textViewTerinfeksi) as TextView //Text View Terinfeksi
        tvSembuh = v!!.findViewById<View>(R.id.textViewSembuh) as TextView //Text View Sembuh
        tvMeninggal =
            v!!.findViewById<View>(R.id.textViewMeninggal) as TextView //Text View Meninggal
        tvLogout = v!!.findViewById<View>(R.id.buttonLogout) as RelativeLayout // Text View Logout
        tvEmailName = v!!.findViewById<View>(R.id.textViewEmailName) as TextView // Text View Logout
        viewGejala =
            v!!.findViewById<View>(R.id.recyclerViewGejala) as RecyclerView // Text View Logout
        viewPencegahan =
            v!!.findViewById<View>(R.id.recyclerViewPencegahan) as RecyclerView // Text View Logout
        viewRefreshLayout = v!!.findViewById<View>(R.id.refreshLayoutHome) as SwipeRefreshLayout

        contentMain = v!!.findViewById(R.id.linearLayoutHome) as LinearLayout
        contentBG = v!!.findViewById(R.id.viewBgHome) as View
        noConnection = v!!.findViewById(R.id.imageViewHome) as ImageView
        bgNoConnetion = v!!.findViewById(R.id.textViewHome) as TextView


//        Toast.makeText(activity, tvLogout!!.text, Toast.LENGTH_SHORT).show()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser

        tvLogout.setOnClickListener(View.OnClickListener {
            firebaseAuth!!.signOut()

            val intent = Intent(mcontext, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            mcontext!!.startActivity(intent)
        })

        mHandler = Handler()

        //daftar gejala yang akan ditampilkan
        viewGejala.layoutManager = LinearLayoutManager(mcontext, RecyclerView.HORIZONTAL, false)
        val gejalaList = ArrayList<GejalaModel>()
        gejalaList.add(
            GejalaModel(
                R.drawable.batuk,
                "Batuk Kering",
                "Batuk kering adalah batuk yang tidak menghasilkan lendir atau dahak."
            )
        )
        gejalaList.add(
            GejalaModel(
                R.drawable.demam,
                "Demam",
                "Demam adalah suatu keadaan saat suhu badan melebihi 37 °C yang disebabkan oleh penyakit atau peradangan."
            )
        )
        gejalaList.add(
            GejalaModel(
                R.drawable.sakit_kepala,
                "Sakit Kepala",
                "Sakit kepala adalah kondisi ketika timbul rasa sakit dan tidak nyaman di kepala, kulit kepala, atau leher."
            )
        )
        gejalaList.add(
            GejalaModel(
                R.drawable.sakit_tenggorokan,
                "Sakit Tenggorokan",
                "Sakit tenggorokan adalah rasa sakit, gatal, atau iritasi pada tenggorokan yang terasa semakin sakit saat menelan."
            )
        )
        gejalaList.add(
            GejalaModel(
                R.drawable.sesak_napas,
                "Sesak Napas",
                "Sesak napas adalah kondisi yang menyulitkan Anda bernapas karena kurangnya pasokan udara yang masuk ke paru-paru."
            )
        )

//        mengirim daftar gejala ke adapter agar data-data dapat di integrasikan dengan layout pada activity_main.xml
        val gejalaAdapter = GejalaAdapter(gejalaList)
        viewGejala.adapter = gejalaAdapter

//        daftar pencegahan yang akan ditampilkan
        viewPencegahan.layoutManager = LinearLayoutManager(mcontext, RecyclerView.HORIZONTAL, false)
        val pencegahanList = ArrayList<PencegahanModel>()
        pencegahanList.add(PencegahanModel(R.drawable.cuci_tangan, "Cuci\nTangan"))
        pencegahanList.add(PencegahanModel(R.drawable.gunakan_sabun, "Gunakan\nSabun"))
        pencegahanList.add(PencegahanModel(R.drawable.jangan_berkerumun, "Jangan\nBerkerumun"))
        pencegahanList.add(PencegahanModel(R.drawable.gunakan_disinfektan, "Gunakan\nDisinfektan"))
        pencegahanList.add(PencegahanModel(R.drawable.jangan_bersalaman, "Jangan\nBersalaman"))
        pencegahanList.add(PencegahanModel(R.drawable.gunakan_masker, "Gunakan\nMasker"))

//        mengirim daftar pencegahan ke adapter agar data-data dapat di integrasikan dengan layout pada activity_main.xml
        val pencegahanAdapter = PencegahanAdapter(pencegahanList)
        viewPencegahan.adapter = pencegahanAdapter

        mRunnable = Runnable {
            //ketika refresh dijalankan
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //memastikan apakah handphone memerlukan permission check dengan menggunakan nilai SDK
                if (checkPermission(permissions)) {
                    getLocation()
                    Handler().postDelayed({
                        fetchData()
                    }, 2000)
                    if (activity != null)
                        (activity as MainActivity).setLocationName(locationName)
                } else {
                    requestPermissions(permissions, PERMISSION_REQUEST)
                    requestPermissions(permissions, REQUEST_CALL)
                }

                mcontext!!.registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            } else {
                getLocation()
                Handler().postDelayed({
                    fetchData()
                }, 2000)
                if (activity != null)
                    (activity as MainActivity).setLocationName(locationName)

                mcontext!!.registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            }

            viewRefreshLayout.isRefreshing = false //menghilangkan icon refresh
        }

        mHandler.postDelayed(
            mRunnable,
            (2000).toLong() // Delay 3 detik
        )

//        listener ketika refresh event dilakukan
        viewRefreshLayout.setOnRefreshListener {
            // perintah akan dijalankan dengan waktu yang ditentukan
            mHandler.postDelayed(
                mRunnable,
                (2000).toLong() // Delay 3 detik
            )
        }

        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser

        mcontext!!.registerReceiver(
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onStart() {
        super.onStart()

        mcontext!!.registerReceiver(
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

    }

    //
    private fun disconnected() { //aksi yang dilakukan ketika tidak terdapat koneksi internet
        contentMain.visibility = View.GONE //all card view and recycler view not showing
        contentBG.visibility = View.GONE //blue square not showing
        noConnection.visibility = View.VISIBLE //no conection image showing
        bgNoConnetion.visibility = View.GONE //no conection image showing
    }

    private fun connected() { //aksi yang dilakukan ketika terdapat koneksi internet
        contentMain.visibility = View.VISIBLE //all card view and recycler view showing
        contentBG.visibility = View.VISIBLE //blue square showing
        noConnection.visibility = View.GONE //no conection image not showing
        bgNoConnetion.visibility = View.VISIBLE //no conection image showing
    }

    //
    private fun fetchData() { //mendaptkan data covid-19 real time
        val url = "https://api.kawalcorona.com/indonesia/provinsi"
//        simpleArcLoader.start()

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String?> { response ->
//                Toast.makeText(mcontext, "berhasil", Toast.LENGTH_SHORT).show()
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val data = jsonObject.getJSONObject("attributes")

                        val provinsi = data.getString("Provinsi")
                        if (textView.text.contains(provinsi)) {
                            tvTerinfeksi!!.text = data.getString("Kasus_Posi")
                            tvSembuh!!.text = data.getString("Kasus_Semb")
                            tvMeninggal!!.text = data.getString("Kasus_Meni")
                            break
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener //error ketika koneksi internet dan gps tidak di aktifkan
            {
//                Toast.makeText(mcontext, "tidak dapat terkoneksi dengan url", Toast.LENGTH_SHORT).show()
            })

            requestQueue = Volley.newRequestQueue(mcontext);
//        else
//            Toasty.warning(mcontext!!, "TEST", Toast.LENGTH_SHORT).show()

        requestQueue.add(stringRequest)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mcontext!!.registerReceiver(
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
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
                            locationGps = location
                            val addresses: List<Address>

                            val geocoder: Geocoder = Geocoder(mcontext)
                            addresses = geocoder.getFromLocation(
                                locationGps!!.latitude,
                                locationGps!!.longitude,
                                1
                            )

                            val alamatLengkap: String = addresses[0].getAddressLine(0)
                            val Kecamatan: String = addresses[0].locality
                            val Kabupaten: String = addresses[0].subAdminArea
                            var Provinsi: String = addresses[0].adminArea
                            val Negara: String = addresses[0].countryName

//                            mengubah nama provinsi yang didapat pada location track agar sama dengan nama provinsi pada data Covid-19 real time API
                            if (true) {
                                if (Provinsi.equals("Daerah Khusus Ibukota Jakarta")) {
                                    Provinsi = "DKI Jakarta"
                                } else if (Provinsi.equals("Nanggro Aceh Darussalam")) {
                                    Provinsi = "Aceh"
                                } else if (Provinsi.equals("North Sumatra")) {
                                    Provinsi = "Sumatera Utara"
                                } else if (Provinsi.equals("West Sumatra")) {
                                    Provinsi = "Sumatera Barat"
                                } else if (Provinsi.equals("Riau Islands")) {
                                    Provinsi = "Kepulauan Riau"
                                } else if (Provinsi.equals("South Sumatra")) {
                                    Provinsi = "Sumatera Selatan"
                                } else if (Provinsi.equals("Bangka Belitung")) {
                                    Provinsi = "Kepulauan Bangka Belitung"
                                } else if (Provinsi.equals("Bangka Belitung Islands")) {
                                    Provinsi = "Kepulauan Bangka Belitung"
                                } else if (Provinsi.equals("West Java")) {
                                    Provinsi = "Jawa Barat"
                                } else if (Provinsi.equals("Central Java")) {
                                    Provinsi = "Jawa Tengah"
                                } else if (Provinsi.equals("Special Region of Yogyakarta")) {
                                    Provinsi = "Daerah Istimewa Yogyakarta"
                                } else if (Provinsi.equals("Yogyakarta")) {
                                    Provinsi = "Daerah Istimewa Yogyakarta"
                                } else if (Provinsi.equals("East Java")) {
                                    Provinsi = "Jawa Barat"
                                } else if (Provinsi.equals("West Nusa Tenggara")) {
                                    Provinsi = "Nusa Tenggara Barat"
                                } else if (Provinsi.equals("West Kalimantan")) {
                                    Provinsi = "Kalimantan Barat"
                                } else if (Provinsi.equals("Central Kalimantan")) {
                                    Provinsi = "Kalimantan Tengah"
                                } else if (Provinsi.equals("South Kalimantan")) {
                                    Provinsi = "Kalimantan Selatan"
                                } else if (Provinsi.equals("East Kalimantan")) {
                                    Provinsi = "Kalimantan Timur"
                                } else if (Provinsi.equals("North Kalimantan")) {
                                    Provinsi = "Kalimantan Utara"
                                } else if (Provinsi.equals("North Sulawesi")) {
                                    Provinsi = "Sulawesi Utara"
                                } else if (Provinsi.equals("West Sulawesi")) {
                                    Provinsi = "Sulawesi Barat"
                                } else if (Provinsi.equals("Central Sulawesi")) {
                                    Provinsi = "Sulawesi Tengah"
                                } else if (Provinsi.equals("South East Sulawesi")) {
                                    Provinsi = "Sulawesi Tenggara"
                                } else if (Provinsi.equals("South Sulawesi")) {
                                    Provinsi = "Sulawesi Selatan"
                                } else if (Provinsi.equals("North Maluku")) {
                                    Provinsi = "Maluku Utara"
                                } else if (Provinsi.equals("West Papua")) {
                                    Provinsi = "Papua Barat"
                                }
                            }

                            textView.text = Provinsi
                            locationName = Provinsi

                            if(activity != null){
                                (activity as MainActivity).setLocationName(locationName)
                                (activity as MainActivity).setLatLang(locationGps!!.latitude, locationGps!!.longitude)
                            }

                            Log.d(
                                "CodeAndroidLocation",
                                " GPS Latitude : " + locationGps!!.latitude
                            )
                            Log.d(
                                "CodeAndroidLocation",
                                " GPS Longitude : " + locationGps!!.longitude
                            )

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
        private const val REQUEST_CALL = 1

        //    inisialisasi untuk value change pada activity main
        private lateinit var textView: TextView //text view lokasi
        private var tvTerinfeksi: TextView? = null //text view data orang terinfeksi
        private var tvSembuh: TextView? = null //text view data orang sembuh
        private var tvMeninggal: TextView? = null //text view data orang meninggal
        private lateinit var tvLogout: RelativeLayout //button logout
        private var tvEmailName: TextView? = null //text view data email name

        var v: View? = null

        //    inisialisasi untuk refresh event
        private lateinit var mRunnable: Runnable
        private lateinit var mHandler: Handler

        //    inisialisasi untuk session account
        private var firebaseAuth: FirebaseAuth? = null
        private var firebaseUser: FirebaseUser? = null

        //    inisialisasi untuk location tracker
        lateinit var locationManager: LocationManager
        private var hasGps = false
        private var locationGps: Location? = null

        private lateinit var viewGejala: RecyclerView
        private lateinit var viewPencegahan: RecyclerView
        private lateinit var viewRefreshLayout: SwipeRefreshLayout
        private lateinit var contentMain: LinearLayout
        private lateinit var contentBG: View
        private lateinit var noConnection: ImageView
        private lateinit var bgNoConnetion: TextView

        lateinit var requestQueue: RequestQueue

        var mcontext: Context? = null

        var locationName: String? = null
    }
}
