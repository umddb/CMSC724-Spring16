---------------------------------
--- Olympics Data Set
---------------------------------
-- 1. Draw the query plan for the following query. Very briefly: how well did the
-- estimates match up the actual values?

select r1.event_id, r1.player_id as gold_player_id, r1.result as gold_result, 
       r2.result as silver_result, r2.result - r1.result as diff
from IndividualMedals r1, IndividualMedals r2, events e
where r1.event_id = r2.event_id and r1.medal_obtained = 'GOLD' and r2.medal_obtained = 'SILVER' 
      and e.event_id = r1.event_id and e.result_noted_in = 'seconds';

-- 2.  Draw the query plan for the following query. Clearly annotate the nodes in the
-- query plan that correspond to the tables "c1", "c2", and "temp".
with temp as (
    select c1.country_id, cast(c2.num_players as float)/c1.num_players as ratio
    from (select country_id, count(player_id) as num_players from players group by country_id) c1, 
         (select country_id, count(player_id) as num_players from players where substr(name, 1, 1) in  ('A', 'E', 'O', 'I', 'U') group by country_id) c2
    where c1.country_id = c2.country_id
)
select c.name 
from temp t, countries c 
where ratio = (select max(ratio) from temp) and t.country_id = c.country_id;


---------------------------------
--- TPC-H Data Set
--- The first file in TPCH directory will load the entire dataset ("\i tpch-load.sql").
---------------------------------

-- 3. PostgreSQL does not do a good job with the second and third query in the
-- following set, taking more time for (2) than for (1). Explain.
explain analyze select * from lineitem, orders where o_orderkey = l_orderkey;
explain analyze select * from lineitem, orders 
        where extract(year from o_orderdate) = 1996 and o_orderkey = l_orderkey;
explain analyze select * from lineitem, orders 
        where extract(year from o_orderdate) = 1997 and o_orderkey = l_orderkey;

-- 4. Inspite of a very selective join condition (returning a total of 1844
-- rows), the following query does not execute efficiently. Why?  How may
-- you fix it? There is a very easy fix which PostgreSQL suprising does not
-- automatically employ. 
select count(*) from orders, lineitem where l_shipdate - o_orderdate = 1 
        and extract(year from o_orderdate) = 1992;

-- 5. For the following query, explain in some detail why the final result size
-- is underestimated in one case, and overestimated in the other. 
explain analyze select * from supplier,customer 
        where s_nationkey=c_nationkey and s_acctbal < 5000;
explain analyze select * from supplier,customer 
        where s_nationkey=c_nationkey and s_acctbal > 5000;
