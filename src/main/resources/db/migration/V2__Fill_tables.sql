INSERT INTO BOOKSTORE (NAME)
VALUES ('Bloomsbury');

INSERT INTO ADDRESS (STREET, CITY, COUNTRY, BOOKSTORE_ID)
VALUES ('Charing Cross Road', 'London', 'Great Britain', 1);

UPDATE BOOKSTORE
SET ADDRESS_ID = 1
WHERE ID = 1;

INSERT INTO DEPARTMENT (NAME, BOOKSTORE_ID)
VALUES ('Fantasy', 1), ('Science', 1);

INSERT INTO BOOK (NAME, DEPARTMENT_ID)
VALUES ('Harry Potter', 1),
  ('Lord of the rings', 1),
  ('Cassandra', 2);

INSERT INTO AUTHOR (NAME)
VALUES ('J. K. Rowling'), 
('J. R. R. Tolkien'), 
('Carpenter Jeff'), 
('Hewitt Eben');

INSERT INTO BOOK_AUTHOR (BOOKS_ID, AUTHORS_ID)
VALUES (1,1), (2,2), (3,3), (3,4);