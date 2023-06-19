DROP TABLE IF EXISTS compilations_events_ewm;
DROP TABLE IF EXISTS compilations_ewm;
DROP TABLE IF EXISTS requests_ewm;
DROP TABLE IF EXISTS events_ewm;
DROP TABLE IF EXISTS users_ewm;
DROP TABLE IF EXISTS categories_ewm;


CREATE TABLE users_ewm (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY, -- H2
    --id BIGSERIAL, -- Postgres
    email varchar(254) UNIQUE NOT NULL,
    name varchar(250) UNIQUE NOT NULL,
    CONSTRAINT chk_users_email_min CHECK(length(email) >= 6),
    CONSTRAINT chk_users_name_min CHECK(length(name) >= 2)
);

CREATE TABLE categories_ewm (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY, -- H2
    --id BIGSERIAL, -- Postgres
    name varchar(50) UNIQUE NOT NULL,
   CONSTRAINT chk_categories_name_min CHECK(length(name) >= 1)
);

CREATE TABLE events_ewm (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY, -- H2
    --id BIGSERIAL, -- Postgres
    title varchar(120) NOT NULL,
    annotation varchar(2000) NOT NULL,
    description varchar(7000) NOT NULL,
    event_date TIMESTAMP (3) WITHOUT TIME ZONE,
    category_id BIGINT,
    initiator_id BIGINT,
    location_lat FLOAT NOT NULL,
    location_lon FLOAT NOT NULL,
    created TIMESTAMP (3) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    paid BOOLEAN DEFAULT TRUE NOT NULL,
    moderation BOOLEAN DEFAULT TRUE,
    part_limit BIGINT DEFAULT 0,
    published TIMESTAMP (3) WITHOUT TIME ZONE,
    state varchar(32) NOT NULL,
    CONSTRAINT fk_events_to_categories FOREIGN KEY(category_id) REFERENCES categories_ewm(id),
    CONSTRAINT fk_events_to_users FOREIGN KEY(initiator_id) REFERENCES users_ewm(id),
    CONSTRAINT chk_events_title_min CHECK(length(title) >= 3),
    CONSTRAINT chk_events_description_min CHECK(length(description) >= 20),
    CONSTRAINT chk_events_annotation_min CHECK(length(annotation) >= 20)
);

CREATE TABLE compilations_ewm (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY, -- H2
    --id BIGSERIAL, -- Postgres
    pinned BOOLEAN DEFAULT FALSE,
    title varchar(50) NOT NULL,
    CONSTRAINT chk_compilations_title_min CHECK(length(title) >= 1)
);

CREATE TABLE requests_ewm (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY, -- H2
    --id BIGSERIAL, -- Postgres
    created TIMESTAMP (3) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    event_id BIGINT,
    owner_id BIGINT,
    requester_id BIGINT,
    status varchar(32) NOT NULL,
    CONSTRAINT fk_requests_to_events FOREIGN KEY(event_id) REFERENCES events_ewm(id),
    CONSTRAINT fk_requests_owner_to_users FOREIGN KEY(owner_id) REFERENCES users_ewm(id),
    CONSTRAINT fk_requests_requester_to_users FOREIGN KEY(requester_id) REFERENCES users_ewm(id),
    CONSTRAINT unique_request UNIQUE (requester_id, event_id)
);

CREATE TABLE compilations_events_ewm (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY, -- H2
    --id BIGSERIAL, -- Postgres
    event_id BIGINT,
    compilation_id BIGINT,
    constraint fk_compilations_events_to_events FOREIGN KEY(event_id) REFERENCES events_ewm(id) ON DELETE CASCADE,
    constraint fk_compilations_events_to_compilations FOREIGN KEY(compilation_id) REFERENCES compilations_ewm(id) ON DELETE CASCADE
);