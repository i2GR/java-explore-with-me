DROP TABLE IF EXISTS stats;

CREATE TABLE stats (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    --id BIGSERIAL, -- Postgres
    app varchar(32) NOT NULL,
    uri varchar(32) NOT NULL,
    -- ipv4 INET -- Postgres specific -> check
    ip varchar(39) NOT NULL,
    time_stamp TIMESTAMP--,
    --CONSTRAINT stats_pk PRIMARY KEY (id)
);