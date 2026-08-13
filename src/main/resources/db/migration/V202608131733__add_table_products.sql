create sequence products_product_id_seq;
create table products(
    product_id int8 default nextval('products_product_id_seq'),
    name varchar(150) not null,
    price numeric(10, 2) not null,
    created_at timestamptz not null default now(),
    primary key(product_id)
)