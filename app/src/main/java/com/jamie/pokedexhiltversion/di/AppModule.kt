package com.jamie.pokedexhiltversion.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.jamie.pokedexhiltversion.data.local.PokedexDatabase
import com.jamie.pokedexhiltversion.data.local.converters.Converters
import com.jamie.pokedexhiltversion.data.remote.PokeApi
import com.jamie.pokedexhiltversion.repository.PokemonRepository
import com.jamie.pokedexhiltversion.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providePokemonRepository(
        api: PokeApi,
        db: PokedexDatabase
    ) = PokemonRepository(api, db)

    @Singleton
    @Provides
    fun provideGson(): Gson = Gson()

    @Singleton
    @Provides
    fun providePokedexDatabase(
        @ApplicationContext context: Context,
        gson: Gson
    ): PokedexDatabase {
        return Room.databaseBuilder(
            context,
            PokedexDatabase::class.java,
            "pokedex_db"
        )
            .addTypeConverter(Converters(gson))
            .fallbackToDestructiveMigration(false)
            .build()
    }


    @Singleton
    @Provides
    fun providePokemonDao(db: PokedexDatabase) = db.pokemonDao()

    @Singleton
    @Provides
    fun providePokeApi(): PokeApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)
    }
}