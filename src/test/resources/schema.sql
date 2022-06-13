use TASKS;

create table tasks (id bigint not null AUTO_INCREMENT, date_time datetime(6), description varchar(255), name varchar(255), status varchar(255), owner_id bigint, primary key (id)) engine=InnoDB;
create table users (id bigint not null AUTO_INCREMENT, first_name varchar(255), last_name varchar(255), user_name varchar(255), primary key (id)) engine=InnoDB;

alter table tasks add constraint FOREIGN_KEY foreign key (owner_id) references users (id);
alter table users add constraint UNIQ_KEY unique (user_name);