create sequence publish_box_id_seq;
create table publish_box (
    id int8 default nextval('publish_box_id_seq'),
    outbox_event_id uuid unique not null,
    consumer varchar(128) not null
);
create index publish_box_outbox_event_id on publish_box using btree (outbox_event_id)