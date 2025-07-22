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
import sys
from csv_ingester import ingest_csv_files
from sprite_ingester import ingest_sprites
from logger import debug, info, warning, error, critical
from database_handler import connect_db, setup_db, setup_post_ingest_db


# Script

def main():
    sys.stdout.reconfigure(line_buffering = True)

    with connect_db() as conn:
        # Setup database
        setup_db(conn)

        # Ingest CSV Files
        try:
            info("Ingesting CSV files...")
            ingest_csv_files(conn)
        except Exception as e:
            error(f"Could not ingest CSV files: {e}")

        # Ingest Sprites
        try:
            info("Ingesting sprites...")
            ingest_sprites(conn)
        except Exception as e:
            error(f"Failed to ingest sprites: {e}")

        # Setup database post ingest
        setup_post_ingest_db(conn)


if __name__ == '__main__':
    main()
