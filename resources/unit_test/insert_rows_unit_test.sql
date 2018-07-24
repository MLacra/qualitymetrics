	
DROP TABLE quality.output;

CREATE TABLE quality.output
(
    column1 text COLLATE pg_catalog."default",
    column2 text COLLATE pg_catalog."default",
    column3 text COLLATE pg_catalog."default",
    column4 text COLLATE pg_catalog."default"
);   

DROP TABLE quality.ground_truth;

CREATE TABLE quality.ground_truth
(
    column1 text COLLATE pg_catalog."default",
    column2 text COLLATE pg_catalog."default",
    column3 text COLLATE pg_catalog."default",
    column4 text COLLATE pg_catalog."default"
);
    --row1
INSERT INTO quality.output(
	column1, column2, column3, column4)
	VALUES ('a1', 'b1', 'c1', 'd1');

INSERT INTO quality.ground_truth(
	column1, column2, column3, column4)
	VALUES ('a1', 'b1', 'c1', 'd1');
	
	--row2
	INSERT INTO quality.output(
	column1, column2, column3, column4)
	VALUES ('a2', null , 'c2', 'd2');

INSERT INTO quality.ground_truth(
	column1, column2, column3, column4)
	VALUES ('a2', null, 'c2', 'd2');
	
	--row3
	INSERT INTO quality.output(
	column1, column2, column3, column4)
	VALUES ('a3', null, 'c3', 'd3');

INSERT INTO quality.ground_truth(
	column1, column2, column3, column4)
	VALUES ('a3', 'b3', 'c3', 'd3');
	
	--row4
	INSERT INTO quality.output(
	column1, column2, column3, column4)
	VALUES ('a4', 'b4', 'c3', 'd4');

INSERT INTO quality.ground_truth(
	column1, column2, column3, column4)
	VALUES ('a4', 'b4', 'c4', 'd4');
	
	--row5
	INSERT INTO quality.output(
	column1, column2, column3, column4)
	VALUES ('a5', null, null, null);

INSERT INTO quality.ground_truth(
	column1, column2, column3, column4)
	VALUES ('a5', 'b5', 'c5', 'd5');
	
	--row6
	INSERT INTO quality.output(
	column1, column2, column3, column4)
	VALUES ('a6', 'b4', 'c4', 'd4');

INSERT INTO quality.ground_truth(
	column1, column2, column3, column4)
	VALUES ('a6', 'b6', 'c6', 'd6');
	
	--row7
	INSERT INTO quality.output(
	column1, column2, column3, column4)
	VALUES ('a7', null, 'c1', 'd1');

INSERT INTO quality.ground_truth(
	column1, column2, column3, column4)
	VALUES ('a7', 'b7', 'c7', 'd7');
	
	--row8
INSERT INTO quality.ground_truth(
	column1, column2, column3, column4)
	VALUES ('a8', 'b8', 'c8', 'd8');
	
	--row9
	INSERT INTO quality.output(
	column1, column2, column3, column4)
	VALUES ('a9', 'b9', 'c9', 'd9');

	