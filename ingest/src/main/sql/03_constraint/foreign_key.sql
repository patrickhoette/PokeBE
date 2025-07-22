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
ADD CONSTRAINT fk_type_region
FOREIGN KEY (generation_id)
REFERENCES generation (id);

-- Evolution
ALTER TABLE evolution_chain
ADD CONSTRAINT fk_evolution_item
FOREIGN KEY (baby_trigger_item_id)
REFERENCES item (id);

-- Item
ALTER TABLE item
ADD CONSTRAINT fk_item_category
FOREIGN KEY (category_id)
REFERENCES item_category (id);

-- Species
ALTER TABLE species
ADD CONSTRAINT fk_species_generation
FOREIGN KEY (generation_id)
REFERENCES generation (id);

ALTER TABLE species
ADD CONSTRAINT fk_species_evolves_from
FOREIGN KEY (evolves_from)
REFERENCES species (id);

ALTER TABLE species
ADD CONSTRAINT fk_species_evolution_chain
FOREIGN KEY (evolution_chain_id)
REFERENCES evolution_chain (id);

-- Sprites
ALTER TABLE pokemon_official_sprite
ADD CONSTRAINT fk_official_sprite_pokemon
FOREIGN KEY (pokemon_id)
REFERENCES pokemon (id);

ALTER TABLE pokemon_version_sprite
ADD CONSTRAINT fk_version_sprite_pokemon
FOREIGN KEY (pokemon_id)
REFERENCES pokemon (id);

ALTER TABLE pokemon_misc_sprite
ADD CONSTRAINT fk_misc_sprite_pokemon
FOREIGN KEY (pokemon_id)
REFERENCES pokemon (id);
