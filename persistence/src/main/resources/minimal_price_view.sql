create or replace view minimal_price as
    select driver_id, min(hourly_rate) as min from vehicle group by driver_id;
