-- auto-generated definition
create table user
(
    id       int auto_increment
        primary key,
    name     varchar(100) not null,
    email    varchar(100) not null,
    password varchar(100) not null,
    constraint unique_email
        unique (email)
);
