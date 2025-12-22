package com.example.quranapp.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class LatLng(val latitude: Double, val longitude: Double)

class LocationProvider(private val context: Context) {

    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): LatLng? {
        if (!hasPermission()) return null
        val client = LocationServices.getFusedLocationProviderClient(context)
        val loc = suspendCancellableCoroutine<android.location.Location?> { cont ->
            client.lastLocation.addOnSuccessListener { cont.resume(it) }
            client.lastLocation.addOnFailureListener { cont.resume(null) }
        }
        return loc?.let { LatLng(it.latitude, it.longitude) }
    }
}
