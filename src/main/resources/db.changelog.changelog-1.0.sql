-- liquibase formatted sql

-- changeset create tables:add constraints

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

-- changeset splitStatements:false

create or replace function now_utc()
	RETURNS timestamp
		RETURN now() at time zone 'utc';

create or replace function trigger_update()
	RETURNS trigger AS $$
begin
	if NEW."name" != OLD."name"
	or NEW.description is DISTINCT FROM OLD.description
	or NEW.price is DISTINCT FROM OLD.price
	or NEW.duration != OLD.duration
	then
	update gift_certificate
	set last_update_date = now_utc()
	where id = OLD.id;
	end if;
	return new;
end;
$$ LANGUAGE 'plpgsql';

create or replace trigger last_update_date_changes
	after update
	on gift_certificate
	for each row
	EXECUTE procedure trigger_update();

-- changeset test data:add
INSERT INTO gift_certificate (name, description, price, duration)
VALUES
	('skydiving',
	'parachute jump from an airplane from a height of 2000 meters with an instructor',
	199.99,
	7),
	('massage',
	'back massage lasting 1 hour',
	10.00,
	14),
	('20 dollars for shopping',
	'20 dollars for shopping at the GREEN store',
	20.00,
	30),
	('gym membership',
	'monthly membership to the CROSSFIT gym',
	30.99,
	60),
	('a haircut',
	'haircut in the beauty salon "Camilla"',
	7.99,
	60);


INSERT INTO tag (name)
VALUES
	('male'),
	('female'),
	('extreme'),
	('beauty'),
	('health'),
	('money'),
	('sport');

INSERT INTO certificate_tag (certificate_id, tag_id)
VALUES
	((SELECT gc.id FROM	gift_certificate gc WHERE gc.description = 'parachute jump from an airplane from a height of 2000 meters with an instructor'),
	(SELECT	t.id FROM tag t WHERE	t.name = 'male')),
	((SELECT gc.id FROM	gift_certificate gc WHERE gc.description = 'parachute jump from an airplane from a height of 2000 meters with an instructor'),
	(SELECT	t.id FROM tag t WHERE	t.name = 'extreme')),
	((SELECT gc.id FROM gift_certificate gc WHERE gc.description = 'back massage lasting 1 hour'),
	(SELECT	t.id FROM tag t WHERE t.name = 'health')),
	((SELECT gc.id FROM	gift_certificate gc WHERE gc.description = '20 dollars for shopping at the GREEN store'),
	(SELECT	t.id FROM tag t WHERE t.name = 'money')),
	((SELECT gc.id FROM gift_certificate gc WHERE gc.description = 'monthly membership to the CROSSFIT gym'),
	(SELECT t.id FROM tag t WHERE t.name = 'sport')),
	((SELECT gc.id FROM gift_certificate gc WHERE gc.description = 'monthly membership to the CROSSFIT gym'),
	(SELECT t.id FROM tag t WHERE t.name = 'health')),
	((SELECT gc.id FROM gift_certificate gc WHERE gc.description = 'haircut in the beauty salon "Camilla"'),
	(SELECT t.id FROM tag t WHERE t.name = 'beauty'));

INSERT INTO "role" ("name")
VALUES ('ADMIN'),
		('MANAGER'),
		('USER');

INSERT INTO users (first_name, last_name, email, "password", role_id)
VALUES ('Nick', 'Johnson', 'johnson@gmail.us', 'qwerty', (SELECT r.role_id FROM "role" r WHERE r."name" = 'ADMIN')),
		('Mike', 'Scholz', 'scholz@yandex.ru', 'password', (SELECT r.role_id FROM "role" r WHERE r."name" = 'MANAGER')),
		('Joseph', 'Black', 'black@yandex.ru', 'hardpassword', (SELECT r.role_id FROM "role" r WHERE r."name" = 'MANAGER')),
		('Yuri', 'Ivanov', 'ivanov@gmail.com', 'zxcvb', (SELECT r.role_id FROM "role" r WHERE r."name" = 'USER')),
		('Peter', 'Mask', 'peter@gmail.com', 'vcxzb', (SELECT r.role_id FROM "role" r WHERE r."name" = 'USER')),
		('Valeriy', 'Fedorov', 'fedorov@gmail.com', 'fedorov123', (SELECT r.role_id FROM "role" r WHERE r."name" = 'USER'));


INSERT INTO status ("name")
VALUES ('PENDING'),
	('PAID'),
	('DELIVERED'),
	('CANCELED');

INSERT INTO orders (user_id, status_id, total_cost)
VALUES ((SELECT u.user_id FROM users u WHERE u.email = 'johnson@gmail.us'), (SELECT s.status_id FROM status s WHERE s."name" = 'PENDING'), 25.12),
		((SELECT u.user_id FROM users u WHERE u.email = 'johnson@gmail.us'), (SELECT s.status_id FROM status s WHERE s."name" = 'PENDING'), 12),
		((SELECT u.user_id FROM users u WHERE u.email = 'scholz@yandex.ru'), (SELECT s.status_id FROM status s WHERE s."name" = 'DELIVERED'), 55.00);

INSERT INTO order_infos (certificate_id, order_id, certificate_quantity, certificate_price)
VALUES 	((SELECT gc.id FROM gift_certificate gc WHERE gc.description = 'parachute jump from an airplane from a height of 2000 meters with an instructor'), (SELECT o.order_id FROM orders o WHERE o.total_cost = 25.12), 2, 9.00),
		((SELECT gc.id FROM gift_certificate gc WHERE gc.description = 'monthly membership to the CROSSFIT gym'), (SELECT o.order_id FROM orders o WHERE o.total_cost = 12), 1, 11.00),
		((SELECT gc.id FROM gift_certificate gc WHERE gc.description = '20 dollars for shopping at the GREEN store'), (SELECT o.order_id FROM orders o WHERE o.total_cost = 12), 1, 15.32);

SELECT setval('gift_certificate_id_seq', (SELECT max(id) FROM gift_certificate));
SELECT setval('tag_id_seq', (SELECT max(id) FROM tag));
SELECT setval('role_role_id_seq', (SELECT max(role_id) FROM role));
SELECT setval('users_user_id_seq', (SELECT max(user_id) FROM users));
SELECT setval('status_status_id_seq', (SELECT max(status_id) FROM status));
SELECT setval('orders_order_id_seq', (SELECT max(order_id) FROM orders));
SELECT setval('order_infos_order_infos_id_seq', (SELECT max(order_infos_id) FROM order_infos));
