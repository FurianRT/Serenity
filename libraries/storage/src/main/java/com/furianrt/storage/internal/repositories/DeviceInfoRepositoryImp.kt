package com.furianrt.storage.internal.repositories

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresPermission
import com.furianrt.domain.repositories.DeviceInfoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

internal class DeviceInfoRepositoryImp @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : DeviceInfoRepository {

    private val powerSaveModeState = MutableStateFlow(detectPowerSaveMode(context))
    private val dndState = MutableStateFlow(detectDndStatus(context))

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent?,
        ) {
            when (intent?.action) {
                PowerManager.ACTION_POWER_SAVE_MODE_CHANGED -> powerSaveModeState.update {
                    detectPowerSaveMode(context)
                }

                NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED -> dndState.update {
                    detectDndStatus(context)
                }
            }
        }
    }

    init {
        context.registerReceiver(
            broadcastReceiver,
            IntentFilter().apply {
                addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
                addAction(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
            },
            Context.RECEIVER_NOT_EXPORTED,
        )
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun hasNetworkConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun getAndroidVersion(): String = Build.VERSION.SDK_INT.toString()
    override fun getDeviceLanguage(): String = Locale.getDefault().language
    override fun getDeviceModel(): String = Build.MODEL
    override fun getMarketUrl(): String = "market://details?id=${context.packageName}"

    override fun getAppVersionName(): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName.orEmpty()
    }

    override fun getDeviceInfoText(): String =
        "${getDeviceModel()} ${getAndroidVersion()} ${getDeviceLanguage()} ${getAppVersionName()}"

    override fun isPowerSaveModeEnabled(): StateFlow<Boolean> = powerSaveModeState
    override fun isDndModeEnabled(): StateFlow<Boolean> = dndState

    private fun detectPowerSaveMode(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isPowerSaveMode
    }

    private fun detectDndStatus(context: Context): Boolean {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return nm.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
    }
}