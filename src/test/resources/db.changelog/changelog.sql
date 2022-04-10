--liquibase formatted sql

--changeset master:1
CREATE TABLE product(
                        id bigint primary key,
                        name varchar(256),
                        description varchar(1024),
                        available boolean not null default true,
                        quantity int not null default 0
);

CREATE INDEX product_name_idx ON product(name);

--changeset master:2
CREATE TABLE price(
                      id bigint primary key,
                      amount DOUBLE NOT NULL,
                      currency varchar(3) NOT NULL,
                      valid_from DATETIME NOT NULL,
                      product_id bigint NOT NULL
);

ALTER TABLE price ADD FOREIGN KEY (product_id) REFERENCES product(id);

--changeset master:3
CREATE TABLE e_order(
                        id bigint primary key,
                        creation_date DATETIME NOT NULL,
                        order_state varchar(8) NOT NULL
);

--changeset master:4
CREATE TABLE e_order_product(
                                id bigint primary key,
                                quantity int NOT NULL,
                                product_id bigint NOT NULL,
                                e_order_id bigint NOT NULL
);

ALTER TABLE e_order_product ADD FOREIGN KEY (product_id) REFERENCES product(id);
ALTER TABLE e_order_product ADD FOREIGN KEY (e_order_id) REFERENCES e_order(id);

CREATE INDEX e_order_product_idx ON e_order_product(product_id);
CREATE INDEX e_order_product_e_order_idx ON e_order_product(e_order_id);

--changeset master:6
create sequence e_order_id_seq start with 1 increment by 1;
create sequence e_order_product_id_seq start with 1 increment by 1;
create sequence product_id_seq start with 1 increment by 1;

--changeset master:7
create sequence price_id_seq start with 1 increment by 1;