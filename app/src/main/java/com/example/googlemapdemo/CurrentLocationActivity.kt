package com.example.googlemapdemo

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.googlemapdemo.databinding.ActivityCurrentLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class CurrentLocationActivity : AppCompatActivity() {
    lateinit var binding: ActivityCurrentLocationBinding
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val permissionID = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCurrentLocationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.currentLocation.setOnClickListener {
            getCurrentLocation()
        }
    }
    fun getCurrentLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                mFusedLocationClient.lastLocation.addOnCompleteListener(this){ task->
                    val location:Location? = task.result
                    if(location != null){
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        binding.apply {
                            latitude.text = "Latitude\n${list[0].latitude}"
                            longitude.text = "Longitude\n${list[0].longitude}"
                            countryName.text = "Country Name\n${list[0].countryName}"
                            locality.text = "Locality\n${list[0].locality}"
                            address.text = "Address\n${list[0].getAddressLine(0)}"
                        }
                    }
                }
            }else{
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            requestPermissions()
        }
    }
    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return  true
        }
        return false
    }
    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            permissionID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == permissionID){
            if((grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                getCurrentLocation()
            }
        }
    }
}