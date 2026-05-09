package com.furianrt.storage.internal.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.furianrt.domain.entities.NoteLocation
import com.furianrt.domain.repositories.LocationRepository
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.storage.internal.database.notes.dao.LocationDao
import com.furianrt.storage.internal.database.notes.mappers.toEntryNoteLocation
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

private const val DETECTION_TIMEOUT = 15_000L

internal class LocationRepositoryImp @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val permissionsUtils: PermissionsUtils,
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

    override suspend fun detectLocation(): NoteLocation? = withTimeoutOrNull(DETECTION_TIMEOUT) {
        val location = getCurrentLocation() ?: return@withTimeoutOrNull null
        val address = getAddressFromLocation(location) ?: return@withTimeoutOrNull null
        return@withTimeoutOrNull NoteLocation(
            id = UUID.randomUUID().toString(),
            title = address,
            latitude = location.latitude,
            longitude = location.longitude,
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val cancellationTokenSource = CancellationTokenSource()
        val request = CurrentLocationRequest.Builder()
            .setPriority(
                if (permissionsUtils.hasFineLocationPermission()) {
                    Priority.PRIORITY_HIGH_ACCURACY
                } else {
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY
                }
            )
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setDurationMillis(DETECTION_TIMEOUT)
            .setMaxUpdateAgeMillis(0)
            .build()

        locationClient.getCurrentLocation(request, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (cont.isActive) {
                    cont.resumeWith(Result.success(location))
                }
            }
            .addOnFailureListener { error ->
                if (cont.isActive) {
                    cont.resumeWith(Result.failure(error))
                }
            }
        cont.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }

    private suspend fun getAddressFromLocation(
        location: Location,
    ): String? = suspendCancellableCoroutine { cont ->
        val listener = object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: List<Address?>) {
                val address = addresses.firstOrNull()?.getAddressLine(0)
                if (cont.isActive) {
                    cont.resumeWith(Result.success(address))
                }
            }

            override fun onError(errorMessage: String?) {
                if (cont.isActive) {
                    cont.resumeWith(Result.success(null))
                }
            }
        }
        Geocoder(appContext, Locale.getDefault())
            .getFromLocation(
                /* latitude = */ location.latitude,
                /* longitude = */ location.longitude,
                /* maxResults = */ 1,
                /* listener = */ listener,
            )
    }
}