--liquibase formatted sql
CREATE TABLE Chats
(
	id serial,
	tg_chat_id bigint not null,
	PRIMARY KEY(id),
	UNIQUE(tg_chat_id)
)
