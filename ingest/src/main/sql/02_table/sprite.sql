CREATE TABLE IF NOT EXISTS pokemon_official_sprite (
    pokemon_id INTEGER NOT NULL PRIMARY KEY,
    base       TEXT,
    shiny      TEXT
);

CREATE TABLE IF NOT EXISTS pokemon_version_sprite (
    id             INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    pokemon_id     INTEGER                              NOT NULL,
    version_id     INTEGER                              NOT NULL,
    is_shiny       BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_female      BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_back        BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_grey        BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_animated    BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_icon        BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_transparent BOOLEAN                              NOT NULL DEFAULT FALSE,
    sprite_path    TEXT                                 NOT NULL
);

CREATE TABLE IF NOT EXISTS pokemon_misc_sprite (
    id          INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    pokemon_id  INTEGER                              NOT NULL,
    category    TEXT                                 NOT NULL,
    is_female   BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_shiny    BOOLEAN                              NOT NULL DEFAULT FALSE,
    is_back     BOOLEAN                              NOT NULL DEFAULT FALSE,
    sprite_path TEXT                                 NOT NULL
);
