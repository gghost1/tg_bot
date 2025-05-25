# Telegram bot (test assigment)

## Описание
Бот создан с помощью библиотеки [telegram-bots-api](https://github.com/rubenlagus/TelegramBots):
```
<dependency>
    <groupId>org.telegram</groupId>
    <artifactId>telegrambots</artifactId>
    <version>6.9.7.1</version>
</dependency>
```
Также используется Spring Boot, Spring Data JPA, Liquibase, Lombok.

*Бот разработан с помощью базовых функицй библиотеки, что позволяет получить больше контроля над ботом, точно настроить все функции и легко протестировать их.*

В файле [application.properties](src/main/resources/application.properties) можно настроить параметры многопоточности обработки обновлений
```
bot.corePoolSize=10 - количество потоков
bot.maxPoolSize=20 - максимальное количество потоков
bot.queueCapacity=100 - размер очереди
```
**Основные функции бота протестированы.**

## Использование (потребуется запущенный Docker и интернет)
1. Склонировать репозиторий:
```
git clone https://github.com/gghost1/tg_bot.git
```
2. Создать файл .env в корне репозитория со следующим содержимым:
```
bot_token=<ваш токен>
POSTGRES_USER=<ваш логин от postgres>
POSTGRES_PASSWORD=<ваш пароль от postgres>
POSTGRES_DB=postgresql
```
3. Убедитесь, что порт 4000 свободен (туда прокидывается порт контейнера postgres)
4. Запустить docker-compose:
```
cd tg_bot
docker-compose up --build -d
```
5. Бот запущен и готов к работе
6. Чтобы завершить работу бота, выполните команду:
```
docker-compose down
```
7. Данные бота сохраняются в docker-volume (при удалении контейнера)