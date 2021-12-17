CREATE TABLE proxy_list(
    id SERIAL PRIMARY KEY ,
    name varchar(255),
    type Enum('http', 'https')
);