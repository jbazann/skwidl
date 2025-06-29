
    create table product.product (
        id uuid not null,
        category uuid not null,
        current_stock integer check (current_stock>=0),
        description varchar(2047) not null,
        discount numeric(38,2) not null check (discount>=0),
        minimum_stock integer check (minimum_stock>=0),
        name varchar(511),
        price numeric(38,2) not null check (price>=0),
        primary key (id)
    );
