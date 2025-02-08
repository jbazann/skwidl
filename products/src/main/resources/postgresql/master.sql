CREATE DATABASE product;
\c product;
\i /docker-entrypoint-initdb.d/ddl/product.sql;

CREATE USER product PASSWORD 'product';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA product TO products;

CREATE DATABASE category PASSWORD 'category';
\c category;
\i /docker-entrypoint-initdb.d/ddl/category.sql;

CREATE USER categories;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA category TO categories;