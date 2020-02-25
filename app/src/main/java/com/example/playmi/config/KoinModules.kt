package com.example.playmi.config

import com.example.playmi.data.api.*
import com.example.playmi.data.cache.UserCache
import com.example.playmi.data.model.profile.Profile
import com.example.playmi.data.repository.*
import com.example.playmi.ui.auth.UserAuthViewModel
import com.example.playmi.ui.channel.ChannelViewModel
import com.example.playmi.ui.following.FollowingViewModel
import com.example.playmi.ui.home.HomeViewModel
import com.example.playmi.ui.intro.IntroViewModel
import com.example.playmi.ui.playlist.PlaylistViewModel
import com.example.playmi.ui.setting.SettingViewModel
import com.example.playmi.ui.video.VideoViewModel
import com.example.playmi.ui.video_update.VideoUpdateViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Created by Kemal Amru Ramadhan on 12/04/2019.
 */

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        cacheModule,
        apiModule,
        repositoryModule,
        viewModelModule
    )
}

val cacheModule: Module = module {
    single { Cache<Profile>() }
    single { UserCache(profileCache = get(), notifSettingCache = get()) }
}

val repositoryModule: Module = module {
    factory { UserRepository(userCache = get(), userApi = get()) }
    factory { HomeRepository(api = get()) }
    factory { VideoRepository(userCache = get(), api = get()) }
    factory { CategoryRepository(api = get()) }
    factory { PlaylistRepository(playlistApi = get(), videoApi = get()) }
    factory { ChannelRepository(api = get()) }
}

val viewModelModule: Module = module {
    viewModel { UserAuthViewModel(repository = get()) }
    viewModel { IntroViewModel(repository = get()) }
    viewModel { ChannelViewModel(repository = get()) }
    viewModel { HomeViewModel(home = get(), video = get(), category = get()) }
    viewModel { VideoViewModel(video = get(), channel = get(), playlist = get()) }
    viewModel { VideoUpdateViewModel(repository = get()) }
    viewModel { FollowingViewModel(repository = get()) }
    viewModel { PlaylistViewModel(repository = get()) }
    viewModel { SettingViewModel(repository = get()) }
}

val apiModule: Module = module {
    factory { createRetrofitClient(userCache = get()).create(UserApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(HomeApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(PlaylistApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(VideoApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(CategoryApi::class.java) }
    factory { createRetrofitClient(userCache = get()).create(ChannelApi::class.java) }
}
