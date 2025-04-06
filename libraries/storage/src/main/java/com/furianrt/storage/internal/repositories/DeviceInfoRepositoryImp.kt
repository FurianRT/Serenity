package com.furianrt.storage.internal.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.furianrt.domain.repositories.DeviceInfoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class DeviceInfoRepositoryImp @Inject constructor(
    @ApplicationContext private val context: Context,
) : DeviceInfoRepository {

    override fun hasNetworkConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}