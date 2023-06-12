package com.furianrt.storage.internal.notes.repositories

import com.furianrt.storage.api.repositories.ImagesRepository
import com.furianrt.storage.internal.notes.dao.ImageDao
import javax.inject.Inject

internal class ImagesRepositoryImp @Inject constructor(
    private val imageDao: ImageDao,
) : ImagesRepository
