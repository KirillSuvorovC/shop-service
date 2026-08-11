create sequence "roles_role_id_seq";

create table roles (
    role_id int default nextval('roles_role_id_seq'),
    name varchar(100) not null,
    primary key(role_id)
);

insert into roles(name) values
    ('ROLE_USER'),
    ('ROLE_ADMIN')