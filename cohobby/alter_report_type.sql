-- Report 테이블의 type과 status 컬럼 크기 확장
ALTER TABLE report MODIFY COLUMN type VARCHAR(50);
ALTER TABLE report MODIFY COLUMN status VARCHAR(50);

