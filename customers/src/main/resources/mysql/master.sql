CREATE DATABASE IF NOT EXISTS customer;
USE customer;

SOURCE /docker-entrypoint-initdb.d/ddl/customer.sql;

CREATE DATABASE IF NOT EXISTS site;
USE site;
SOURCE /docker-entrypoint-initdb.d/ddl/site.sql;

CREATE DATABASE IF NOT EXISTS user;
USE user;
SOURCE /docker-entrypoint-initdb.d/ddl/user.sql;

GRANT SELECT, INSERT, UPDATE, DELETE ON customer.* TO 'customers'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON site.* TO 'customers'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON user.* TO 'customers'@'%';