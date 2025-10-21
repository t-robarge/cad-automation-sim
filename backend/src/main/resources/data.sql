insert into part (part_number,name,unit,created_at) values ('P-1001','Bracket A','MM', now());
insert into feature (type,x,y,d1,d2,part_id) values
('HOLE', 30, 40, 8, 0, 1),
('EDGE', 10, 20, 120, 0, 1),
('FACE', 50, 60, 80, 25, 1);
