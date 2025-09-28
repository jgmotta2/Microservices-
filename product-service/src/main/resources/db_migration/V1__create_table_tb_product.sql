CREATE TABLE TB_PRODUCT (
                            id serial primary key,
                            description varchar(255) not null,
                            brand varchar(100) not null,
                            model varchar(100) not null,
                            currency char(3) not null,
                            price double precision not null,
                            stock integer not null
);