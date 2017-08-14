CREATE TABLE PROGRAM (
 ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
 ELEMENT_ID VARCHAR(30),
 PROGRAM_ID VARCHAR(30),
 URL VARCHAR(255),
 TOTAL_PAGE NUMERIC(10),
 "NAME" VARCHAR(100),
 "TYPE" VARCHAR(30), 
 STATUS VARCHAR(20), 
 INDEX BIGINT DEFAULT 0,
 PRIMARY KEY (ID));