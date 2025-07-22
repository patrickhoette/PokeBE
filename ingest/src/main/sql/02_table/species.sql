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

CREATE TABLE IF NOT EXISTS species (
    id                    INTEGER     NOT NULL PRIMARY KEY,
    name                  TEXT        NOT NULL,
    generation_id         INTEGER     NOT NULL,
    evolves_from          INTEGER,
    evolution_chain_id    INTEGER     NOT NULL,
    color                 PCOLOR      NOT NULL,
    shape                 PSHAPE      NOT NULL,
    habitat               HABITAT,
    gender_rate           INTEGER     NOT NULL,
    capture_rate          INTEGER     NOT NULL,
    base_happiness        INTEGER     NOT NULL,
    is_baby               BOOLEAN     NOT NULL,
    hatch_counter         INTEGER     NOT NULL,
    has_gender_difference BOOLEAN     NOT NULL,
    growth_rate           GROWTH_RATE NOT NULL,
    forms_switchable      BOOLEAN     NOT NULL,
    is_legendary          BOOLEAN     NOT NULL,
    is_mythical           BOOLEAN     NOT NULL,
    natural_order         INTEGER     NOT NULL,
    conquest_order        INTEGER,
    CHECK ( gender_rate BETWEEN -1 AND 8 ),
    CHECK ( capture_rate BETWEEN 0 AND 255),
    CHECK ( base_happiness BETWEEN 0 AND 255),
    CHECK ( hatch_counter >= 0 )
);
