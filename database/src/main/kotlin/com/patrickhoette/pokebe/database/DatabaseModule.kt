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

package com.patrickhoette.pokebe.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import javax.sql.DataSource

@Module
@ComponentScan("com.patrickhoette.pokebe.database")
class DatabaseModule {

    @Factory(binds = [HikariConfig::class])
    fun createConfig() = HikariConfig().apply {
        val databaseUrl = System.getenv("DATABASE_URL")
        jdbcUrl = "jdbc:$databaseUrl"

        username = System.getenv("DATABASE_USER")
        password = System.getenv("DATABASE_PASSWORD")

        maximumPoolSize = 10
        idleTimeout = 10_000
        connectionTimeout = 10_000
        maxLifetime = 1_800_000
        isAutoCommit = true

        driverClassName = "org.postgresql.Driver"
    }

    @Factory(binds = [DataSource::class])
    fun createDatasource(config: HikariConfig) = HikariDataSource(config)
}
