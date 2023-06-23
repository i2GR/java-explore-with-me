DROP TABLE IF EXISTS stats;

CREATE TABLE stats (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    app varchar(32) NOT NULL,
    uri varchar(32) NOT NULL,
    ip varchar(39) NOT NULL,
    time_stamp TIMESTAMP
);