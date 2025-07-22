#  Copyright 2025 Patrick Hoette
#  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
#  documentation files (the “Software”), to deal in the Software without restriction, including without limitation
#  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
#  to permit persons to whom the Software is furnished to do so.
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
import os
import re
import shutil
import subprocess
import io
import csv
import pathlib
from typing import Optional, Tuple
from logger import debug, info, warning, error, critical
from csv_utils import ingest_csv

# Constants

_SPRITES_DIR = pathlib.Path(os.getenv('SPRITE_DIR', 'sprites'))

_IMAGE_GLOBS = ['*.png', '*.gif', '*.svg']

_FLAG_SHINY = 'is_shiny'
_FLAG_FEMALE = 'is_female'
_FLAG_BACK = 'is_back'
_FLAG_GREY = 'is_grey'
_FLAG_TRANSPARENT = 'is_transparent'
_FLAG_ANIMATED = 'is_animated'
_FLAG_LOW_RES = 'is_low_res'

_HANDLED_DIRS = {'versions', 'other'}


# Utilities

def _load_version_lookup():
    path = pathlib.Path('csv') / 'versions.csv'

    with path.open('r', encoding = 'utf-8') as f:
        reader = csv.DictReader(f)
        return { row['identifier']: int(row['id']) for row in reader }


def _extract_flags(parts):
    return {
        _FLAG_SHINY      : 'shiny' in parts,
        _FLAG_FEMALE     : 'female' in parts,
        _FLAG_BACK       : 'back' in parts,
        _FLAG_GREY       : 'grey' in parts,
        _FLAG_TRANSPARENT: 'transparent' in parts,
        _FLAG_ANIMATED   : 'animated' in parts,
        _FLAG_LOW_RES    : 'lowres' in parts,
    }


def _parse_id_and_variant(path: pathlib.Path) -> Optional[Tuple[int, Optional[str]]]:
    try:
        id_regex = re.compile(r'^(\d+)(?:[-_]?(.+))?$')

        match = id_regex.match(path.stem)

        if match is None:
            warning(f'Failed to parse id for {path}')
            return None
        else:
            pid, variant = match.groups()
            converted_pid = int(pid)

            if converted_pid <= 0:
                return None
            else:
                return converted_pid, variant or None

    except ValueError as e:
        warning(f'Failed to parse id for {path.name}: {e}')
        return None


def _is_valid_sprite(path: pathlib.Path) -> bool:
    try:
        parsed = _parse_id_and_variant(path)

        if parsed is None:
            return False
        else:
            return True
    except Exception:
        return False


def _itr_image_files(path):
    for suffix in _IMAGE_GLOBS:
        yield from path.glob(suffix)


# Functions

def _ensure_sprite_repo_cloned():
    if os.path.exists(_SPRITES_DIR) and os.listdir(_SPRITES_DIR):
        info('Sprite directory already exists, skipping clone')
        return

    info('Cloning sprite repository...')

    clone_dir = pathlib.Path('data') / 'repo'

    try:
        subprocess.run(
            [
                'git', 'clone',
                '--depth', '1',
                '--filter=blob:none',
                '--sparse',
                'https://github.com/PokeAPI/sprites',
                clone_dir,
            ], check = True,
        )

        subprocess.run(['git', '-C', clone_dir, 'sparse-checkout', 'init', '--cone'], check = True)
        subprocess.run(['git', '-C', clone_dir, 'sparse-checkout', 'set', 'sprites'], check = True)

        subprocess.run(f'mv {clone_dir}/sprites/* {_SPRITES_DIR}', shell = True, check = True)

        shutil.rmtree(clone_dir)

        info('Sprite repository cloned successfully')
    except subprocess.CalledProcessError as e:
        raise RuntimeError(f'Failed to clone sprite repo: {e}')


def _generate_sprites_csv():
    base_dir = _SPRITES_DIR / 'pokemon'

    buffer = io.StringIO()
    writer = csv.writer(buffer)

    writer.writerow(['path', 'pokemon_id', 'variant'])

    subrows = 0
    for child_dir in base_dir.glob('**/'):
        for file in _itr_image_files(child_dir):
            parsed = _parse_id_and_variant(file)

            if parsed is None:
                continue

            pid, variant = parsed
            if pid is None:
                continue

            path = file.relative_to(_SPRITES_DIR)

            writer.writerow([str(path), pid, variant if variant else ''])
            subrows += 1

    debug(f'Found {subrows} sprites')

    buffer.seek(0)
    return buffer


def _generate_official_artwork_csv():
    official_dir = _SPRITES_DIR / 'pokemon' / 'other' / 'official-artwork'
    shiny_dir = official_dir / 'shiny'

    buffer = io.StringIO()
    writer = csv.writer(buffer)
    writer.writerow(['sprite_path', 'is_shiny'])

    subrows = 0

    # Base
    for file in _itr_image_files(official_dir):
        if file.parent == shiny_dir:
            continue

        if not _is_valid_sprite(file):
            continue

        path = file.relative_to(_SPRITES_DIR)
        writer.writerow([str(path), False])
        subrows += 1

    # Shiny
    for file in _itr_image_files(shiny_dir):
        if not _is_valid_sprite(file):
            continue

        path = file.relative_to(_SPRITES_DIR)
        writer.writerow([str(path), True])
        subrows += 1

    debug(f'Found {subrows} official sprites')

    buffer.seek(0)
    return buffer


def _generate_default_sprite_csv():
    base_dir = _SPRITES_DIR / 'pokemon'

    buffer = io.StringIO()
    writer = csv.writer(buffer)
    writer.writerow(['sprite_path', 'is_shiny', 'is_female', 'is_back', 'is_low_res'])

    subrows = 0

    for child_dir in base_dir.glob('**/'):
        if any(part in _HANDLED_DIRS for part in child_dir.relative_to(_SPRITES_DIR).parts):
            continue

        for file in _itr_image_files(child_dir):
            if not _is_valid_sprite(file):
                continue

            path = file.relative_to(_SPRITES_DIR)

            flags = _extract_flags(path.parts)

            writer.writerow(
                [
                    str(path),
                    flags[_FLAG_SHINY],
                    flags[_FLAG_FEMALE],
                    flags[_FLAG_BACK],
                    flags[_FLAG_LOW_RES],
                ]
            )
            subrows += 1

    debug(f'Found {subrows} default sprites')

    buffer.seek(0)
    return buffer


def _generate_misc_sprite_csv():
    base_dir = _SPRITES_DIR / 'pokemon' / 'other'

    buffer = io.StringIO()
    writer = csv.writer(buffer)
    writer.writerow(['sprite_path', 'category', 'is_female', 'is_shiny', 'is_back'])

    subrows = 0
    for child_dir in base_dir.glob('**/'):
        if child_dir.name == 'official-artwork':
            continue

        for file in _itr_image_files(child_dir):
            if not _is_valid_sprite(file):
                continue

            base_dir_rel_path = file.relative_to(base_dir)
            flags = _extract_flags(base_dir_rel_path.parts)
            path = file.relative_to(_SPRITES_DIR)

            writer.writerow(
                [
                    str(path),
                    base_dir_rel_path.parts[0],
                    flags[_FLAG_FEMALE],
                    flags[_FLAG_SHINY],
                    flags[_FLAG_BACK],
                ],
            )
            subrows += 1

    debug(f'Found {subrows} misc sprites')

    buffer.seek(0)
    return buffer


def _generate_version_sprite_csv():
    version_lookup = _load_version_lookup()
    version_names = sorted(version_lookup.keys(), key = len, reverse = True)
    version_regex = re.compile('|'.join(re.escape(v) for v in version_names))

    base_dir = _SPRITES_DIR / 'pokemon' / 'versions'

    buffer = io.StringIO()
    writer = csv.writer(buffer)
    writer.writerow(
        [
            'sprite_path', 'version_id', 'is_shiny', 'is_female', 'is_back',
            'is_grey', 'is_animated', 'is_transparent',
        ],
    )

    subrows = 0
    for version_dir in base_dir.glob('generation*/*/'):
        if version_dir.name == 'icons':
            continue

        versions = version_regex.findall(version_dir.name.lower())

        if not versions:
            warning(f'Unknown version name for path: {version_dir}')
            continue

        version_ids = [version_lookup[version] for version in versions]

        for file in _itr_image_files(version_dir):
            if not _is_valid_sprite(file):
                continue

            flags = _extract_flags(file.relative_to(version_dir).parts)
            path = file.relative_to(_SPRITES_DIR)

            for version_id in version_ids:
                writer.writerow(
                    [
                        str(path),
                        version_id,
                        flags[_FLAG_SHINY],
                        flags[_FLAG_FEMALE],
                        flags[_FLAG_BACK],
                        flags[_FLAG_GREY],
                        flags[_FLAG_ANIMATED],
                        flags[_FLAG_TRANSPARENT],
                    ],
                )
                subrows += 1

    debug(f'Found {subrows} version sprites')

    buffer.seek(0)
    return buffer


# Main Function

def ingest_sprites(conn):
    _ensure_sprite_repo_cloned()

    sprite_buffer = None
    try:
        info('Parsing sprites')
        sprite_buffer = _generate_sprites_csv()
        info('Done parsing sprites')
    except Exception as e:
        error(f'Failed to parse sprites: {e}')

    official_buffer = None
    try:
        info('Parsing official artwork')
        official_buffer = _generate_official_artwork_csv()
        info('Done parsing official artwork')
    except Exception as e:
        error(f'Failed to parse official artwork: {e}')

    default_buffer = None
    try:
        info('Parsing default artwork')
        default_buffer = _generate_default_sprite_csv()
        info('Done parsing default artwork')
    except Exception as e:
        error(f'Failed to parse default artwork: {e}')

    misc_buffer = None
    try:
        info('Parsing misc artwork')
        misc_buffer = _generate_misc_sprite_csv()
        info('Done parsing misc artwork')
    except Exception as e:
        error(f'Failed to parse misc artwork: {e}')

    versions_buffer = None
    try:
        info('Parsing version artwork')
        versions_buffer = _generate_version_sprite_csv()
        info('Done parsing version artwork')
    except Exception as e:
        error(f'Failed to parse version artwork: {e}')

    try:
        if sprite_buffer:
            info('Inserting sprites')
            ingest_csv(conn = conn, csv_source = sprite_buffer, table_name = 'pokemon_sprite')
            info('Done inserting sprites')
        else:
            warning('No sprites to insert, skipping')
    except Exception as e:
        error(f'Failed to insert sprites: {e}')

    try:
        if official_buffer:
            info('Inserting official artwork')
            ingest_csv(
                conn = conn,
                csv_source = official_buffer,
                table_name = 'pokemon_official_sprite',
                has_generated_primary = True,
            )
            info('Done inserting official artwork')
        else:
            warning('No official artwork to insert, skipping')
    except Exception as e:
        error(f'Failed to insert official artwork: {e}')

    try:
        if default_buffer:
            info('Inserting default artwork')
            ingest_csv(
                conn = conn,
                csv_source = default_buffer,
                table_name = 'pokemon_default_sprite',
                has_generated_primary = True,
            )
            info('Done inserting default artwork')
        else:
            warning('No default artwork to insert, skipping')
    except Exception as e:
        error(f'Failed to insert default artwork: {e}')

    try:
        if misc_buffer:
            info('Inserting misc artwork')
            ingest_csv(
                conn = conn,
                csv_source = misc_buffer,
                table_name = 'pokemon_misc_sprite',
                has_generated_primary = True,
            )
            info('Done inserting misc artwork')
        else:
            warning('No misc artwork to insert, skipping')
    except Exception as e:
        error(f'Failed to insert misc artwork: {e}')

    try:
        if versions_buffer:
            info('Inserting version artwork')
            ingest_csv(
                conn = conn,
                csv_source = versions_buffer,
                table_name = 'pokemon_version_sprite',
                has_generated_primary = True,
            )
            info('Done inserting version artwork')
        else:
            warning('No version artwork to insert, skipping')
    except Exception as e:
        error(f'Failed to insert version artwork: {e}')

    conn.commit()
