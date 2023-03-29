# java-filmorate
Template repository for Filmorate project.

## DB for filmorate-project.
 ![DB for filmorate project.](../java-filmorate/DB.png)

 ### ��� ��������� �������� � ���� ������ ��� ��������� �� ����������:

_�������� ������ ������ ��� ������������ � User_ID = 1_
```
SELECT Friends_ID
FROM Friends
WHERE User_ID = 1 
AND Friends_Status LIKE 'confirmed';
```

_�������� ������ ����� ������ ��� ������������� � User_ID = 1 � User_ID = 2_
```
SELECT f.Friends_ID as common
FROM Friends AS f
WHERE User_ID = 2 
AND Friends_Status LIKE 'confirmed'
AND f.common IN (SELECT Friends_ID
                 FROM Friends
                 WHERE UserID = 1);
```

_�������� ���-�� ������ ��� ������ � Film_ID = 1_
```
SELECT COUNT(UserID)
FROM Likes_To_Film
WHERE Film_ID = 1;
```

_�������� ��� 5 ����������� ������_
```
SELECT Film_ID,
       COUNT(UserID) AS likes
FROM Likes_To_Film
GROUP BY Film_ID
ORDER BY likes DESC
LIMIT 5;
```