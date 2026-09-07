create sequence outbox_outbox_event_id_seq;
create table outbox (
	event_id int8 default nextval('outbox_outbox_event_id_seq'),
	payload JSONB not null,
	exchange varchar(128) not null,
	routing_key varchar(128) not null,
	primary key(event_id)
);