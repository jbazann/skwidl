
    create table customer (
        id binary(16) not null,
        budget decimal(38,2) not null,
        cuit varchar(15) not null,
        email varchar(1023) not null,
        max_active_sites integer not null,
        max_debt decimal(38,2) not null,
        name varchar(511) not null,
        pending_sites integer not null,
        primary key (id)
    ) engine=InnoDB;

    create table customer_active_sites (
        Customer_id binary(16) not null,
        active_sites binary(16)
    ) engine=InnoDB;

    create table customer_allowed_users (
        Customer_id binary(16) not null,
        allowed_users binary(16)
    ) engine=InnoDB;

    alter table customer_active_sites 
       add constraint FKsa2am22d2c2po265lu4rcm7nn 
       foreign key (Customer_id) 
       references customer (id);

    alter table customer_allowed_users 
       add constraint FKspubd04rgskome6fon629kydc 
       foreign key (Customer_id) 
       references customer (id);
