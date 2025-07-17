CREATE TABLE if NOT EXISTS conference
(
id VARCHAR(64) NOT NULL PRIMARY KEY,
name text NOT NULL
);
DELETE FROM conference;

CREATE TABLE if NOT EXISTS tokenentry
(
segment INTEGER NOT NULL,
processorName VARCHAR(255) NOT NULL,
token bytea,
tokenType VARCHAR(255),
timestamp VARCHAR(1000),
owner VARCHAR(1000),
PRIMARY KEY(processorName, segment)
);
DELETE from tokenentry;