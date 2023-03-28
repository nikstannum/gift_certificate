DROP DATABASE certificate;

DROP TABLE IF EXISTS certificate_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS gift_certificate;

DROP FUNCTION IF EXISTS now_utc() CASCADE;
DROP FUNCTION IF EXISTS trigger_update() CASCADE;
DROP TRIGGER IF EXISTS last_update_date_changes ON gift_certificate CASCADE