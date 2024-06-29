create extension if not exists "uuid-ossp";

create function filemanager.random_uuid()
    returns uuid
    language plpgsql
as
$$
begin
    return filemanager.uuid_generate_v4();
end;
$$;

create table filemanager.files
(
    id   uuid default filemanager.random_uuid() not null,
    data oid,
    name varchar(255),
    size bigint,
    type varchar(255),
    primary key (id)
)