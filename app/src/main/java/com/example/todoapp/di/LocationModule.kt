package com.example.todoapp.di

import com.example.todoapp.data.remote.FusedLocationDataSource
import com.example.todoapp.data.remote.FusedLocationDataSourceImpl
import com.example.todoapp.data.remote.HistoryDataSource
import com.example.todoapp.data.remote.HistoryDataSourceImpl
import com.example.todoapp.data.remote.IpLocationDataSource
import com.example.todoapp.data.remote.IpLocationDataSourceImpl
import com.example.todoapp.data.remote.LocationCacheDataSource
import com.example.todoapp.data.remote.LocationCacheDataSourceImpl
import com.example.todoapp.data.repository.LocationRepositoryImpl
import com.example.todoapp.domain.repository.LocationRepository
import com.example.todoapp.domain.usecase.GetLocationHistoryUseCase
import com.example.todoapp.domain.usecase.GetLocationUseCase
import com.example.todoapp.domain.usecase.RequestExactLocationUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val locationModule =
    module {
        single<IpLocationDataSource> {
            IpLocationDataSourceImpl(client = get())
        }

        single<FusedLocationDataSource> {
            FusedLocationDataSourceImpl(locationClient = get())
        }

        single<LocationCacheDataSource> {
            LocationCacheDataSourceImpl(context = androidContext())
        }

        single<HistoryDataSource> {
            HistoryDataSourceImpl(context = androidContext(), gson = get())
        }

        single<FusedLocationProviderClient> {
            LocationServices.getFusedLocationProviderClient(androidApplication())
        }

        single<LocationRepository> {
            LocationRepositoryImpl(
                ipLocationDataSource = get(),
                fusedLocationDataSource = get(),
                cacheDataSource = get(),
                historyDataSource = get(),
                context = androidContext(),
            )
        }

        factory { GetLocationUseCase(repository = get()) }
        factory { RequestExactLocationUseCase(repository = get()) }
        factory { GetLocationHistoryUseCase(repository = get()) }
    }
