DROP TABLE IF EXISTS tTicket;
DROP TABLE IF EXISTS tConcerts;
DROP SEQUENCE IF EXISTS concert_id_seq;
DROP SEQUENCE IF EXISTS ticket_id_seq;
CREATE SEQUENCE concert_id_seq start with 1;
CREATE SEQUENCE ticket_id_seq start with 1;

create table tTicket
(
    id         serial,
    cost       varchar(255),
    type       varchar(255),
    concert_id bigint,
    primary key (id)
);

create table tConcerts
(
    id              serial,
    artist          varchar(255),
    concertUrl      varchar(255),
    date            varchar(255),
    place           varchar(255),
    ticketsToString text,
    primary key (id)
);

alter table if exists tTicket
    add constraint FK1ikasd7pqvi9l6f4yr0rfk14m
        foreign key (concert_id)
            references tConcerts ON DELETE CASCADE;


ALTER TABLE tConcerts
    ALTER id SET DEFAULT NEXTVAL('concert_id_seq');
ALTER TABLE tTicket
    ALTER id SET DEFAULT NEXTVAL('ticket_id_seq');


