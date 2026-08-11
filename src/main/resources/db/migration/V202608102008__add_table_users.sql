create sequence users_user_id_seq;

create table users (
    user_id int8 default nextval('users_user_id_seq'),
    username varchar(100) not null,
    password varchar(255) not null,
    role_id int8,
    primary key(user_id),
    foreign key(role_id) references roles (role_id)
)