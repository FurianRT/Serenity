package com.furianrt.storage.internal.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.furianrt.domain.repositories.DeviceInfoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

internal class DeviceInfoRepositoryImp @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : DeviceInfoRepository {

    override fun hasNetworkConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun getAndroidVersion(): String = Build.VERSION.SDK_INT.toString()
    override fun getDeviceLanguage(): String =  Locale.getDefault().language
    override fun getDeviceModel(): String = Build.MODEL
    override fun getMarketUrl(): String = "market://details?id=${context.packageName}"

    override fun getAppVersionName(): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName.orEmpty()
    }
}