:set fileformat=unix
#!/bin/bash

psql -d onlineShop -U postgres -c "\copy st_type FROM st_type.csv delimiter ';' csv header ENCODING 'utf8'"
psql -d onlineShop -U postgres -c "\copy st_category FROM st_category.csv delimiter ';' csv header ENCODING 'utf8'"
psql -d onlineShop -U postgres -c "\copy st_product FROM st_product.csv delimiter ';' csv header ENCODING 'utf8'"
psql -d onlineShop -U postgres -c "\copy st_characteristic FROM st_characteristic.csv delimiter ';' csv header ENCODING 'utf8'"

