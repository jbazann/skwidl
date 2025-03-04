
    create table user (
        id binary(16) not null,
        dni varchar(255) not null,
        email varchar(255) not null,
        lastname varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table User_customers (
        User_id binary(16) not null,
        customers binary(16)
    ) engine=InnoDB;

    alter table User_customers 
       add constraint FKhbn77ey2rty89p2xxthieegtf 
       foreign key (User_id) 
       references user (id);
