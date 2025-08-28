package com.furianrt.storage.internal.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.furianrt.domain.entities.NoteLocation
import com.furianrt.domain.repositories.LocationRepository
import com.furianrt.storage.internal.database.notes.dao.LocationDao
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

internal class LocationRepositoryImp @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val locationDao: LocationDao,
) : LocationRepository {

    private val locationClient by lazy(LazyThreadSafetyMode.NONE) {
        LocationServices.getFusedLocationProviderClient(appContext)
    }

    override suspend fun insert(noteId: String, location: NoteLocation) {
        locationDao.insert(location.toEntryNoteLocation(noteId))
    }

    override suspend fun delete(noteId: String) {
        locationDao.delete(noteId)
    }

    override suspend fun detectLocation(): NoteLocation? {
        val location = getCurrentLocation() ?: return null
        val address = getAddressFromLocation(location) ?: return null
        return NoteLocation(
            id = UUID.randomUUID().toString(),
            title = address,
            latitude = location.latitude,
            longitude = location.longitude,
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setMaxUpdates(1)
            .build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                locationClient.removeLocationUpdates(this)
                val location = result.locations.firstOrNull()
                cont.resumeWith(Result.success(location))
            }
        }
        locationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        cont.invokeOnCancellation { locationClient.removeLocationUpdates(callback) }
    }

    private suspend fun getAddressFromLocation(
        location: Location,
    ): String? = suspendCancellableCoroutine { cont ->
        val geocoder = Geocoder(appContext, Locale.getDefault())
        geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
            val address = addresses.firstOrNull()?.getAddressLine(0)
            cont.resumeWith(Result.success(address))
        }
    }
}