![Bot](https://github.com/sanyarnd/java-course-2023-backend-template/actions/workflows/bot.yml/badge.svg)
![Scrapper](https://github.com/sanyarnd/java-course-2023-backend-template/actions/workflows/scrapper.yml/badge.svg)

# Link Tracker

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram

Проект написан на Java 21 с использованием Spring Boot 3 на основе микросервисной архитектуры. Docker-образы сервисов опубликованы в GitHub Container Registry

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Scrapper взаимодействует с БД, где хранится информация о пользователях и отслеживаемых ссылках. Периодически он проверяет ссылки на наличие обновлений и посылает их в Bot, а он отправляет сообщение в чат. Пользователь Telegram чата через Bot отправляет в Scrapper запрос о подписке или отмене отслеживания ссылки.

- Доступ к БД PostgreSQL возможен через JDBC / JPA / JOOQ
- Обмен сообщений сервисов возможен через протокол Http / очередь сообщений Kafka
- Реализованы механизмы Retry и Rate Limiting
- Документация к API Scrapper и Bot доступна через Swagger UI
- Проведено тестирование с помощью JUnit 5, Mockito, WireMock, Testcontainers
- Метрики приложений доступны через Prometheus

# Скриншоты
Функции бота:

![image](https://github.com/user-attachments/assets/d2194ec3-5b10-414e-bbd0-a965d45c0107)

Подписка на репозиторий Github:

![github](https://github.com/user-attachments/assets/62cdba0f-53fa-40c9-85ea-e9114384f3fa)

Обновление вопроса на Stack Overflow:

![sof smaller](https://github.com/user-attachments/assets/b98a27ec-b9f6-4514-b0cc-21454d25783c)

Список отслеживаемых ссылок:

![list](https://github.com/user-attachments/assets/462d5038-0261-435e-a871-c900bc159272)
