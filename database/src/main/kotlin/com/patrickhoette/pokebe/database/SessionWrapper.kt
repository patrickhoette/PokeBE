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

import com.patrickhoette.pokebe.core.util.coroutine.DispatcherProvider
import kotlinx.coroutines.withContext
import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import org.intellij.lang.annotations.Language

class SessionWrapper(
    private val session: Session,
    private val dispatcherProvider: DispatcherProvider,
) : QueryContext {

    override suspend fun executeAsUpdate(@Language("SQL") query: String, vararg params: Any?) {
        withContext(dispatcherProvider.IO) {
            session.run(queryOf(query, *params).asUpdate)
        }
    }

    override suspend fun <R> executeAsSingle(
        @Language("SQL") query: String,
        vararg params: Any?,
        mapper: (Row) -> R?,
    ): R? = withContext(dispatcherProvider.IO) {
        session.run(queryOf(query, *params).map(mapper).asSingle)
    }

    override suspend fun <R> executeAsList(
        @Language("SQL") query: String,
        vararg params: Any?,
        mapper: (Row) -> R?,
    ): List<R> = withContext(dispatcherProvider.IO) {
        session.run(queryOf(query, *params).map(mapper).asList)
    }

    override suspend fun execute(@Language("SQL") query: String, vararg params: Any?) {
        withContext(dispatcherProvider.IO) {
            session.run(queryOf(query, *params).asExecute)
        }
    }

    override suspend fun executeAsUpdate(@Language("SQL") query: String, params: Map<String, Any?>) {
        withContext(dispatcherProvider.IO) {
            session.run(queryOf(query, params).asUpdate)
        }
    }

    override suspend fun <R> executeAsSingle(
        @Language("SQL") query: String,
        params: Map<String, Any?>,
        mapper: (Row) -> R?,
    ): R? = withContext(dispatcherProvider.IO) {
        session.run(queryOf(query, params).map(mapper).asSingle)
    }

    override suspend fun <R> executeAsList(
        @Language("SQL") query: String,
        params: Map<String, Any?>,
        mapper: (Row) -> R?,
    ): List<R> = withContext(dispatcherProvider.IO) {
        session.run(queryOf(query, params).map(mapper).asList)
    }

    override suspend fun execute(@Language("SQL") query: String, params: Map<String, Any?>) {
        withContext(dispatcherProvider.IO) {
            session.run(queryOf(query, params).asExecute)
        }
    }
}
