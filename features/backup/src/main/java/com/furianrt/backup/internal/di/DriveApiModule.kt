package com.furianrt.backup.internal.di

import com.furianrt.backup.BuildConfig
import com.furianrt.backup.internal.data.remote.drive.DriveApiService
import com.furianrt.backup.internal.data.remote.token.TokenAuthenticator
import com.furianrt.backup.internal.data.remote.token.TokenInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier

private const val BASE_URL = "https://www.googleapis.com/drive/v3"

@Module
@InstallIn(ViewModelComponent::class)
internal object DriveApiModule {
    @Provides
    @DriveApiQualifier
    @ViewModelScoped
    fun provideDriveOkHttpClient(
        tokenAuthenticator: TokenAuthenticator,
        tokenInterceptor: TokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .authenticator(tokenAuthenticator)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

    @Provides
    @DriveApiQualifier
    @ViewModelScoped
    fun provideDriveRetrofit(
        @DriveApiQualifier okHttpClient: OkHttpClient,
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
        }
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideDriveApiService(
        @DriveApiQualifier retrofit: Retrofit,
    ): DriveApiService = retrofit.create(DriveApiService::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class DriveApiQualifier
