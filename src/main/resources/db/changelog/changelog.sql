--liquibase formatted sql

--changeset master:1
CREATE TABLE product(
  id int auto_increment primary key,
  name varchar(256),
  description varchar(1024)
);

CREATE INDEX product_name_idx ON product(name);

--changeset master:2
CREATE TABLE price(
  id int auto_increment primary key,
  amount DOUBLE NOT NULL,
  currency varchar(3) NOT NULL,
  valid_from DATETIME NOT NULL,
  product_id int NOT NULL
);

ALTER TABLE price ADD FOREIGN KEY (product_id) REFERENCES product(id);

--changeset master:3
CREATE TABLE activity(
    id bigint auto_increment primary key,
    activity_type varchar(16),
    amount int NOT NULL,
    creation_date datetime NOT NULL,
    product_id int NOT NULL
);

ALTER TABLE activity ADD FOREIGN KEY (product_id) REFERENCES product(id);

CREATE INDEX activity_product_idx ON activity(product_id);

--changeset master:4
CREATE TABLE e_order(
    id bigint auto_increment primary key,
    creation_date DATETIME NOT NULL,
    order_state varchar(8) NOT NULL
);

--changeset master:5
CREATE TABLE e_order_product(
    id bigint auto_increment primary key,
    quantity int NOT NULL,
    product_id int NOT NULL,
    e_order_id bigint NOT NULL
);

ALTER TABLE e_order_product ADD FOREIGN KEY (product_id) REFERENCES product(id);
ALTER TABLE e_order_product ADD FOREIGN KEY (e_order_id) REFERENCES activity(id);

CREATE INDEX EORDER_PRODUCT_IDX ON e_order_product(product_id);
CREATE INDEX EORDER_PRODUCT_ACTIVITY_IDX ON e_order_product(e_order_id);

