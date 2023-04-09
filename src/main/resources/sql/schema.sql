CREATE DATABASE certificate;

CREATE TABLE IF NOT EXISTS gift_certificate (
	id BIGSERIAL PRIMARY KEY,
	"name" VARCHAR (30) NOT NULL,
	description VARCHAR (150) UNIQUE,
	price NUMERIC (6,2) CHECK (price > 0),
	duration SMALLINT CHECK (duration > 0),
	create_date TIMESTAMP (3) WITHOUT time ZONE DEFAULT (now() at time zone 'utc'),
	last_update_date TIMESTAMP (3) WITHOUT time ZONE DEFAULT (now() at time zone 'utc')
);

CREATE TABLE IF NOT EXISTS tag (
	id BIGSERIAL PRIMARY KEY,
	"name" VARCHAR (30) UNIQUE
);

CREATE TABLE IF NOT EXISTS certificate_tag (
	certificate_id BIGINT NOT NULL REFERENCES gift_certificate,
	tag_id BIGINT NOT NULL REFERENCES tag,
	UNIQUE (certificate_id, tag_id)
);

CREATE TABLE IF NOT EXISTS "role" (
	role_id BIGSERIAL PRIMARY KEY,
	"name" VARCHAR (10) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
	user_id BIGSERIAL PRIMARY KEY,
	first_name VARCHAR (30),
	last_name VARCHAR (30),
	email VARCHAR (40) NOT NULL,
	"password" VARCHAR (40) NOT NULL,
	role_id BIGINT NOT NULL REFERENCES ROLE,
	deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS status (
	status_id BIGSERIAL PRIMARY KEY,
	"name" VARCHAR (20) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
	order_id BIGSERIAL PRIMARY KEY,
	user_id BIGINT NOT NULL REFERENCES users,
	status_id BIGINT REFERENCES status,
	total_cost NUMERIC (10,2),
	deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS order_infos (
	order_infos_id BIGSERIAL PRIMARY KEY,
	certificate_id BIGINT NOT NULL REFERENCES gift_certificate,
	order_id BIGINT NOT NULL REFERENCES orders,
	certificate_quantity BIGINT,
	certificate_price NUMERIC (6,2),
	create_date TIMESTAMP (3) WITHOUT time ZONE DEFAULT (now() at time zone 'utc'),
	deleted BOOLEAN NOT NULL DEFAULT false
);


CREATE OR REPLACE FUNCTION now_utc()
	RETURNS timestamp
		RETURN now() at time zone 'utc';

CREATE OR REPLACE FUNCTION trigger_update()
	RETURNS TRIGGER AS $$
BEGIN
	IF NEW."name" != OLD."name"
	OR NEW.description IS DISTINCT FROM OLD.description
	OR NEW.price IS DISTINCT FROM OLD.price
	OR NEW.duration != OLD.duration
	THEN
	UPDATE gift_certificate
	SET last_update_date = now_utc()
	WHERE id = OLD.id;
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE TRIGGER last_update_date_changes
	AFTER UPDATE
	ON gift_certificate
	FOR EACH ROW
	EXECUTE PROCEDURE trigger_update();


