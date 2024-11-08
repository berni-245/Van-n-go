create view minimal_price as SELECT driver_id, MIN(hourly_rate) from vehicle group by driver_id;
