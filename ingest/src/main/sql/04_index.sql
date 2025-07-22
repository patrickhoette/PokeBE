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
CREATE INDEX idx_pokemon_sprite_pokemon_id ON pokemon_sprite (pokemon_id);

CREATE INDEX idx_version_sprite_version ON pokemon_version_sprite (version_id);

CREATE INDEX idx_misc_sprite_category ON pokemon_misc_sprite (category);

-- Species
CREATE INDEX IF NOT EXISTS idx_species_name
ON species(name);

CREATE INDEX IF NOT EXISTS idx_species_generation_id
ON species(generation_id);

CREATE INDEX IF NOT EXISTS idx_species_evolution_chain_id
ON species(evolution_chain_id);

-- Pokemon
CREATE INDEX IF NOT EXISTS idx_pokemon_species_id
ON pokemon(species_id);

-- Item
CREATE INDEX IF NOT EXISTS idx_item_name
ON item(name);

CREATE INDEX IF NOT EXISTS idx_item_category_id
ON item(category_id);

-- Item Category
CREATE INDEX IF NOT EXISTS idx_item_category_pocket
ON item_category(pocket);

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

-- Type Metadata
CREATE INDEX IF NOT EXISTS idx_type_metadata_generation
ON type_metadata(generation_id);

-- Evolution Chain
CREATE INDEX IF NOT EXISTS idx_evolution_chain_baby_trigger_item_id
ON evolution_chain(baby_trigger_item_id);

-- Version / Version Group
CREATE INDEX IF NOT EXISTS idx_version_group_generation_id
ON version_group(generation_id);

CREATE INDEX IF NOT EXISTS idx_version_version_group_id
ON version(version_group_id);
