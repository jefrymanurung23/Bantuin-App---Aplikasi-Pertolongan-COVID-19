package com.manurung.covidapp

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_maps.*

class MapsFragment : Fragment(), OnMapReadyCallback {
    override fun onAttach(context: Context) {
        super.onAttach(context)

        mcontext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_maps, container, false)

        mapView = v.findViewById(R.id.map)

        mapView.onCreate(null)
        mapView.onResume()
        mapView.getMapAsync(this)

        listPoints = ArrayList()

//        Toasty.info(mcontext, "Tekan lama bagian mana saja pada google maps untuk melakukan refresh", Toast.LENGTH_LONG).show()

        return v
    }

    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(activity)

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isMyLocationEnabled = true


        // Add a marker in Sydney and move the camera
        if ((activity as MainActivity).getLat() != null) {
            currentLocation = LatLng((activity as MainActivity).getLat()!!, (activity as MainActivity).getLong()!!)
            mMap.addMarker(MarkerOptions().position(currentLocation!!).title("Lokasi Anda Sekarang"))
            val zoom = CameraPosition.builder().target(currentLocation).zoom(15F).bearing(0F).tilt(45F).build()
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(zoom))
        } else {
            currentLocation = LatLng(-5.397242, 105.266641)
            mMap.addMarker(MarkerOptions().position(currentLocation!!).title("Lokasi Anda Sekarang"))
            val zoom = CameraPosition.builder().target(currentLocation).zoom(15F).bearing(0F).tilt(45F).build()
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(zoom))
        }

        mMap.setOnMapLongClickListener(GoogleMap.OnMapLongClickListener {
            if ((activity as MainActivity).getLat() != null) {
                mMap.clear()
                currentLocation = LatLng((activity as MainActivity).getLat()!!, (activity as MainActivity).getLong()!!)
                mMap.addMarker(MarkerOptions().position(currentLocation!!).title("Lokasi Anda Sekarang"))
                val zoom = CameraPosition.builder().target(currentLocation).zoom(15F).bearing(0F).tilt(45F).build()
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(zoom))
            }
            //reset marker when already 2
//            if (listPoints!!.size == 1) {
//                listPoints!!.clear()
//                mMap.clear()
//            }
//            //save first poin select
//            listPoints!!.add(it)
//
//            //create marker
//            val markerOptions = MarkerOptions().position(it)
//
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//
//            mMap.addMarker(markerOptions)
//            mMap.addMarker(MarkerOptions().position(currentLocation!!).title("Lokasi Anda Sekarang"))
            if ((activity as MainActivity).getLatLong() != null) {
                mMap.clear()
                targetLocation = (activity as MainActivity).getLatLong()
                mMap.addMarker(MarkerOptions().position(targetLocation!!).title("Tujuan Anda").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                val zoom = CameraPosition.builder().target(targetLocation).zoom(15F).bearing(0F).tilt(45F).build()
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(zoom))
                mMap.addMarker(MarkerOptions().position(currentLocation!!).title("Lokasi Anda Sekarang"))
            }
        })
    }

    companion object {
        lateinit var v: View

        private lateinit var mMap: GoogleMap

        lateinit var mcontext: Context
        lateinit var mapView: MapView

        var listPoints: ArrayList<LatLng>? = null
        var currentLocation: LatLng? = null
        var targetLocation: LatLng? = null
    }

}
