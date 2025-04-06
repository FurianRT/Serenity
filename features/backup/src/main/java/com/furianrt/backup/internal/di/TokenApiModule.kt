package com.furianrt.backup.internal.di

import com.furianrt.backup.BuildConfig
import com.furianrt.backup.internal.data.remote.token.TokenApiService
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

private const val BASE_URL = "https://oauth2.googleapis.com"

@Module
@InstallIn(ViewModelComponent::class)
internal object TokenApiModule {
    @Provides
    @TokenApiQualifier
    @ViewModelScoped
    fun provideTokenOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

    @Provides
    @TokenApiQualifier
    @ViewModelScoped
    fun provideTokenRetrofit(
        @TokenApiQualifier okHttpClient: OkHttpClient,
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
    fun provideTokenApiService(
        @TokenApiQualifier retrofit: Retrofit,
    ): TokenApiService = retrofit.create(TokenApiService::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class TokenApiQualifier
