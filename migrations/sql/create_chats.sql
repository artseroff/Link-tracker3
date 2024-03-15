--liquibase formatted sql
CREATE TABLE IF NOT EXISTS chats
(
	id bigint not null,
	PRIMARY KEY(id)
)
