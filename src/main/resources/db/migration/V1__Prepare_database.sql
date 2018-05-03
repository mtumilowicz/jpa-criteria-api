create table BOOKSTORE (
    ID int auto_increment primary key,
    NAME varchar(100) not null unique,
    ADDRESS_ID INT,
);

create table ADDRESS (
    ID int auto_increment primary key,
    STREET varchar(100),
    CITY varchar(100),
    COUNTRY varchar(100),
    BOOKSTORE_ID INT,
    foreign key (BOOKSTORE_ID) references BOOKSTORE(ID),
);

alter table BOOKSTORE add foreign key (ADDRESS_ID) references ADDRESS(ID);

create table BOOK (
    ID int auto_increment primary key,
    TITLE varchar(100) not null,
    PRICE int not null,
    GENRE varchar(100) not null,
    BOOKSTORE_ID INT,
    foreign key (BOOKSTORE_ID) references BOOKSTORE(ID),
);

alter table BOOK
add constraint GENRE CHECK (GENRE='SCIENCE' or GENRE='FANTASY');

create table AUTHOR (
    ID int auto_increment primary key,
    NAME varchar(100) not null,
);

create table BOOK_AUTHOR (
    BOOKS_ID int not null,
    foreign key (BOOKS_ID) references BOOK(ID),
    AUTHORS_ID int not null,
    foreign key (AUTHORS_ID) references AUTHOR(ID),
);
