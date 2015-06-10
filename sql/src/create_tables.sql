DROP TABLE WORK_EXPR;
DROP TABLE EDUCATIONAL_DETAILS;
DROP TABLE MESSAGE;
DROP TABLE CONNECTION_USR;
DROP TABLE USR;


CREATE TABLE USR(
	userId text UNIQUE NOT NULL, 
	password text NOT NULL,
	email text NOT NULL,
	name text,
	dateOfBirth char(11),
	Primary Key(userId));

CREATE TABLE WORK_EXPR(
	userId text NOT NULL, 
	company text NOT NULL, 
	role text NOT NULL,
	location text,
	startDate date,
	endDate date,
	PRIMARY KEY(userId,company,role,startDate));

CREATE TABLE EDUCATIONAL_DETAILS(
	userId text NOT NULL, 
	instituitionName text NOT NULL, 
	major text NOT NULL,
	degree text NOT NULL,
	startdate date,
	enddate date,
	PRIMARY KEY(userId,major,degree));

CREATE TABLE MESSAGE(
	msgId integer UNIQUE NOT NULL, 
	senderId text NOT NULL,
	receiverId text NOT NULL,
	contents text NOT NULL,
	sendTime text,
	deleteStatus integer,
	status text NOT NULL,
	PRIMARY KEY(msgId));

CREATE TABLE CONNECTION_USR(
	userId text NOT NULL, 
	connectionId text NOT NULL, 
	status text NOT NULL,
	PRIMARY KEY(userId,connectionId));

CREATE SEQUENCE msg START 1;

INSERT INTO USR VALUES ('sultani','multani','hi@hi.com','Sultan Khan','1995/10/15');


SELECT USR.name
FROM USR
WHERE USR.name = 'Sultan Khan';
