package id.islaami.playmi2021.config

import id.islaami.playmi2021.data.api.*
import id.islaami.playmi2021.data.cache.UserCache
import id.islaami.playmi2021.data.model.profile.Profile
import id.islaami.playmi2021.data.repository.*
import id.islaami.playmi2021.ui.MainViewModel
import id.islaami.playmi2021.ui.auth.UserAuthViewModel
import id.islaami.playmi2021.ui.channel.ChannelViewModel
import id.islaami.playmi2021.ui.channel_following.OrganizeChannelViewModel
import id.islaami.playmi2021.ui.home.HomeViewModel
import id.islaami.playmi2021.ui.intro.IntroViewModel
import id.islaami.playmi2021.ui.playlist.PlaylistViewModel
import id.islaami.playmi2021.ui.setting.SettingViewModel
import id.islaami.playmi2021.ui.setting.profile.ProfileViewModel
import id.islaami.playmi2021.ui.video.VideoViewModel
import id.islaami.playmi2021.ui.video_update.VideoUpdateViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        cacheModule,
        apiModule,
        repositoryModule,
        viewModelModule
    )
}

//
val cacheModule: Module = module {
    single { Cache<Profile>() }
    single { UserCache(profileCache = get(), notifSettingCache = get()) }
}

val repositoryModule: Module = module {
    factory { UserRepository(userCache = get(), userApi = get()) }
    factory { SettingRepository(userCache = get(), userApi = get(), api = get()) }
    factory { HomeRepository(api = get()) }
    factory { VideoRepository(userCache = get(), video = get(), channel = get()) }
    factory { CategoryRepository(api = get()) }
    factory { PlaylistRepository(playlist = get(), video = get(), channel = get()) }
    factory { ChannelRepository(api = get()) }
}

val viewModelModule: Module = module {
    viewModel { UserAuthViewModel(repository = get()) }
    viewModel { IntroViewModel(repository = get()) }
    viewModel { ChannelViewModel(repository = get(),video = get(), playlist = get()) }
    viewModel { HomeViewModel(channel = get(), video = get(), category = get(), playlist = get()) }
    viewModel { VideoViewModel(video = get(), channel = get(), playlist = get()) }
    viewModel { VideoUpdateViewModel(repository = get()) }
    viewModel { OrganizeChannelViewModel(repository = get()) }
    viewModel { PlaylistViewModel(repository = get()) }
    viewModel { SettingViewModel(repository = get(), userRepository = get()) }
    viewModel { ProfileViewModel(userRepository = get()) }
    viewModel { MainViewModel(userRepository = get()) }
}

val apiModule: Module = module {
    factory { createRetrofitClient(userCache = get()).create(UserApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(HomeApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(PlaylistApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(VideoApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(CategoryApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(ChannelApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(SettingApi::class.java) }
}
