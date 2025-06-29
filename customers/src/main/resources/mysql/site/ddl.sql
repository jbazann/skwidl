
    create table site (
        id binary(16) not null,
        address varchar(511),
        budget decimal(38,2),
        coordinates varchar(511),
        customer binary(16) not null,
        status enum ('UNSET','ACTIVE','PENDING','FINISHED') not null,
        primary key (id)
    ) engine=InnoDB;
