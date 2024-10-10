
    create table user (
        id binary(16) not null,
        customer binary(16) not null,
        dni varchar(255) not null,
        email varchar(255) not null,
        lastname varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;
