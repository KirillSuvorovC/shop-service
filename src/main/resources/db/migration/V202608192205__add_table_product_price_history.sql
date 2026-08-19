create sequence products_price_history_history_id_seq;
create table products_price_history (
	history_id int8 default nextval('products_price_history_history_id_seq'),
	product_id int8 not null,
	updated_at timestamptz default now(),
	price numeric(10, 2) not null,
	primary key(history_id),
	foreign key(product_id) references products(product_id),
	check(price > 0),
	check(updated_at <= now())
);
create index products_price_history_product_id_idx on products_price_history using btree (product_id);
create index products_price_history_product_updated_at_idx on products_price_history using btree (updated_at);