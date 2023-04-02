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


