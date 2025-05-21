package com.furianrt.serenity.di

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.furianrt.backup.api.RootActivityIntentProvider
import com.furianrt.common.BuildInfoProvider
import com.furianrt.common.ErrorTracker
import com.furianrt.core.DispatchersProvider
import com.furianrt.serenity.BuildConfig
import com.furianrt.serenity.MainActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    @Singleton
    @Provides
    fun provideDispatchersProvider(): DispatchersProvider = object : DispatchersProvider {
        override val main: CoroutineDispatcher = Dispatchers.Main
        override val io: CoroutineDispatcher = Dispatchers.IO
        override val default: CoroutineDispatcher = Dispatchers.Default
        override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    }

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        BuildConfig.VERSION_NAME
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Singleton
    @Provides
    fun provideRootActivityIntentProvider(
        @ApplicationContext context: Context,
    ): RootActivityIntentProvider = object : RootActivityIntentProvider {
        override fun provide(): Intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
    }

    @Singleton
    @Provides
    fun provideErrorTracker(crashlytics: FirebaseCrashlytics) = object : ErrorTracker {
        override fun trackNonFatalError(error: Throwable) {
            error.printStackTrace()
            crashlytics.recordException(error)
        }
    }

    @Singleton
    @Provides
    fun provideBuildInfoProvider(): BuildInfoProvider = object : BuildInfoProvider {
        override fun getAppVersionName(): String = BuildConfig.VERSION_NAME
    }
}