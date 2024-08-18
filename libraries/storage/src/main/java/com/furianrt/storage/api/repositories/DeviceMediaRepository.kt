package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.DeviceMedia

interface DeviceMediaRepository {
    suspend fun getMediaList(): List<DeviceMedia>
}