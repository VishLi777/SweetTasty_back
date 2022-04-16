 create sequence category_seq start 214 increment 1;
 create sequence characteristic_seq start 14398 increment 1;
 create sequence product_seq start 2082 increment 1;
 create sequence type_seq start 8 increment 1;
 create table st_category (id bigserial not null, name varchar(255), type_id int8, primary key (id));
 create table st_product (id bigserial not null, data_of_create int8, is_name boolean, name varchar(255), path_file TEXT, price varchar(255) not null, category_id int8, type_id int8, primary key (id));
 create table st_characteristic (id bigserial not null, description text, title text, product_id int8, primary key (id));
 create table st_order (id  bigserial not null, data_of_create int8, status varchar(255), total_sum_check int8 not null, user_id int8, primary key (id));
 create table st_order_product (id  bigserial not null, amount_of_product int8 not null, product_id int8, order_id int8, primary key (id));
 create table st_refresh_token (id  bigserial not null, expiry_date timestamp not null, token varchar(255) not null, user_id int8, primary key (id));
 create table st_role (id  bigserial not null, role varchar(255), primary key (id));
 create table st_roles_user_entities (user_id int8 not null, role_id int8 not null);
 create table st_type (id bigserial not null, name varchar(255), primary key (id));
 create table st_users (id  bigserial not null, fio varchar(255), is_man boolean, password varchar(255), telephone_number varchar(255) not null, primary key (id));
 alter table if exists st_product add constraint UKbj4t7p0tggpag9wcmybi3pruq unique (name);
 alter table if exists st_refresh_token add constraint UK_hkuy0qblrwy6m2q69iphgw8nd unique (token);
 alter table if exists st_type add constraint UKkxil4qdacayg2fdxyi31tax0g unique (name);
 alter table if exists st_users add constraint UKhx7tuk6f8yxe5uxtnjb7lqd7e unique (telephone_number);
 alter table if exists st_category add constraint FKm75en58ybxe798ayqbkjk4k7i foreign key (type_id) references st_type on delete cascade;
 alter table if exists st_product add constraint FKmj22rk9rlg1p7rq5fuivj0829 foreign key (category_id) references st_category on delete cascade;
 alter table if exists st_product add constraint FK59w5prox8vfk6ai0efh775o67 foreign key (type_id) references st_type on delete cascade;
 alter table if exists st_characteristic add constraint FKa1pnm5u6v8e714vnlt3jakqt9 foreign key (product_id) references st_product on delete cascade;
 alter table if exists st_order add constraint FK3utlev34r2puojvcaoy4tdx1 foreign key (user_id) references st_users;
 alter table if exists st_order_product add constraint FK3rb9fy0snywkyusln4ti4odwq foreign key (product_id) references st_product;
 alter table if exists st_order_product add constraint FK37p1rswsbum9jpbsfcx5u5v8i foreign key (order_id) references st_order;
 alter table if exists st_refresh_token add constraint FKc5eff31geb3nbm4wf3by4u5sw foreign key (user_id) references st_users;
 alter table if exists st_roles_user_entities add constraint FKrlnrhrcd3ej31xm1vmrdebgg1 foreign key (role_id) references st_role;
 alter table if exists st_roles_user_entities add constraint FKl32gfivab3akfy5nyfeyjlrrf foreign key (user_id) references st_users;


 insert into st_role(role) values ('ADMIN');
 insert into st_role(role) values ('USER');

 insert into st_users (fio, password, telephone_number)
 VALUES ('admin', '$2a$12$tyQ6j.apGIh3f7FtmZRpFeAjkscr1hNHTmzGAa9StEpJJCdFjZPne', '+77777777777');

 insert into st_roles_user_entities(user_id, role_id) values (1, 1);

 insert into st_type(name) values ('Стандарт');
 insert into st_type(name) values ('На заказ');