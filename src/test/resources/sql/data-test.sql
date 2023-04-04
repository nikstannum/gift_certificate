
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

