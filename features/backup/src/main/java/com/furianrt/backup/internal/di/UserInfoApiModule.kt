package com.furianrt.backup.internal.di

import com.furianrt.backup.BuildConfig
import com.furianrt.backup.internal.data.remote.google.info.UserInfoApiService
import com.furianrt.backup.internal.data.remote.google.token.TokenAuthenticator
import com.furianrt.backup.internal.data.remote.google.token.TokenInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

private const val BASE_URL = "https://people.googleapis.com"

@Module
@InstallIn(SingletonComponent::class)
internal object UserInfoApiModule {
    @Provides
    @UserInfoApiQualifier
    @Singleton
    fun provideUserInfoOkHttpClient(
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
    @UserInfoApiQualifier
    @Singleton
    fun provideUserInfoRetrofit(
        @UserInfoApiQualifier okHttpClient: OkHttpClient,
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideUserInfoApiService(
        @UserInfoApiQualifier retrofit: Retrofit,
    ): UserInfoApiService = retrofit.create(UserInfoApiService::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class UserInfoApiQualifier
