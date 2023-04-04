CREATE TABLE IF NOT EXISTS gift_certificate (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR (30) NOT NULL,
	description VARCHAR (150) UNIQUE,
	price NUMERIC (6,2) CHECK (price > 0),
	duration SMALLINT CHECK (duration > 0),
	create_date TIMESTAMP (3) WITHOUT time ZONE DEFAULT (now()),
	last_update_date TIMESTAMP (3) WITHOUT time ZONE DEFAULT (now())
);

CREATE TABLE IF NOT EXISTS tag (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR (30) UNIQUE
);

CREATE TABLE IF NOT EXISTS certificate_tag (
	certificate_id BIGINT NOT NULL REFERENCES gift_certificate,
	tag_id BIGINT NOT NULL REFERENCES tag,
	UNIQUE (certificate_id, tag_id)
);