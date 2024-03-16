--liquibase formatted sql
CREATE TABLE IF NOT EXISTS subscriptions
(
	chat_id integer not null,
	link_id integer not null,
	PRIMARY KEY (chat_id, link_id),
    FOREIGN KEY (chat_id) REFERENCES chats (id),
    FOREIGN KEY (link_id) REFERENCES links (id) ON DELETE CASCADE
)
