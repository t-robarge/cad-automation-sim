create table if not exists part (
  id bigserial primary key,
  part_number varchar(64),
  name varchar(255),
  unit varchar(8),
  created_at timestamp
);
create table if not exists feature (
  id bigserial primary key,
  type varchar(32), x double precision, y double precision, d1 double precision, d2 double precision,
  part_id bigint references part(id)
);
create table if not exists annotation (
  id bigserial primary key,
  label varchar(64), value varchar(64), x double precision, y double precision, view_type varchar(16),
  part_id bigint references part(id)
);
create table if not exists change_package (
  id bigserial primary key,
  part_number varchar(64), reason varchar(255), diff text, created_at timestamp
);
