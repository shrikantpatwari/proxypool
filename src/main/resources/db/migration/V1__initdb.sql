CREATE TABLE proxy_list (
  id BIGINT NOT NULL,
  ip VARCHAR(255) NULL,
  status INT NULL,
  created_at datetime NULL,
  updated_at datetime NULL,
  CONSTRAINT pk_proxy_list PRIMARY KEY (id)
);