package eu.jw.tbo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.jw.tbo.data.repository.CoinRepositoryImpl
import eu.jw.tbo.domain.repository.CoinRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoinRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepository(
        repositoryImpl: CoinRepositoryImpl
    ): CoinRepository
}