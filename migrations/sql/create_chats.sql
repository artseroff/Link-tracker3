--liquibase formatted sql
CREATE TABLE Chats
(
	id serial,
	chat_id bigint not null,
	PRIMARY KEY(id),
	UNIQUE(chat_id)
)
