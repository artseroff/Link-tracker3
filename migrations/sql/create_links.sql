--liquibase formatted sql
CREATE TABLE IF NOT EXISTS links
(
	id bigserial,
	url text not null,
	last_updated_at timestamp with time zone,
	last_scheduler_check timestamp with time zone,
	PRIMARY KEY(id),
	UNIQUE(url)
)
