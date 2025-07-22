CREATE TABLE IF NOT EXISTS item (
    id           INTEGER      NOT NULL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    category_id  INTEGER      NOT NULL,
    cost         INTEGER      NOT NULL,
    fling_power  INTEGER,
    fling_effect FLING_EFFECT
);

CREATE TABLE IF NOT EXISTS item_category (
    id     INTEGER     NOT NULL PRIMARY KEY,
    pocket ITEM_POCKET NOT NULL,
    name   VARCHAR(100)
);
