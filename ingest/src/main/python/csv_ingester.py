#  Copyright 2025 Patrick Hoette
#
#  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
#  documentation files (the “Software”), to deal in the Software without restriction, including without limitation
#  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
#  to permit persons to whom the Software is furnished to do so.
#
#  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
#  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
#  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
#  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
#  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
#  OTHER DEALINGS IN THE SOFTWARE.

#
#  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
#  documentation files (the “Software”), to deal in the Software without restriction, including without limitation
#  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
#  to permit persons to whom the Software is furnished to do so.
#
import csv
import io
import os
import pathlib
from logger import debug, info, warning, error, critical
from csv_utils import ingest_csv

# Constants

_CSV_DIR = os.getenv('POKEAPI_CSV_DIR', 'csv')

# Enum Maps

_SIMPLE_CSVS = {
    'evolution_chain': 'evolution_chains',
    'generation'     : 'generations',
    'region'         : 'regions',
    'version'        : 'versions',
    'version_group'  : 'version_groups',
}

_TYPE_ID_TO_ENUM = {
    '1' : 'Normal',
    '2' : 'Fighting',
    '3' : 'Flying',
    '4' : 'Poison',
    '5' : 'Ground',
    '6' : 'Rock',
    '7' : 'Bug',
    '8' : 'Ghost',
    '9' : 'Steel',
    '10': 'Fire',
    '11': 'Water',
    '12': 'Grass',
    '13': 'Electric',
    '14': 'Psychic',
    '15': 'Ice',
    '16': 'Dragon',
    '17': 'Dark',
    '18': 'Fairy',
    '19': 'Stellar',
    '10001': 'Unknown',
    '10002': 'Shadow',
}

_DAMAGE_CLASS_ID_TO_ENUM = {
    '1': 'Status',
    '2': 'Physical',
    '3': 'Special',
}

_GROWTH_RATE_ID_TO_ENUM = {
    '1': 'Slow',
    '2': 'Medium',
    '3': 'Fast',
    '4': 'MediumSlow',
    '5': 'SlowThenVeryFast',
    '6': 'FastThenVerySlow',
}

_ITEM_POCKET_ID_TO_ENUM = {
    '1': 'Misc',
    '2': 'Medicine',
    '3': 'Pokeballs',
    '4': 'Machines',
    '5': 'Berries',
    '6': 'Mail',
    '7': 'Battle',
    '8': 'Key',
}

_FLING_EFFECT_ID_TO_ENUM = {
    '1': 'BadPoison',
    '2': 'Burn',
    '3': 'BerryEffect',
    '4': 'HerbEffect',
    '5': 'Paralyze',
    '6': 'Poison',
    '7': 'Flinch',
}

_COLOR_ID_TO_ENUM = {
    '1' : 'Black',
    '2' : 'Blue',
    '3' : 'Brown',
    '4' : 'Gray',
    '5' : 'Green',
    '6' : 'Pink',
    '7' : 'Purple',
    '8' : 'Red',
    '9' : 'White',
    '10': 'Yellow',
}

_SHAPE_ID_TO_ENUM = {
    '1' : 'Ball',
    '2' : 'Squiggle',
    '3' : 'Fish',
    '4' : 'Arms',
    '5' : 'Blob',
    '6' : 'Upright',
    '7' : 'Legs',
    '8' : 'Quadruped',
    '9' : 'Wings',
    '10': 'Tentacles',
    '11': 'Heads',
    '12': 'Humanoid',
    '13': 'BugWings',
    '14': 'Armor',
}

_HABITAT_ID_TO_ENUM = {
    '1': 'Cave',
    '2': 'Forest',
    '3': 'Grassland',
    '4': 'Mountain',
    '5': 'Rare',
    '6': 'RoughTerrain',
    '7': 'Sea',
    '8': 'Urban',
    '9': 'WatersEdge',
}


# Utilities

def _run_ingest(conn, table_name, file_name, block):
    try:
        info(f'Ingesting {table_name}')
        block(conn, table_name, file_name)
        info(f'Done ingesting {table_name}')
    except Exception as e:
        error(f'Failed to ingest {table_name}: {e}')


def _get_csv_path(name):
    return pathlib.Path(_CSV_DIR) / f'{name}.csv'


def _ingest_simple_csv(conn, table_name, file_name):
    with _get_csv_path(file_name).open('r', encoding = 'utf-8') as f:
        ingest_csv(conn = conn, csv_source = f, table_name = table_name)


def _map_csv(file_name, headers, mapper):
    buffer = io.StringIO()
    writer = csv.writer(buffer)
    writer.writerow(headers)

    with _get_csv_path(file_name).open('r', encoding = 'utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            mapped = mapper(row)
            writer.writerow(mapped)

    buffer.seek(0)
    return buffer


def _load_static_type_lookup():
    pokemon_type_map = { }
    with _get_csv_path('pokemon_types').open('r', encoding = 'utf-8') as f:
        for row in csv.DictReader(f):
            pid = row['pokemon_id']
            slot = int(row['slot'])
            type_name = _TYPE_ID_TO_ENUM[row['type_id']]
            pokemon_type_map.setdefault(pid, []).append((slot, type_name))

    result = { }
    for pid, slot_types in pokemon_type_map.items():
        sorted_types = sorted(slot_types, key = lambda t: t[0])
        primary = sorted_types[0][1]
        secondary = sorted_types[1][1] if len(sorted_types) > 1 else None
        result[pid] = (primary, secondary)

    return result


def _int_or_none(value):
    return int(value) if value not in ('', '\\N', None) else None


# Ingestions

def _ingest_pokemon(conn, table_name, file_name):
    type_lookup = _load_static_type_lookup()

    buffer = _map_csv(
        file_name = file_name,
        headers = [
            'id', 'name', 'primaryType', 'secondaryType', 'species_id', 'height_dm', 'weight_hg',
            'base_experience', 'natural_order', 'is_default',
        ],
        mapper = lambda row:
        (
            int(row['id']),  # id
            row['identifier'],  # name
            *(type_lookup.get(row['id'], (None, None))),  # primaryType, secondaryType
            int(row['species_id']),  # species_id
            int(row['height']),  # height_dm
            int(row['weight']),  # weight_hg
            int(row['base_experience']),  # base_experience
            _int_or_none(row['order']),  # natural_order
            row['is_default'] == '1',  # is_default
        ),
    )

    ingest_csv(conn = conn, csv_source = buffer, table_name = table_name)


def _ingest_type_metadata(conn, table_name, file_name):
    buffer = _map_csv(
        file_name = file_name,
        headers = ['ptype', 'generation_id', 'damage_class'],
        mapper = lambda row:
        (
            _TYPE_ID_TO_ENUM[row['id']],
            int(row['generation_id']),
            _DAMAGE_CLASS_ID_TO_ENUM.get(row['damage_class_id'] or None),
        ),
    )

    ingest_csv(conn = conn, csv_source = buffer, table_name = table_name)


def _ingest_growth_rate_metadata(conn, table_name, file_name):
    # id,identifier,formula
    buffer = _map_csv(
        file_name = file_name,
        headers = ['rate', 'formula'],
        mapper = lambda row:
        (
            _GROWTH_RATE_ID_TO_ENUM[row['id']],
            row['formula'],
        ),
    )

    ingest_csv(conn = conn, csv_source = buffer, table_name = table_name)


def _ingest_item_categories(conn, table_name, file_name):
    buffer = _map_csv(
        file_name = file_name,
        headers = ['id', 'pocket', 'name'],
        mapper = lambda row:
        (
            int(row['id']),
            _ITEM_POCKET_ID_TO_ENUM[row['pocket_id']],
            row['identifier'],
        ),
    )

    ingest_csv(conn = conn, csv_source = buffer, table_name = table_name)


def _ingest_items(conn, table_name, file_name):
    buffer = _map_csv(
        file_name = file_name,
        headers = ['id', 'name', 'category_id', 'cost', 'fling_power', 'fling_effect'],
        mapper = lambda row:
        (
            int(row['id']),
            row['identifier'],
            int(row['category_id']),
            int(row['cost']),
            _int_or_none(row['fling_power']),
            _FLING_EFFECT_ID_TO_ENUM.get(row['fling_effect_id'] or None),
        ),
    )

    ingest_csv(conn = conn, csv_source = buffer, table_name = table_name)


def _ingest_species(conn, table_name, file_name):
    buffer = _map_csv(
        file_name = file_name,
        headers = [
            'id', 'name', 'generation_id', 'evolves_from', 'evolution_chain_id', 'color', 'shape', 'habitat',
            'gender_rate', 'capture_rate', 'base_happiness', 'is_baby', 'hatch_counter', 'has_gender_difference',
            'growth_rate', 'forms_switchable', 'is_legendary', 'is_mythical', 'natural_order', 'conquest_order',
        ],
        mapper = lambda row:
        (
            int(row['id']),
            row['identifier'],
            int(row['generation_id']),
            _int_or_none(row['evolves_from_species_id']),
            int(row['evolution_chain_id']),
            _COLOR_ID_TO_ENUM[row['color_id']],
            _SHAPE_ID_TO_ENUM[row['shape_id']],
            _HABITAT_ID_TO_ENUM.get(row['habitat_id'] or None),
            int(row['gender_rate']),
            int(row['capture_rate']),
            int(row['base_happiness']),
            row['is_baby'] == '1',
            int(row['hatch_counter']),
            row['has_gender_differences'] == '1',
            _GROWTH_RATE_ID_TO_ENUM[row['growth_rate_id']],
            row['forms_switchable'] == '1',
            row['is_legendary'] == '1',
            row['is_mythical'] == '1',
            int(row['order']),
            _int_or_none(row['conquest_order']),
        ),
    )

    ingest_csv(conn = conn, csv_source = buffer, table_name = table_name)


# Main function

def ingest_csv_files(conn):
    for table_name, file_name in _SIMPLE_CSVS.items():
        _run_ingest(
            conn = conn,
            table_name = table_name,
            file_name = file_name,
            block = _ingest_simple_csv,
        )

    _run_ingest(
        conn = conn,
        table_name = 'pokemon',
        file_name = 'pokemon',
        block = _ingest_pokemon,
    )

    _run_ingest(
        conn = conn,
        table_name = 'type_metadata',
        file_name = 'types',
        block = _ingest_type_metadata,
    )

    _run_ingest(
        conn = conn,
        table_name = 'growth_metadata',
        file_name = 'growth_rates',
        block = _ingest_growth_rate_metadata,
    )

    _run_ingest(
        conn = conn,
        table_name = 'item_category',
        file_name = 'item_categories',
        block = _ingest_item_categories,
    )

    _run_ingest(
        conn = conn,
        table_name = 'item',
        file_name = 'items',
        block = _ingest_items,
    )

    _run_ingest(
        conn = conn,
        table_name = 'species',
        file_name = 'pokemon_species',
        block = _ingest_species,
    )

    conn.commit()
