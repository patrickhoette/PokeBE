CREATE TABLE IF NOT EXISTS pokemon (
    id              INTEGER     NOT NULL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    species_id      INTEGER     NOT NULL,
    height_dm       INTEGER     NOT NULL,
    weight_hg       INTEGER     NOT NULL,
    base_experience INTEGER     NOT NULL,
    natural_order   INTEGER     NOT NULL,
    is_default      BOOLEAN     NOT NULL
);
