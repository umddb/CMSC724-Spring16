# SQL Assignment, CMSC724, Spring 2016

*The assignment is to be done by yourself.*

The goal of this assignment is to get you (re-)familiarized with relational databases, and specifically the
standard query language for databases, SQL.
We will use the open source PostgreSQL database system for this purpose. Installing the system on your
machine will be an important part of the assignment.

**Software installs often do not go smoothly; we strongly recommend you at least install and play with
PostgreSQL well in advance. Generally speaking, we will likely not be able to help you with any installation
issues, but sooner you contact us, better the chances.**

## Installing and Using PostgreSQL
The PostgreSQL website has binary packages and source code for installing it on a variety of platforms. 
You will be asked for the `superuser` password along the way during install -- make sure to remember that. 
The current version of PostgreSQL is 9.4. You will find the detailed documentation at:
[PostgreSQL Website](http://www.postgresql.org/docs/9.4/static/index.html)

You will be using PostgreSQL in a client-server mode. 
First you have to start the server in the background. 
Recall that the server is a continuously running process that listens on a specific port (the actual port would
differ, and you can usually choose it when starting the server). In order to connect to the server, the client
will need to know the port. The client and server are often on different machines, but
for you, it may be easiest if they are on the same machine. 

Using the `psql` client is the easiest -- it provides
a commandline access to the database. But there are other clients too. We will assume `psql` here.

After installing PostgreSQL, before starting the server, you must first `initialize`
a database storage area on disk. This is the area where all the data files are stored (called `data directory` in PostgreSQL). This can be done
using the `initdb` command, which takes in a data directory as the input. See [initdb documentation](http://www.postgresql.org/docs/current/static/app-initdb.html) for
more details.

After initializing the database storage area, you can start the server. There are different ways
to do it depending on the installation and the platform. On UNIX platforms, the command:
``
postgres -D /usr/local/pgsql/data
``
will start the server, with the data directory `/usr/local/pgsql/data`. See the 
[documentation for more details](http://www.postgresql.org/docs/current/static/server-start.html).

**Note: Both the above steps may be done during the install itself. E.g., on Macs, the 1-click
installer asks for a location for the data directory (Default: /Library/PostgreSQL/9.4/data), initializes 
it, and starts the server.**

After the server has started, you have to `create` a database. This is done using the `createdb` command.
PostgreSQL automatically creates one database for its own purpose, called `postgres`jI. It is preferable 
you create a different database for your data. Here are [more details on `createdb`](http://www.postgresql.org/docs/current/static/tutorial-createdb.html).

For the assignment, you can create a database called `olympics` (you can name it something else if you want).
The rest of the commands will assume the database is called `olympics`.

**Important:** PostgreSQL server has a default superuser called `postgres`. You can do everything under that
username, or you can create a different username for yourself. If you run a command (say createdb) without
any options, it uses the same username that you are logged in under. However, if you haven't created a
PostgreSQL user with that name, the command will fail. You can either create a user (by logging in as the superuser),
or run everything as a superuser (typically with the option: `-U postgres`).

Once the database is created, you can connect to it. There are many ways to connect to the server. The
easiest is to use the commandline tool called `psql`. Start it by:
```
psql -U postgres olympics
```

The above command gives the username `postgres` to connect to the database. You may be prompted for
a password. `psql` takes quite a few other options: you can specify different user, a specific port,
another server etc (see [documentation](http://www.postgresql.org/docs/current/static/app-psql.html)).

Now you can start using the database. 

- The psql program has a number of internal commands that are not SQL commands; such commands are often client and database
specific. For psql, they begin with the backslash character, `\`. 
For example, you can get help on the syntax of various PostgreSQL SQL commands by typing: `\h`.

- `\d`: lists out the tables in the database.

- All commands like this can be found at:  [link](http://www.postgresql.org/docs/current/static/app-psql.html). `\?` will also list them out.

- To populate the database using the provided olympics dataset, use the following:
`\i populate.sql`. For this to work, the populate.sql file must be in the same directory as the one 
where you started psql. This commands creates the tables, and inserts the tuples. We will discuss the schema of the dataset
in the next section.


## Olympics Dataset
The dataset contains the details of the 2000 and 2004 Summer Olympics, for a subset of the games
(`swimming` and `athletics`). More specifically,
it contains the information about players, countries, events, and results. It only contains the medals information
(so no heats, and no players who didn't win a medal).

The schema of the tables should be self-explanatory (see the `populate.sql` file). 
The data was collected from [Database Olypics](http://www.databaseolympics.com/) and Wikipedia.

Some things to note: 
- The birth-date information was not available in the database, and that field was populated randomly.
- Be careful with the team events; the information about medals is stored by player, and a single team event 
gold gets translated into usually 4, but upto 6-7 `medal` entries in the Results table (for SWI and ATH events).
- If two players tie in an event, they are both awarded the same medal, and the next medal is skipped (ie., there are
events without any silver medals, but two gold medals). This is more common in Gymnastics (the dataset does not contain 
that data anyway, but does have a few cases like that).
- Note that, there are instances (in the dataset) where two players appear to be tied (e.g., 'E132'). In that event, 
the two players did have the same final score, but the tie was broken using information not provided in the dataset
(number of tries for High Jump). This shouldn't have any effect on the assignment questions.
- The `result` for a match is reported as a `float` number, and its interpretation is given in the corresponding
`events` table. There are three types: `seconds` (for time), `points` (like in Decathlon), `meters` (like in long jump).

You can load the database in psql using: 

`\i populate.sql`

You may have to give an explicit path for populate.sql (tab-autocomplete works well in psql).

## SQL
Queries in psql must be terminated with a semicolon. After populating the database, you can test it by 
running simple queries like: 
`select * from olympics;`

We will not cover SQL in the class.
- The website for [CMSC 424](http://www.cs.umd.edu/class/spring2012/cmsc424-0101/) contains my slides for SQL, along with a set of sample queries.
- You can also look at an undergraduate textbook. Keep in mind the syntax of the commands
can slightly vary, especially commands that use any advanced features.
- There are numerous online resources. PostgreSQL manual is a good place for introduction to SQL. 
[SQL Zoo](http://sqlzoo.net) also has many examples.

Here are some example queries on the olympics dataset and the SQL for them.

- Report the total number of medals won by M. Phelps over both olympics.
```
select * from players where name like '%Phelps%';
```
See that M. Phelps ID is PHELPMIC01.
```
select count(medal) from results where player_id = 'PHELPMIC01'; 
```

- Find the country with the highest population density (population/area-sqkm).
```
select name 
from countries 
where population/area_sqkm = (select max(population/area_sqkm) from countries);
```
Note that using a nested "subquery" (which first finds the maximum value of the density) as above is the most compact way to write this query.

- What was the duration of the 2004 Olympics (use startdate and enddate to find this) ? 
```
select olympic_id, enddate - startdate + 1
from olympics o 
where o.year = 2004;
```
There are many interesting and useful functions on the "date" datatype. See [link](http://www.postgresql.org/docs/current/interactive/functions-datetime.html).

- Write a query to add a new column called `country_id` to the IndividualMedals table (created during the assignment, Question 2). 
```
alter table IndividualMedals add country_id char(3);
```
    
- Initially the `country_id` column in the IndividualMedals table would be listed as empty.  Write a query to `update` the table to set it appropriately.
```
update IndividualMedals im
set country_id = (select country_id
        from players p
        where p.player_id = im.player_id);
```

- **(WITH)** In many cases you might find it easier to create temporary tables, especially
for queries involving finding "max" or "min". This also allows you to break down
the full query and makes it easier to debug. It is preferable to use the WITH construct
for this purpose. The syntax appears to differ across systems, but here is the link
to [PostgreSQL Syntax](http://www.postgresql.org/docs/9.4/static/queries-with.html).

- **(LIMIT)** PostgreSQL allows you to limit the number of results displayed which 
is useful for debugging etc. Here is an example: `select * from Players limit 5;`

## Assignment 
Submit three files, `sql-assignment.txt` (filled in with your answers), your Java program source, and the output of the Java program, using 
the Submit Server.

### Part 1 (SQL)
See the provided text file (`sql-assignment.txt`) for the queries. You should use the same text file for submission. 
The text file should be self-explanatory. The submission will be through the submit server.

### Part 2 (JDBC)
One of more prominent ways to use a database system is using an external client, using APIs such as ODBC and JDBC.
This allows you to run queries against the database and access the results from within say a Java program.

Here are some useful links:
- [JDBC](http://en.wikipedia.org/wiki/Java_Database_Connectivity)
- [Step by Step Instructions (using an old JDBC driver)](http://www.mkyong.com/java/how-do-connect-to-postgresql-with-jdbc-driver-java)
- [JDBC PostgreSQL](http://jdbc.postgresql.org/index.html)

The last link has detailed examples in the `documentation` section.

*** Your Task: Write a Java program to do the same thing as Question 4 from the SQL assignment. ***

```
4. For 2004 Olympics, generate a list - (birthyear, num-players, num-gold-medals) - 
containing the years in which the atheletes were born, the number of players
born in each of those years who won at least one gold medal, and the number of gold 
medals won by the players born in that year.
```
... with the constraint that you can only issue a simple query to the database that does the join between 
the three relations. Specifically, you should only issue the following query to the database, rest
of the processing should be done in the Java program:

```
select p.birthdate, p.player_id
from results r, events e, players p 
where p.player_id = r.player_id and e.event_id = r.event_id 
      and e.olympic_id = 'ATH2004'and r.medal = 'GOLD'
```

You should submit the Java program, and the answer itself should be submitted along with the rest of the assignment.
