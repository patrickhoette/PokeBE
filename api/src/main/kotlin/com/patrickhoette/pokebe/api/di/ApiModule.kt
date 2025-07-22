/*
 * Copyright 2025 Patrick Hoette
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

@file:OptIn(ExperimentalSerializationApi::class)

package com.patrickhoette.pokebe.api.di

import com.patrickhoette.pokebe.api.config.model.SpriteConfig
import com.patrickhoette.pokebe.api.coroutine.DefaultDispatcherProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import com.patrickhoette.pokebe.core.api.CoreApiModule
import com.patrickhoette.pokebe.core.data.CoreDataModule
import com.patrickhoette.pokebe.core.domain.CoreDomainModule
import com.patrickhoette.pokebe.core.util.coroutine.DispatcherProvider
import com.patrickhoette.pokebe.database.DatabaseModule
import com.patrickhoette.pokebe.entity.pokemon.config.ApiConfig
import com.patrickhoette.pokebe.pokemon.api.PokemonApiModule
import com.patrickhoette.pokebe.pokemon.data.PokemonDataModule
import com.patrickhoette.pokebe.pokemon.domain.PokemonDomainModule
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import io.github.config4k.extract

@Module(
    includes = [
        DatabaseModule::class,
        CoreApiModule::class,
        CoreDomainModule::class,
        CoreDataModule::class,
        PokemonApiModule::class,
        PokemonDomainModule::class,
        PokemonDataModule::class,
    ]
)
@ComponentScan("com.patrickhoette.pokebe.api")
class ApiModule {

    @Factory
    fun createJson(): Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true

        allowTrailingComma = true
    }

    @Factory(binds = [DispatcherProvider::class])
    fun createDispatcherProvider() = DefaultDispatcherProvider

    @Factory
    fun createConfig(): Config = ConfigFactory.load()

    @Factory
    fun createSpriteConfig(config: Config) = config.extract<SpriteConfig>("sprite")

    @Factory
    fun createApiConfig(config: SpriteConfig) = ApiConfig(
        spriteBaseUrl = config.baseUrl,
    )
}
