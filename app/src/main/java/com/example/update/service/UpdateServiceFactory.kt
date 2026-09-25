package com.example.update.service

import android.content.Context
import com.example.update.repository.UpdateRepository

object UpdateServiceFactory {

    private var activeProviderType: UpdateProviderType = UpdateProviderType.NEXORA_PACKAGE

    fun create(
        context: Context,
        repository: UpdateRepository,
        providerType: UpdateProviderType = activeProviderType
    ): UpdateService {
        return when (providerType) {
            UpdateProviderType.NEXORA_PACKAGE -> InAppApkUpdateProvider(context, repository)
            UpdateProviderType.GOOGLE_PLAY -> GooglePlayUpdateProvider(context, repository)
        }
    }

    fun setActiveProvider(type: UpdateProviderType) {
        activeProviderType = type
    }

    fun getActiveProviderType(): UpdateProviderType = activeProviderType
}
