
    create table product.product (
        id uuid not null,
        category uuid not null,
        currentStock integer not null check (currentStock>=0),
        description varchar(255) not null,
        discount numeric(38,2) not null check (discount>=0),
        minimumStock integer not null check (minimumStock>=0),
        name varchar(255) not null,
        price numeric(38,2) not null check (price>=0),
        primary key (id)
    );
