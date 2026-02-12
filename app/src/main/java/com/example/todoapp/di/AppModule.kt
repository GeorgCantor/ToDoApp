package com.example.todoapp.di

import androidx.datastore.core.DataStore
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.todoapp.TodoApp
import com.example.todoapp.data.remote.ApolloGraphQLClient
import com.example.todoapp.data.remote.GraphQLClient
import com.example.todoapp.data.remote.api.NewsApiService
import com.example.todoapp.data.repository.AuthRepositoryImpl
import com.example.todoapp.data.repository.CalculatorRepositoryImpl
import com.example.todoapp.data.repository.CartRepositoryImpl
import com.example.todoapp.data.repository.ChatRepositoryImpl
import com.example.todoapp.data.repository.CoroutineMonitorRepositoryImpl
import com.example.todoapp.data.repository.DocumentRepositoryImpl
import com.example.todoapp.data.repository.NewsRepositoryImpl
import com.example.todoapp.data.repository.PlayerRepositoryImpl
import com.example.todoapp.data.repository.SpaceXRepositoryImpl
import com.example.todoapp.data.repository.UserProfileRepositoryImpl
import com.example.todoapp.domain.manager.BiometricAuthManager
import com.example.todoapp.domain.manager.BiometricAuthManagerImpl
import com.example.todoapp.domain.model.UserProfile
import com.example.todoapp.domain.repository.AuthRepository
import com.example.todoapp.domain.repository.CalculatorRepository
import com.example.todoapp.domain.repository.CartRepository
import com.example.todoapp.domain.repository.ChatRepository
import com.example.todoapp.domain.repository.CoroutineMonitorRepository
import com.example.todoapp.domain.repository.DocumentRepository
import com.example.todoapp.domain.repository.NewsRepository
import com.example.todoapp.domain.repository.PlayerRepository
import com.example.todoapp.domain.repository.SpaceXRepository
import com.example.todoapp.domain.repository.UserProfileRepository
import com.example.todoapp.domain.usecase.AudioToBase64UseCase
import com.example.todoapp.domain.usecase.Base64ToAudioFileUseCase
import com.example.todoapp.domain.usecase.CalculateExpressionUseCase
import com.example.todoapp.domain.usecase.CalculateTotalUseCase
import com.example.todoapp.domain.usecase.ClearCalculatorUseCase
import com.example.todoapp.domain.usecase.DeleteMediaItemUseCase
import com.example.todoapp.domain.usecase.DeleteMessageUseCase
import com.example.todoapp.domain.usecase.DownloadDocumentUseCase
import com.example.todoapp.domain.usecase.EditMessageUseCase
import com.example.todoapp.domain.usecase.GetAvailableDocumentsUseCase
import com.example.todoapp.domain.usecase.GetCartItemsUseCase
import com.example.todoapp.domain.usecase.GetChatMessagesUseCase
import com.example.todoapp.domain.usecase.GetLaunchDetailUseCase
import com.example.todoapp.domain.usecase.GetLaunchStatisticsUseCase
import com.example.todoapp.domain.usecase.GetLocalMediaUseCase
import com.example.todoapp.domain.usecase.GetMediaItemsUseCase
import com.example.todoapp.domain.usecase.GetRecentMediaUseCase
import com.example.todoapp.domain.usecase.GetSpaceXLaunchesUseCase
import com.example.todoapp.domain.usecase.GetTopHeadlinesUseCase
import com.example.todoapp.domain.usecase.GetUserProfileUseCase
import com.example.todoapp.domain.usecase.InitializeUserProfileUseCase
import com.example.todoapp.domain.usecase.ManagePlayerUseCase
import com.example.todoapp.domain.usecase.ObserveMessagesUseCase
import com.example.todoapp.domain.usecase.PlayMediaUseCase
import com.example.todoapp.domain.usecase.RemoveFromCartUseCase
import com.example.todoapp.domain.usecase.SaveMediaItemUseCase
import com.example.todoapp.domain.usecase.SaveUserProfileUseCase
import com.example.todoapp.domain.usecase.SendMessageUseCase
import com.example.todoapp.domain.usecase.UpdateQuantityUseCase
import com.example.todoapp.domain.usecase.UpdateUserStatisticsUseCase
import com.example.todoapp.presentation.viewmodel.AuthViewModel
import com.example.todoapp.presentation.viewmodel.CalculatorViewModel
import com.example.todoapp.presentation.viewmodel.CartViewModel
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.CoroutineMonitorViewModel
import com.example.todoapp.presentation.viewmodel.DocumentsViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel
import com.example.todoapp.presentation.viewmodel.PlayerViewModel
import com.example.todoapp.presentation.viewmodel.ProfileViewModel
import com.example.todoapp.presentation.viewmodel.SpaceXStatsViewModel
import com.example.todoapp.presentation.viewmodel.SpaceXViewModel
import com.example.todoapp.presentation.visualization.SpaceXVisualizerFactory
import com.example.todoapp.presentation.visualization.VisualizerFactory
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule =
    module {
        single<OkHttpClient> {
            val loggingInterceptor =
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

            val chuckerInterceptor =
                ChuckerInterceptor
                    .Builder(androidContext())
                    .collector(ChuckerCollector(androidContext()))
                    .maxContentLength(250000L)
                    .redactHeaders("Auth-Token", "Bearer")
                    .alwaysReadResponseBody(false)
                    .build()

            OkHttpClient
                .Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chuckerInterceptor)
                .build()
        }

        single<Retrofit> {
            Retrofit
                .Builder()
                .baseUrl("https://newsapi.org/")
                .client(get<OkHttpClient>())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        single { Gson() }

        single<CartRepository> {
            CartRepositoryImpl(
                context = androidContext(),
                gson = get(),
            )
        }
        single<NewsApiService> { get<Retrofit>().create(NewsApiService::class.java) }
        single<BiometricAuthManager> { BiometricAuthManagerImpl(androidContext()) }
        single<DataStore<UserProfile>> { androidContext().userProfileDataStore }
        single<NewsRepository> { NewsRepositoryImpl(get()) }
        single<ChatRepository> { ChatRepositoryImpl() }
        single<DocumentRepository> { DocumentRepositoryImpl() }
        single<CalculatorRepository> { CalculatorRepositoryImpl() }
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<UserProfileRepository> { UserProfileRepositoryImpl(get()) }
        single<GraphQLClient> { ApolloGraphQLClient(get()) }
        single<SpaceXRepository> { SpaceXRepositoryImpl(get()) }
        single<VisualizerFactory> { SpaceXVisualizerFactory() }
        single<CoroutineMonitorRepository> { CoroutineMonitorRepositoryImpl() }
        single<PlayerRepository> { PlayerRepositoryImpl(androidContext()) }

        factory { GetTopHeadlinesUseCase(get()) }
        factory { SendMessageUseCase(get()) }
        factory { GetChatMessagesUseCase(get()) }
        factory { ObserveMessagesUseCase(get()) }
        factory { EditMessageUseCase(get()) }
        factory { DeleteMessageUseCase(get()) }
        factory { AudioToBase64UseCase(get()) }
        factory { Base64ToAudioFileUseCase(get()) }
        factory { GetAvailableDocumentsUseCase(get()) }
        factory { DownloadDocumentUseCase(get()) }
        factory { CalculateExpressionUseCase(get()) }
        factory { ClearCalculatorUseCase(get()) }
        factory { GetUserProfileUseCase(get()) }
        factory { SaveUserProfileUseCase(get()) }
        factory { UpdateUserStatisticsUseCase(get()) }
        factory { InitializeUserProfileUseCase(get()) }
        factory { GetSpaceXLaunchesUseCase(get()) }
        factory { GetLaunchDetailUseCase(get()) }
        factory { GetLaunchStatisticsUseCase(get()) }
        factory { SpaceXVisualizerFactory() }
        factory { GetMediaItemsUseCase(get<PlayerRepository>()) }
        factory { GetRecentMediaUseCase(get<PlayerRepository>()) }
        factory { SaveMediaItemUseCase(get<PlayerRepository>()) }
        factory { DeleteMediaItemUseCase(get<PlayerRepository>()) }
        factory { GetLocalMediaUseCase(get<PlayerRepository>()) }
        factory { GetCartItemsUseCase(get()) }
        factory { UpdateQuantityUseCase(get()) }
        factory { RemoveFromCartUseCase(get()) }
        factory { CalculateTotalUseCase() }

        factory<PlayMediaUseCase> {
            val app = androidApplication() as TodoApp
            PlayMediaUseCase(app.exoPlayerManager)
        }

        factory<ManagePlayerUseCase> {
            val app = androidApplication() as TodoApp
            ManagePlayerUseCase(app.exoPlayerManager)
        }

        viewModel { NewsViewModel(get(), get()) }
        viewModel {
            ChatViewModel(
                sendMessageUseCase = get(),
                observeMessagesUseCase = get(),
                editMessageUseCase = get(),
                deleteMessageUseCase = get(),
                audioToBase64UseCase = get(),
                base64ToAudioFileUseCase = get(),
                updateUserStatisticsUseCase = get(),
            )
        }
        viewModel {
            PlayerViewModel(
                application = androidApplication(),
                managePlayerUseCase = get(),
                playMediaUseCase = get(),
                getMediaItemsUseCase = get(),
                getRecentMediaUseCase = get(),
                saveMediaItemUseCase = get(),
                deleteMediaItemUseCase = get(),
                getLocalMediaUseCase = get(),
            )
        }
        viewModel {
            CartViewModel(
                getCartItemsUseCase = get(),
                updateQuantityUseCase = get(),
                removeFromCartUseCase = get(),
                calculateTotalUseCase = get(),
            )
        }
        viewModel { ProfileViewModel(get(), get()) }
        viewModel { DocumentsViewModel(get(), get()) }
        viewModel { CalculatorViewModel(get(), get(), get()) }
        viewModel { AuthViewModel(get(), get()) }
        viewModel { SpaceXViewModel(get(), get()) }
        viewModel { SpaceXStatsViewModel(get(), get()) }
        viewModel { CoroutineMonitorViewModel(get()) }
    }
