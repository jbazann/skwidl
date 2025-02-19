CREATE DATABASE product;
\c product;
CREATE SCHEMA product;
\i /docker-entrypoint-initdb.d/ddl/product.sql;

CREATE USER product PASSWORD 'product';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA product TO product;
GRANT USAGE ON SCHEMA product TO product;

CREATE DATABASE category;
\c category;
CREATE SCHEMA category;
\i /docker-entrypoint-initdb.d/ddl/category.sql;

CREATE USER category PASSWORD 'category';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA category TO category;
GRANT USAGE ON SCHEMA category TO category;