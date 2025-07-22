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

CREATE TABLE IF NOT EXISTS pokemon_sprite (
    path       TEXT    NOT NULL PRIMARY KEY,
    pokemon_id INTEGER NOT NULL,
    variant    TEXT
);

CREATE TABLE IF NOT EXISTS pokemon_official_sprite (
    id          INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    sprite_path TEXT                                 NOT NULL,
    is_shiny    BOOLEAN                              NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS pokemon_default_sprite (
    id          INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    sprite_path TEXT                                 NOT NULL,
    is_shiny    BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_female   BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_back     BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_low_res  BOOLEAN                              NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS pokemon_misc_sprite (
    id          INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    sprite_path TEXT                                 NOT NULL,
    category    TEXT                                 NOT NULL,
    is_female   BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_shiny    BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_back     BOOLEAN                              NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS pokemon_version_sprite (
    id             INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    sprite_path    TEXT                                 NOT NULL,
    version_id     INTEGER                              NOT NULL,
    is_shiny       BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_female      BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_back        BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_grey        BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_animated    BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_transparent BOOLEAN                              NOT NULL DEFAULT FALSE
);
