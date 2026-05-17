# java-explore-with-me (Афиша). 
В этом приложение можно предложить какое-либо событие от выставки до похода в кино и собрать компанию для участия в нём.

Структура проекта с отметками о выполнение:

<pre>
<span style="color: #0969da; font-weight: bold;">   ExploreWithMe (Root Project)</span>
<span style="color: #cf222e; font-weight: bold;">   -├── ewms-service (Main Service — Бизнес-логика)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   ├── pom.xml (Зависит от stats-client)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   ├── src/main/java/ru/practicum/explore</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   ├── categories (Категории событий)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── controller</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   │   ├── AdminCategoryController.java (POST, PATCH, DELETE /admin/categories)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   │   └── PublicCategoryController.java (GET /categories, GET /categories/{catId})</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── model/Category.java (@Entity)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   └── service/CategoryServiceImpl.java</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   ├── events (События)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── controller</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   │   ├── AdminEventController.java (GET, PATCH /admin/events)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   │   ├── PrivateEventController.java (GET, POST, PATCH /users/{userId}/events)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   │   └── PublicEventController.java (GET /events, GET /events/{id})</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── model/Event.java (@Entity), State.java (Enum), Location.java</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   └── service/EventServiceImpl.java (Логика + вызовы StatsClient)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   ├── users (Пользователи)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── controller/AdminUserController.java (GET, POST, DELETE /admin/users)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── model/User.java (@Entity)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── dto/UserDto.java, UserMapper.java
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── repository/UserRepository.java
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   └── service/UserService.java
<span style="color: #cf222e; font-weight: bold;">   -│   │   ├── requests (Заявки на участие)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── controller/PrivateRequestController.java (GET, POST, PATCH /users/{userId}/requests)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   └── model/ParticipationRequest.java (@Entity)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   ├── compilations (Подборки событий)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   ├── controller/AdminCompController.java & PublicCompController.java</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   │   └── model/Compilation.java (@Entity)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │   └── exception</span>
<span style="color: #cf222e; font-weight: bold;">   -│   │       └── ErrorHandler.java (@RestControllerAdvice)</span>
<span style="color: #cf222e; font-weight: bold;">   -│   └── src/main/resources</span>
<span style="color: #cf222e; font-weight: bold;">   -│       ├── schema.sql (Схема БД: users, categories, events, requests, compilations)</span>
<span style="color: #cf222e; font-weight: bold;">   -│       └── application.properties (Настройки порта 8080 и подключения к DB)</span>
<span style="color: #cf222e; font-weight: bold;">   -│</span>
<span style="color: #0969da; font-weight: bold;">   +├── stats-server (Микросервис статистики)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   ├── pom.xml (Агрегатор модулей статистики: dto, client, server)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   ├── stats-dto (Общий модуль с объектами передачи данных)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │   ├── pom.xml</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │   └── src/main/java/ru/practicum/dto</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │       ├── EndpointHitDto.java (app, uri, ip, timestamp)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │       └── ViewStatsDto.java (app, uri, hits)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   ├── stats-client (Библиотека-клиент для Main Service)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │   ├── pom.xml (Зависит от stats-dto)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │   └── src/main/java/ru/practicum/explore/client</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │       └── StatsClient.java (Методы hit() и getStats() через RestTemplate)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   │</span>
<span style="color: #2ea44f; font-weight: bold;">   +│   └── stats-server (Сервер сбора и хранения данных)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       ├── pom.xml (Зависит от stats-dto)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       ├── src/main/java/ru/practicum/explore/stats</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       │   ├── StatsController.java</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       │   │   ├── POST /hit (Регистрация просмотра)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       │   │   └── GET /stats (Получение статистики: start, end, uris, unique)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       │   ├── EndpointHitMapper.java</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       │   ├── EndpointHit.java (@Entity)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       │   ├── StatsRepository.java (Native/JPQL Query для агрегации)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       │   └── StatsServiceImpl.java</span>
<span style="color: #2ea44f; font-weight: bold;">   +│       └── src/main/resources</span>
<span style="color: #2ea44f; font-weight: bold;">   +│           ├── schema.sql (Таблица hits)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│           └── application.properties (Настройки порта 9090)</span>
<span style="color: #2ea44f; font-weight: bold;">   +│</span>
<span style="color: #2ea44f; font-weight: bold;">   +├── pom.xml (Parent POM: управление версиями зависимостей и модулями)</span>
<span style="color: #2ea44f; font-weight: bold;">   +└── docker-compose.yml (Инфраструктура: ewm-service, stats-server, 2 БД PostgreSQL)</span>

</pre>
