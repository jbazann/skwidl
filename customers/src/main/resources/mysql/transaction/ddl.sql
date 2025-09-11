
    create table quorum_member (
        transaction_entity_id binary(16) not null,
        id varchar(255)
    ) engine=InnoDB;

    create table transaction (
        id binary(16) not null,
        expires datetime(6) not null,
        coordinator_id varchar(255),
        status tinyint not null,
        primary key (id)
    ) engine=InnoDB;

    alter table quorum_member 
       add constraint FKjhmo0t917ih9djh8cscfwh0h 
       foreign key (transaction_entity_id) 
       references transaction (id);
