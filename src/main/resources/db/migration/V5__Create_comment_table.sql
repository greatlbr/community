create table comment
(
	id bigint auto_increment,
	parent_id bigint not null,
	type int not null,
	commentor int not null,
	gmt_create bigint,
	gmt_modified bigint,
	like_count bigint default 0,
	constraint COMMENT_PK
		primary key (id)
);\
