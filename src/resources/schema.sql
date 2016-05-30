CREATE TABLE coupon(
  code varchar(10) primary key,
  name varchar(50) not null,
  description varchar(255) not null,
  maxUsage integer not null,
  expiration date not null,
  discount integer not null
)