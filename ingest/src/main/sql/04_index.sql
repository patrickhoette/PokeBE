-- Sprites
CREATE INDEX IF NOT EXISTS idx_version_sprite_pokemon_id
ON pokemon_version_sprite (pokemon_id);

CREATE INDEX IF NOT EXISTS idx_version_sprite_version_id
ON pokemon_version_sprite (version_id);

CREATE INDEX IF NOT EXISTS idx_version_sprite_lookup
ON pokemon_version_sprite (
    pokemon_id,
    version_id,
    is_shiny,
    is_back,
    is_grey,
    is_transparent,
    is_icon,
    is_animated
);

CREATE INDEX IF NOT EXISTS idx_misc_sprite_pokemon_id
ON pokemon_misc_sprite (pokemon_id);

CREATE INDEX IF NOT EXISTS idx_misc_sprite_category
ON pokemon_misc_sprite (category);

CREATE INDEX IF NOT EXISTS idx_misc_lookup
ON pokemon_misc_sprite (
    pokemon_id,
    category,
    is_shiny,
    is_female,
    is_back
);
