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

-- Pokemon
ALTER TABLE pokemon
ADD CONSTRAINT fk_pokemon_species
FOREIGN KEY (species_id)
REFERENCES species (id);

-- Generation
ALTER TABLE generation
ADD CONSTRAINT fk_generation_region
FOREIGN KEY (main_region_id)
REFERENCES region (id);

-- Version
ALTER TABLE version
ADD CONSTRAINT fk_version_group
FOREIGN KEY (version_group_id)
REFERENCES version_group (id);

ALTER TABLE version_group
ADD CONSTRAINT fk_version_group_generation
FOREIGN KEY (generation_id)
REFERENCES generation (id);

-- Type
ALTER TABLE type_metadata
ADD CONSTRAINT fk_type_generation
FOREIGN KEY (generation_id)
REFERENCES generation (id);

-- Evolution
ALTER TABLE evolution_chain
ADD CONSTRAINT fk_evolution_item
FOREIGN KEY (baby_trigger_item_id)
REFERENCES item (id)
ON DELETE SET NULL;

-- Item
ALTER TABLE item
ADD CONSTRAINT fk_item_category
FOREIGN KEY (category_id)
REFERENCES item_category (id);

-- Species
ALTER TABLE species
ADD CONSTRAINT fk_species_generation
FOREIGN KEY (generation_id)
REFERENCES generation (id)
ON DELETE RESTRICT;

ALTER TABLE species
ADD CONSTRAINT fk_species_evolves_from
FOREIGN KEY (evolves_from)
REFERENCES species (id)
ON DELETE SET NULL;

ALTER TABLE species
ADD CONSTRAINT fk_species_evolution_chain
FOREIGN KEY (evolution_chain_id)
REFERENCES evolution_chain (id);

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

-- Sprites
ALTER TABLE pokemon_sprite
ADD CONSTRAINT fk_sprite_pokemon
FOREIGN KEY (pokemon_id)
REFERENCES pokemon (id);

ALTER TABLE pokemon_official_sprite
ADD CONSTRAINT fk_official_sprite_path
FOREIGN KEY (sprite_path)
REFERENCES pokemon_sprite (path);

ALTER TABLE pokemon_default_sprite
ADD CONSTRAINT fk_default_sprite_path
FOREIGN KEY (sprite_path)
REFERENCES pokemon_sprite (path);

ALTER TABLE pokemon_version_sprite
ADD CONSTRAINT fk_version_sprite_path
FOREIGN KEY (sprite_path)
REFERENCES pokemon_sprite (path);

ALTER TABLE pokemon_version_sprite
ADD CONSTRAINT fk_version_sprite_version
FOREIGN KEY (version_id)
REFERENCES version (id);

ALTER TABLE pokemon_misc_sprite
ADD CONSTRAINT fk_misc_sprite_path
FOREIGN KEY (sprite_path)
REFERENCES pokemon_sprite (path);
