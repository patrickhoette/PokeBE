CREATE TABLE IF NOT EXISTS version (
    id               INTEGER     NOT NULL PRIMARY KEY,
    version_group_id INTEGER     NOT NULL,
    name             VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS version_group (
    id            INTEGER     NOT NULL PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    generation_id INTEGER     NOT NULL,
    natural_order INTEGER     NOT NULL
);
