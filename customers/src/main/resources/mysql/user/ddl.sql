
    create table user (
        id binary(16) not null,
        dni varchar(15),
        email varchar(511),
        lastname varchar(511),
        name varchar(511),
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
