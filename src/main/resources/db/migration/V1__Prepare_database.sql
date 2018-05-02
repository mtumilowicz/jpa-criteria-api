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

create table DEPARTMENT (
    ID int auto_increment primary key,
    NAME varchar(100) not null unique,
    BOOKSTORE_ID INT,
    foreign key (BOOKSTORE_ID) references BOOKSTORE(ID),
);

create table BOOK (
    ID int auto_increment primary key,
    NAME varchar(100) not null,
    DEPARTMENT_ID INT,
    foreign key (DEPARTMENT_ID) references DEPARTMENT(ID),
);

create table AUTHOR (
    ID int auto_increment primary key,
    NAME varchar(100) not null,
);

create table BOOK_AUTHOR (
    BOOK_ID int not null,
    foreign key (BOOK_ID) references BOOK(ID),
    AUTHOR_ID int not null,
    foreign key (AUTHOR_ID) references AUTHOR(ID),
);
