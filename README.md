java-explore-with-me

## Дипломный проект
### Приложение ExploreWithMe 

позволяет пользователям делиться информацией об интересных событиях и находить компанию для участия в них.

#### Два сервиса
- основной сервис (main-service) содержит необходимый функционал для работы продукта;
- сервис статистики (stats-service) позволяет делать различные выборки для анализа работы приложения.

#### API основного сервиса содержит три части:
  публичная: доступна без регистрации любому пользователю сети;
  закрытая: доступна только авторизованным пользователям;
  административная: для администраторов сервиса.

**Аутентификация и авторизация** <p>
Согласно описанию ТЗ сервисы работают внутри VPN.<p>
С внешним миром сервисы связывает сетевой шлюз. Он контактирует с системой аутентификации и авторизации, а затем перенаправляет запрос в сервисы. То есть, если шлюз пропустил запрос к закрытой или административной части API, значит, этот запрос успешно прошел аутентификацию и авторизацию.

#### схема БД основного сервиса:
https://dbdiagram.io/d/6488c6f4722eb77494e7c772
![QuickDBD-diagram3.png.png](/DBDiagram.io-diagram3.png)

### Выбранный дополнительный функционал этап 3 дипломного проекта
### Подписки. вариант 1
Возможность подписываться на других пользователей и получать список актуальных событий, опубликованных этими пользователями.

#### Сценарий использования:

1. Предусмотрено, что пользователь может запретить/разрешить подписку на себя
2. При создании пользователей администратором по умолчанию разрешение на подписку не активировано
3. подписка одним пользователем (follower) на другого пользователя (leader)

2. Навигация по подпискам

|                                        вариант а)                                         |                             вариант б)                             |
|:-----------------------------------------------------------------------------------------:|:------------------------------------------------------------------:|
| получение списка подписок (списка пользователей, на который подписан данный пользователь) | получение списка актуальных событий, опубликованных пользователями |
|      получение всех событий пользователя из списка (основной функционал по этапу 2)       |                                 -                                  |
|              Обращение к событию из списка (основной функционал по этапу 2)               |   Обращение к событию из списка (основной функционал по этапу 2)   |


3. возможность отписаться (получение списка подписок -> выбор подписки из списка подписок)
4. возможность получить список пользователей с наибольшим числом подписчиков

#### Энпойнты

1. получение информации о другом пользователе (приватный API)

```
GET /users/{userId}/info/{anotherUserId}
```

2. получение информации о пользователях с наибольшим числом подписок (приватный API)

```
GET /users/{userId}/leaders
```

3. Установка пользователем возможности другим пользователям подписываться на себя (приватный API, userId - зарегистрированный пользователь, subjId - идентификатор, который должен быть равен userId)

```
POST /users/{userId}/subscriptions/{subjectId}/set
```

4. Подписка (by followerId) на другого пользователя (with leaderId)

```
POST /users/{followerId}/subscriptions/{subjectId}
```

5. получение списка подписок пользователя (приватный API)

```
GET /users/{followerId}/subscriptions
```

* предполагается, что по нажатию на подписку в списке будет получен список подписок
* а в списке подписок может быть кнопа [отписаться]

6. отмена подписки на другого пользователя (приватный API)

```
DELETE /users/{followerId}/subscriptions/{subscriptionId}/cancel
```

7. получение списка актуальных событий по подпискам (приватный API)

```
GET /users/{followerId}/events/subscriptions
```
* пагинация
* сортировка по возрастанию даты (вверху самые ближайшие события)
* пометка, что события лидера имеют обновление

8. получение события лидера по идентификатору пользователем-подписчиком
```
GET /users/{userId}/events/subscriptions/{leaderId}/{eventId}
```
* получение события лидера по идентификатору пользователем-подписчиком
* снятие пометки наличия обновлений при просмотре для всей подписки

#### Базовые постман тесты (папка postman)