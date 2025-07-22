CREATE TYPE PTYPE AS ENUM (
    'Normal',
    'Fire',
    'Water',
    'Grass',
    'Electric',
    'Ice',
    'Fighting',
    'Poison',
    'Ground',
    'Flying',
    'Psychic',
    'Bug',
    'Rock',
    'Ghost',
    'Dragon',
    'Dark',
    'Steel',
    'Fairy',
    'Stellar'
    );

CREATE TYPE DAMAGE_CLASS AS ENUM (
    'Status',
    'Physical',
    'Special'
    );

CREATE TYPE FLING_EFFECT AS ENUM (
    'BadPoison',
    'Burn',
    'BerryEffect',
    'HerbEffect',
    'Paralyze',
    'Poison',
    'Flinch'
    );

CREATE TYPE ITEM_POCKET AS ENUM (
    'Misc',
    'Medicine',
    'Pokeballs',
    'Machines',
    'Berries',
    'Mail',
    'Battle',
    'Key'
    );

CREATE TYPE PCOLOR AS ENUM (
    'Black',
    'Blue',
    'Brown',
    'Gray',
    'Green',
    'Pink',
    'Purple',
    'Red',
    'White',
    'Yellow'
    );

CREATE TYPE PSHAPE AS ENUM (
    'Ball',
    'Squiggle',
    'Fish',
    'Arms',
    'Blob',
    'Upright',
    'Legs',
    'Quadruped',
    'Wings',
    'Tentacles',
    'Heads',
    'Humanoid',
    'BugWings',
    'Armor'
    );

CREATE TYPE HABITAT AS ENUM (
    'Cave',
    'Forest',
    'Grassland',
    'Mountain',
    'Rare',
    'RoughTerrain',
    'Sea',
    'Urban',
    'WatersEdge'
    );

CREATE TYPE GROWTH_RATE AS ENUM (
    'Slow',
    'Medium',
    'Fast',
    'MediumSlow',
    'SlowThenVeryFast',
    'FastThenVerySlow'
    );
