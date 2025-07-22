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
from logger import debug, info, warning, error, critical

# Functions


def ingest_csv(conn, csv_source, table_name, has_generated_primary = False):
    tmp_table_name = f'tmp_{table_name}'

    with conn.cursor() as cur:
        cur.execute(
            f"""
            CREATE TEMP TABLE {tmp_table_name} (LIKE {table_name} INCLUDING ALL);
            """
        )

        if has_generated_primary:
            csv_source.seek(0)
            headers = csv_source.readline().strip()
            csv_source.seek(0)

            cur.copy_expert(
                f"""
                COPY {tmp_table_name} ({headers}) FROM STDIN WITH CSV HEADER;
                """,
                csv_source,
            )

            cur.execute(
                f"""
                INSERT INTO {table_name}
                OVERRIDING SYSTEM VALUE
                SELECT * FROM {tmp_table_name}
                ON CONFLICT DO NOTHING;
                """
            )
        else:
            cur.copy_expert(
                f"""
                COPY {tmp_table_name} FROM STDIN WITH CSV HEADER;
                """,
                csv_source,
            )

            cur.execute(
                f"""
                INSERT INTO {table_name}
                SELECT * FROM {tmp_table_name}
                ON CONFLICT DO NOTHING;
                """
            )
