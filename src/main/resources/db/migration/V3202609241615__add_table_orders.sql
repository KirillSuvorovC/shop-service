create table orders (
    id serial8,
    user_id int8 not null,
    status varchar(32) not null,
    created_at timestamptz default now(),
    updated_at timestamptz default now(),
    version int8 default 0,

    primary key(id),
    foreign key(user_id) references users(user_id),

    check(status in ('NEW', 'SHIPPED', 'DELIVERED', 'CANCELLED'))
);

create table order_items (
    id serial8,
    order_id int8 not null,
    product_id int8 not null,
    amount int4 not null,
    price_at_order numeric(10, 2) not null,

    primary key(id),
    foreign key(order_id) references orders(id),
    foreign key(product_id) references products(product_id),

    check(amount > 0 and
          price_at_order > 0)
)