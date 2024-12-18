
# create Schedule table
create table schedule (
                          id BIGINT not null auto_increment,
                          title varchar(50) not null,
                          description TEXT,
                          created_at DATETIME not null,
                          updated_at DATETIME,
                          deleted_at DateTime,
                          author varchar(50),
                          password varchar(25),
                          primary key (id)
);

# insert Schedule
INSERT INTO schedule
    (author, title, created_at, password, description, updated_at, deleted_at)
VALUES
    ('someon',
     'test',
     '2024-01-01-00-00',
     '1234',
     'sth',
     null,
     null);

# 전체 일정을 조회
select * from schedule;

# 선택 일정을 조회
select * from schedule where id = 2;

# 선택 일정을 수정
UPDATE schedule SET title = 'hello',
                    author = 'someone2',
                    description = 'test',
                    updated_at = '2024-11-01-00-03',
                    password = '1234',
                    created_at = '2024-01-01-00-00',
                    deleted_at = null
                WHERE id = 3;


# 선택 일정 삭제
delete from schedule where id = 2;

# 전체 일정 조회 중 updateDate와 Auther 정보만 갖고 조회
select * from schedule
         where (updated_at = '2024-11-01' or null)
            or
             (author = 'someone2' or null)
         order by updated_at desc;

# 날짜로 찾기
select * from schedule where updated_at = '2024-11-01-00-03';

#==========================================================================

# 회원 table 생성
create table member (
    id BIGINT not null primary key,
    userId varchar(25) not null,
    email varchar(50) not null,
    updated_at DATETIME null,
    name varchar(25) not null
);

alter table member change userId user_id varchar(25) not null;

ALTER TABLE member
ADD COLUMN password VARCHAR(255) NOT NULL;
desc member;
desc schedule;

ALTER TABLE schedule
    ADD CONSTRAINT fk_member
        FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;

SELECT user_id, COUNT(*)
FROM member
GROUP BY user_id
HAVING COUNT(*) > 1;

ALTER TABLE member ADD CONSTRAINT unique_user_id UNIQUE (user_id);

SHOW INDEX FROM member WHERE Non_unique = 0 AND Column_name = 'user_id';
ALTER TABLE member DROP INDEX unique_user_id;
SHOW INDEX FROM member;
desc schedule;
desc member;
