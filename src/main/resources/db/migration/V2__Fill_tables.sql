INSERT INTO BOOKSTORE (NAME)
VALUES ('Bloomsbury');

INSERT INTO ADDRESS (STREET, CITY, COUNTRY, BOOKSTORE_ID)
VALUES ('Charing Cross Road', 'London', 'Great Britain', 1);

UPDATE BOOKSTORE
SET ADDRESS_ID = 1
WHERE ID = 1;

INSERT INTO BOOK (NAME, PRICE, GENRE, BOOKSTORE_ID)
VALUES ('Harry Potter', 10, 'FANTASY', 1),
  ('Lord of the rings', 5, 'FANTASY', 1),
  ('Cassandra', 20, 'SCIENCE', 1);

INSERT INTO AUTHOR (NAME)
VALUES ('J. K. Rowling'), 
('J. R. R. Tolkien'), 
('Carpenter Jeff'), 
('Hewitt Eben');

INSERT INTO BOOK_AUTHOR (BOOKS_ID, AUTHORS_ID)
VALUES (1,1), (2,2), (3,3), (3,4);