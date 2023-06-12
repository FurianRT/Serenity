package com.furianrt.storage.internal.notes.repositories

import com.furianrt.storage.api.repositories.TagsRepository
import com.furianrt.storage.internal.notes.dao.TagDao
import javax.inject.Inject

internal class TagsRepositoryImp @Inject constructor(
    private val tagDao: TagDao,
) : TagsRepository
