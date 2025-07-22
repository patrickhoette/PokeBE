-- Sprites
ALTER TABLE pokemon_version_sprite
ADD CONSTRAINT uq_pokemon_version_sprite
UNIQUE (
    pokemon_id,
    version_id,
    is_shiny,
    is_female,
    is_back,
    is_grey,
    is_animated,
    is_icon,
    is_transparent
);
