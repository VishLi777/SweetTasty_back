drop table st_refresh_token cascade;
drop table st_roles_user_entities cascade;
drop table st_role cascade;
drop table st_users cascade;
drop table st_characteristic cascade;
drop table st_product cascade;
drop table st_order_product cascade;
drop table st_order cascade;
drop table st_category cascade ;
drop table st_type cascade;


drop sequence category_seq;
drop sequence characteristic_seq;
drop sequence product_seq;
drop sequence type_seq;



delete from st_type;
delete from st_category;
delete from st_users;
delete from st_product;
delete from st_characteristic;








select * from st_users;
select * from st_type;
select * from st_category;

insert into st_type(name) values ('Стандарт');