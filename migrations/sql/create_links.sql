--liquibase formatted sql
CREATE TABLE Links
(
	id serial,
	url character varying not null,
	last_updated_at timestamp with time zone not null,
	last_scheduler_check timestamp with time zone not null,
	PRIMARY KEY(id),
	UNIQUE(url)
)
