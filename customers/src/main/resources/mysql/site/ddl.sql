
    create table site (
        id binary(16) not null,
        address varchar(255) not null,
        budget decimal(38,2) not null,
        coordinates varchar(255) not null,
        customer binary(16) not null,
        status enum ('ACTIVE','PENDING','FINISHED') not null,
        primary key (id)
    ) engine=InnoDB;
