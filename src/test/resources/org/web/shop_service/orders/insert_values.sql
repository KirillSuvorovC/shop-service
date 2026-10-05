insert into products(product_id, name, price) values
    (1, 'Product', 50.00);

insert into orders(id, user_id, status) values
    (1, 1, 'NEW');
insert into order_items(id, order_id, product_id, amount, price_at_order) values
    (1, 1, 1, 1, 50.00),
    (2, 1, 1, 1, 50.00),
    (3, 1, 1, 1, 50.00);