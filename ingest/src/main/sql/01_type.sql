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
    'Stellar',
    'Unknown',
    'Shadow'
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
