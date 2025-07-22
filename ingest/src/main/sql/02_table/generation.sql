CREATE TABLE IF NOT EXISTS generation (
    id             INTEGER     NOT NULL PRIMARY KEY,
    main_region_id INTEGER     NOT NULL,
    name           VARCHAR(50) NOT NULL
);
