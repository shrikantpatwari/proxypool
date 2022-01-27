CREATE TABLE proxy_list (
  id BIGINT NOT NULL,
  ip VARCHAR(255) NULL,
  usage_count BIGINT NOT NULL,
  status INT NULL,
  created_at datetime NULL,
  updated_at datetime NULL,
  CONSTRAINT pk_proxy_list PRIMARY KEY (id)
);

CREATE SEQUENCE PROXY_SEQUENCE_ID START WITH (select max(id) + 1 from proxy_list);

CREATE TABLE user_proxy_map (
  id BIGINT NOT NULL,
  proxy VARCHAR(255) NULL,
  user_id VARCHAR(255) NULL,
  created_at datetime NULL,
  updated_at datetime NULL,
  CONSTRAINT pk_user_proxy_map PRIMARY KEY (id)
);

CREATE SEQUENCE USER_PROXY_MAP_SEQUENCE_ID START WITH (select max(id) + 1 from user_proxy_map);