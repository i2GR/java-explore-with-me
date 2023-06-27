DROP TABLE IF EXISTS compilations_events;
DROP TABLE IF EXISTS compilations;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS subscriptions;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS categories;


CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(254) UNIQUE NOT NULL,
    name varchar(250) UNIQUE NOT NULL,
    subscriptions_allowed BOOLEAN NOT NULL,
    CONSTRAINT chk_users_email_min CHECK(length(email) >= 6),
    CONSTRAINT chk_users_name_min CHECK(length(name) >= 2)
);

CREATE TABLE categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(50) UNIQUE NOT NULL,
   CONSTRAINT chk_categories_name_min CHECK(length(name) >= 1)
);

CREATE TABLE events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
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
    part_limit INTEGER DEFAULT 0,
    published TIMESTAMP (3) WITHOUT TIME ZONE,
    state varchar(32) NOT NULL,
    CONSTRAINT fk_events_to_categories FOREIGN KEY(category_id) REFERENCES categories(id),
    CONSTRAINT fk_events_to_users FOREIGN KEY(initiator_id) REFERENCES users(id),
    CONSTRAINT chk_events_title_min CHECK(length(title) >= 3),
    CONSTRAINT chk_events_description_min CHECK(length(description) >= 20),
    CONSTRAINT chk_events_annotation_min CHECK(length(annotation) >= 20)
);

CREATE TABLE compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN DEFAULT FALSE,
    title varchar(50) NOT NULL,
    CONSTRAINT chk_compilations_title_min CHECK(length(title) >= 1)
);

CREATE TABLE requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created TIMESTAMP (3) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    event_id BIGINT,
    owner_id BIGINT,
    requester_id BIGINT,
    status varchar(32) NOT NULL,
    CONSTRAINT fk_requests_to_events FOREIGN KEY(event_id) REFERENCES events(id),
    CONSTRAINT fk_requests_owner_to_users FOREIGN KEY(owner_id) REFERENCES users(id),
    CONSTRAINT fk_requests_requester_to_users FOREIGN KEY(requester_id) REFERENCES users(id),
    CONSTRAINT unique_request UNIQUE (requester_id, event_id)
);

CREATE TABLE compilations_events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id BIGINT,
    compilation_id BIGINT,
    CONSTRAINT fk_compilations_events_to_events FOREIGN KEY(event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_compilations_events_to_compilations FOREIGN KEY(compilation_id) REFERENCES compilations(id) ON DELETE CASCADE
);

CREATE TABLE subscriptions (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  follower_id BIGINT,
  leader_id BIGINT,
  last_view TIMESTAMP (3) WITHOUT TIME ZONE,
  CONSTRAINT fk_subscriptions_follower_to_users FOREIGN KEY (follower_id) REFERENCES users(id),
  CONSTRAINT fk_subscriptions_leader_to_users FOREIGN KEY (leader_id) REFERENCES users(id),
  CONSTRAINT fk_subscriptions_unq UNIQUE (follower_id, leader_id),
  CONSTRAINT subs_chk CHECK (follower_id <> leader_id)
);