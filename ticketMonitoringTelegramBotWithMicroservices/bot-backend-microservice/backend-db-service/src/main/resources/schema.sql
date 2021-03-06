DROP TABLE IF EXISTS tUsers CASCADE;;
DROP TABLE IF EXISTS tConcertModel CASCADE;;

create table tConcertModel
(
    id                integer generated by default as identity,
    artist            varchar(255),
    concertUrl        varchar(255),
    date              varchar(255),
    place             varchar(255),
    shouldBeMonitored boolean,
    primary key (id)
);

create table tUsers
(
    id                     integer generated by default as identity,
    chatId                 integer,
    dateOfMonitorFinish    timestamp,
    isDateExpired          boolean,
    isMonitoringSuccessful boolean,
    messageText            text,
    concert_id             integer not null,
    primary key (id)
);

alter table if exists tUsers
    add constraint FKo93itnf9gxojjk4vcwxgajoul
        foreign key (concert_id)
            references tConcertModel ON DELETE CASCADE;



