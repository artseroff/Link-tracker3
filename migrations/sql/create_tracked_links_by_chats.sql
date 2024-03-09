--liquibase formatted sql
CREATE TABLE tracked_links_by_chats
(
	chat_id integer not null,
	link_id integer not null,
	PRIMARY KEY (chat_id, link_id)
)
