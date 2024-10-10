
    create table customer (
        id binary(16) not null,
        cuit varchar(255) not null,
        email varchar(255) not null,
        maxActiveSites integer not null,
        maxDebt decimal(38,2) not null,
        name varchar(255) not null,
        pendingSites integer not null,
        primary key (id)
    ) engine=InnoDB;

    create table Customer_activeSites (
        Customer_id binary(16) not null,
        activeSites binary(16)
    ) engine=InnoDB;

    create table Customer_allowedUsers (
        Customer_id binary(16) not null,
        allowedUsers binary(16)
    ) engine=InnoDB;

    alter table Customer_activeSites 
       add constraint FKp85bcth6v2wuacxg7vx9x6uk6 
       foreign key (Customer_id) 
       references customer (id);

    alter table Customer_allowedUsers 
       add constraint FK95rwfxqep8a44q5od26h6vfjm 
       foreign key (Customer_id) 
       references customer (id);
