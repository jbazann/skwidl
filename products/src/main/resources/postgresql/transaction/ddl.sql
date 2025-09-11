
    create table transaction.quorum_member (
        transaction_entity_id uuid not null,
        id varchar(255)
    );

    create table transaction.transaction (
        id uuid not null,
        expires timestamp(6) not null,
        coordinator_id varchar(255),
        status smallint not null check (status between 0 and 5),
        primary key (id)
    );

    alter table if exists transaction.quorum_member 
       add constraint FKjhmo0t917ih9djh8cscfwh0h 
       foreign key (transaction_entity_id) 
       references transaction.transaction;
