create schema if not exists "filemanager";
create table filemanager.files
(
    id   uuid default public.random_uuid() not null,
    data oid,
    name varchar(255),
    size bigint,
    type varchar(255),
    primary key (id)
)