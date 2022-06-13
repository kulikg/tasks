--liquibase formatted sql

--changeset gabor.kulik:1 labels:master context:tasks
--comment: initial change
create table users (
    id bigint primary key auto_increment not null,
    first_name varchar(255),
    last_name varchar(255),
    user_name varchar(255)
) engine=InnoDB;
create table tasks (
    id bigint primary key auto_increment not null,
    date_time datetime(6),
    description varchar(255),
    name varchar(255),
    status varchar(31),
    owner_id bigint
) engine=InnoDB;

alter table tasks add constraint FOREIGN_KEY foreign key (owner_id) references users (id);
alter table users add constraint UNIQ_KEY unique (user_name);

--rollback DROP TABLE person;

