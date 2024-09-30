create table if not exists image
(
    id serial primary key,
    file_name varchar(255) not null ,
    bin bytea not null
);

create table if not exists app_user
(
    id       serial primary key,
    username text unique not null,
    mail     text unique not null,
    password text        not null,
    pfp      int references image (id) on delete set null
);

create table if not exists client
(
    user_id int primary key references app_user (id) on delete cascade
    -- more client only props would go here
);

create table if not exists driver
(
    user_id int primary key references app_user (id) on delete cascade,
    extra1  text
    -- more driver only props would go here
);

create table if not exists vehicle
(
    id           serial primary key,
    driver_id    int not null references driver (user_id) on delete cascade,
    plate_number text unique,
    volume_m3    double precision,
    description  text,
    img_id int references image (id) on delete set null
);

create table if not exists weekly_availability
(
    id       serial primary key,
    week_day int not null,
    t_start  time with time zone,
    t_end    time with time zone,
    unique (week_day, t_start, t_end)
);

create table if not exists country
(
    id   serial primary key,
    name text unique       not null,
    code varchar(2) unique not null
);

create table if not exists province
(
    id   serial primary key,
    name text unique not null
);

create table if not exists neighborhood
(
    id   serial primary key,
    name text unique not null
);

create table if not exists zone
(
    id              serial primary key,
    country_id      int references country (id) on delete cascade,
    province_id     int references province (id) on delete cascade,
    neighborhood_id int references neighborhood (id) on delete cascade,
    unique (country_id, province_id, neighborhood_id)
);

create table if not exists vehicle_weekly_zone
(
    vehicle_id      int not null references vehicle (id) on delete cascade,
    availability_id int not null references weekly_availability (id) on delete cascade,
    zone_id         int not null references zone (id) on delete cascade,
    primary key (vehicle_id, availability_id, zone_id)
);

create table if not exists booking
(
    id   serial primary key,
    date date
);

create table if not exists reservation
(
    driver_id    int     not null references driver (user_id) on delete cascade,
    client_id    int     not null references client (user_id) on delete cascade,
    booking_id   int     not null references booking (id) on delete cascade,
    is_confirmed boolean not null,
    proof_of_payment int references image (id) on delete set null,
    primary key (driver_id, booking_id)
);