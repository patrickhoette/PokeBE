CREATE TABLE IF NOT EXISTS type_metadata (
    ptype         PTYPE        NOT NULL PRIMARY KEY,
    generation_id INTEGER      NOT NULL,
    damage_class  DAMAGE_CLASS NOT NULL
);
