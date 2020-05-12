package com.manurung.covidapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayout
import es.dmoral.toasty.Toasty


class MainActivity : AppCompatActivity() {

    private var locationName: String? = null
    private var teleponRS: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var latLng: LatLng? = null

    private var sensorManager: SensorManager? = null

    private var SHAKE_THRESHOLD = 600
    private var lastUpdate: Long = 0
    private var lastCall: Long = 5000
    private var last_x = -1.0f
    private var last_y = -1.0f
    private var last_z = -1.0f
    private var count = 0
    private var a = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val tabLayout: TabLayout = findViewById(R.id.tabLayoutNavigation)
        val viewPager: ViewPager = findViewById(R.id.viewPagerContent)
        val viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(HomeFragment(), "")
        viewPagerAdapter.addFragment(CallFragment(), "")
        viewPagerAdapter.addFragment(InfoFragment(), "")
        viewPagerAdapter.addFragment(MapsFragment(), "")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_home_black_24dp)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_call_blue_24dp)
        tabLayout.getTabAt(3)!!.setIcon(R.drawable.ic_map_blue_24dp)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.bold_coronavirus)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager!!.registerListener(
            sensorListener,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )

    }

    fun setLocationName(name: String?) {
        locationName = name
    }

    fun getLocationName(): String? {
        return locationName
    }

    fun setLatLang(latitude: Double?, longitude: Double?) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun getLat(): Double? {
        return this.latitude
    }

    fun getLong(): Double? {
        return this.longitude
    }

    fun setLatLong(latLng: LatLng?) {
        this.latLng = latLng
    }

    fun getLatLong(): LatLng? {
        return this.latLng
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val curTime = System.currentTimeMillis()
            // only allow one update every 100ms.
            if (curTime - lastUpdate > 1000) {
                val diffTime: Long = curTime - lastUpdate
                lastUpdate = curTime
                count = 0
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val speed = kotlin.math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000
                Log.d("sensor", "" + speed)

                if (speed > SHAKE_THRESHOLD) {
                    if (locationName != null) {
                        if (locationName.equals("Lampung")) {
                            teleponRS = "(0721) 703312"
                            call()
                        } else if (locationName.equals("DKI Jakarta")) {
                            teleponRS = "(021) 5703081"
                            call()
                        }
                    }

                }

                last_x = x
                last_y = y
                last_z = z
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    fun call() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$teleponRS")
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
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
            return
        }
        startActivity(intent)
        Toasty.success(applicationContext, "Memulai Panggilan", Toast.LENGTH_SHORT).show()
    }

}
