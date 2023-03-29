# java-filmorate

Template repository for Filmorate project.

## DB for filmorate-project.

![DB for filmorate project.](./DB.png)

### Вот несколько запросов к базе данных для понимания ее устройства:

_Получить список друзей для пользователя с User_ID = 1_

```
SELECT Friends_ID
FROM Friends
WHERE User_ID = 1 
AND Friends_Status LIKE 'confirmed';
```

_Получить список общих друзей для пользователей с User_ID = 1 и User_ID = 2_

```
SELECT f.Friends_ID as common
FROM Friends AS f
WHERE User_ID = 2 
AND Friends_Status LIKE 'confirmed'
AND f.common IN (SELECT Friends_ID
                 FROM Friends
                 WHERE UserID = 1);
```

_Получить кол-во лайков для фильма с Film_ID = 1_

```
SELECT COUNT(UserID)
FROM Likes_To_Film
WHERE Film_ID = 1;
```

_Получить ТОП 5 залайканных фильма_

```
SELECT Film_ID,
       COUNT(UserID) AS likes
FROM Likes_To_Film
GROUP BY Film_ID
ORDER BY likes DESC
LIMIT 5;
```