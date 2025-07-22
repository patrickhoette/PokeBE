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
import psycopg2
import time
import pathlib
from urllib.parse import urlparse
from psycopg2 import OperationalError
from logger import debug, info, warning, error, critical


# Functions

def _parse_database_url():
    url = os.getenv("DATABASE_URL")
    if not url:
        raise RuntimeError("Missing DATABASE_URL env var")

    parsed = urlparse(url)
    return {
        'host'    : parsed.hostname,
        'port'    : parsed.port,
        'dbname'  : parsed.path.lstrip("/"),
        'user'    : parsed.username,
        'password': parsed.password,
    }


def _get_sql_src_dir():
    return pathlib.Path('./src/main/sql')


def _execute_sql_file(conn, rel_path):
    info(f'Executing {rel_path}...')
    try:
        with conn.cursor() as cur:
            with (_get_sql_src_dir() / rel_path).open('r', encoding = 'utf-8') as f:
                cur.execute(f.read())
    except Exception as e:
        error(f'Failed to execute {rel_path}: {e}')
    info(f'Done executing {rel_path}')


def _execute_sql_dir(conn, rel_path):
    sql_src_dir = _get_sql_src_dir()
    for file in (sql_src_dir / rel_path).glob('*.sql'):
        _execute_sql_file(conn = conn, rel_path = file.relative_to(sql_src_dir))


def connect_db(retries = 10, delay = 2):
    config = _parse_database_url()

    for attempt in range(retries):
        info(f'Attempting to connect to database, attempt {attempt + 1}/{retries}')
        try:
            conn = psycopg2.connect(**config)
            info('Connected to PostgreSQL')
            return conn
        except OperationalError as e:
            warning(f'Could not connect to DB sleeping for {delay} seconds: {e}')
            time.sleep(delay)

    raise RuntimeError('Could not connect to DB after several attempts.')


def setup_db(conn):
    info('Setting up database...')

    info('Creating extensions...')
    _execute_sql_file(conn = conn, rel_path = '00_extension.sql')
    info('Done creating extensions')

    info('Creating enums...')
    _execute_sql_file(conn = conn, rel_path = '01_type.sql')
    info('Done creating enums')

    info('Creating tables...')
    _execute_sql_dir(conn = conn, rel_path = '02_table')
    info('Done creating tables')

    conn.commit()
    info('Done setting up database')


def setup_post_ingest_db(conn):
    info('Setting up database post ingest...')

    info('Creating constraints...')
    _execute_sql_dir(conn = conn, rel_path = '03_constraint')
    info('Done creating constraints')

    info('Creating indexes...')
    _execute_sql_file(conn = conn, rel_path = '04_index.sql')
    info('Done creating indexes')

    info('Creating views...')
    _execute_sql_file(conn = conn, rel_path = '05_view.sql')
    info('Done creating views')

    info('Creating triggers...')
    _execute_sql_file(conn = conn, rel_path = '06_trigger.sql')
    info('Done creating triggers')

    info('Creating dev fixtures...')
    _execute_sql_file(conn = conn, rel_path = '07_dev_fixture.sql')
    info('Done creating dev fixtures')

    conn.commit()
    info('Done setting up database post ingest')
