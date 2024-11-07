# schedule
## ⚒️ Tools 
 <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=github&logoColor=Green"> <img alt="Java" src ="https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white"/>  <img alt="Java" src ="https://img.shields.io/badge/intellijidea-000000.svg?&style=for-the-badge&logo=intellijidea&logoColor=white"/>
--
## 👨‍💻 Period : 2024/10/29 ~ 2024/11/06
## ERD 
![ERD](https://github.com/user-attachments/assets/d9d23a23-2b89-44cc-a3c1-065b5166fe90)
## API 명세서
**Lv1&Lv2**

<a href>https://www.postman.com/gold-robot-131519/myapi/documentation/2zga2q0/lv1-lv2?workspaceId=c64232d4-fdd3-46da-b127-41e93826dc0a</a-href>

**Lv3 & Lv4 & Lv5**

MemberAPI

<a href>https://www.postman.com/gold-robot-131519/workspace/myapi/documentation/25410109-65a8add9-a0e1-4378-8252-46907d9a58d3/publish?workspaceId=c64232d4-fdd3-46da-b127-41e93826dc0a</a-href>

**ScheduleAPI**

<a href>https://www.postman.com/gold-robot-131519/myapi/documentation/4xuox4d/scheduleapi?workspaceId=c64232d4-fdd3-46da-b127-41e93826dc0a</a-href>

## 👨‍💻 기능 설명 
### - JdbcTemplate 사용 (Lv1 & Lv2)
  - Entity : Schedule
  - Controller : ScheduleController
  - Dto : ScheduleDto, UpdatedScheduleDto, SingleDateScheduleDto
  - Converter : ScheduleConverter (entity -> Dto, Dto -> entity)
  - Repository : ScheduleRepositoryImpl 
  - Service : ScheduleServiceImpl

        - Lv 1 :
          Create - 할일, 작성자명, 상세 내용, 수정 날짜, 생성 날짜, 삭제 날짜, 비밀번호를 생성 할 수 있도록 함 
          findSchedulesByUpdatedDateAndAuthor - 적성자 명과 updated날짜로 조회,
          findDateById - 선택 일정 조회 (id 값으로 조회 할 수 있도록 함)
          findDateById - 선택한 일정 정보 불러오기
          findByUpdatedDateDesc - update 날짜 내림차순 조호 
    
        - LV 2 :
          updateTitleAndAuthor - 선택 일정 수정
          deleteById - 선택 일정 삭제 (password가 동일해야 넘어감

        - 추가로 기능 구현 한 부분
          findByDate : 동일 날짜가 여러개 일 수 있으므로 List 처리   
 
  - Validation : ScheduleValidation  

### - Lv3-with-Member
  **Schedule**
  - Entity : Schedule
  - Controller : ScheduleController

        - findScheduleByMemberId : 회원과 스케줄을 join 해서 동시 조회
          findSchedulesByMemberId : 회원과 스케줄을 join 해서 동시 조회 부분을 List화
  - Dto : ScheduleDto, UpdatedScheduleDto, SingleDateScheduleDto
  - Converter : ScheduleConverter (entity -> Dto, Dto -> entity)
  - Repository : ScheduleRepositoryImpl

        - 기존 Lv1 & 2 작성한 Repository 코드을 Membe와 Join 해서 SQL 작성
        - RowMapper에 회원 부분 추가
        - 스케줄 부분만 조회할 필요가 있다고 느껴 Schedule만 존재하는 RowMapper 

  - Service : ScheduleServiceImpl

        - Lv 3 :
          findScheduleByMemberId : 회원과 스케줄을 join 해서 동시 조회
          findSchedulesByMemberId : 회원과 스케줄을 join 해서 동시 조회 부분을 List화
    
  - Validation : ScheduleValidation
 
  **Member**
  - Entity : Member
  - Controller : MemberController
  - Dto : MemberDto
  - Converter : MemberConverter (entity -> Dto, Dto -> entity)
  - Repository : MemberRepository
  - Service : MemberService
  
        - Create : 회원 생성
        - findById : pk 값으로 아이디 조회
        - findByUserIde : userId로 회원 조회
        - update : 회원 전체 수정
        - deleteById : pk 값으로 아이디 삭제
        
    - validation : MemberValidation

### - Lv4 & Lv 5 
  **Schedule**
  - Entity : Schedule
  - Controller : ScheduleController

        - Controller 부분도 Paging 처리 
        - 각 CustomError 처리
    
  - Dto : ScheduleDto, UpdatedScheduleDto, SingleDateScheduleDto, DeleteScheduleRequest, SearchDto (paging 부분을 Dto화)
  - Converter : ScheduleConverter (entity -> Dto, Dto -> entity)
  - Repository : ScheduleRepositoryImpl

        - Lv 4 : List를 처리 했던 부분을 Paging 처리, totalCount가 offset을 초과하면, Empty로 반환 
        findAllOrderByUpdatedDateDesc 
        findSchedulesByUpdatedDateAndAuthor
        findByDate
        findSchedulesByMemberId
        - 각 CustomError 처리 

  - Service : ScheduleServiceImpl

        - Lv 4 : List를 처리 했던 부분을 Paging 처리
        findByDate
        findByUpdatedDateDesc
        findSchedulesByMemberId
        findByUpdatedDateAndAuthor
    
  - Validation : ScheduleValidation
 
  **Member**
  - Entity : Member
  - Controller : MemberController

        - 각 CustomError 처리 
  - Dto : MemberDto
  - Converter : MemberConverter (entity -> Dto, Dto -> entity)
  - Repository : MemberRepository

        - 각 CustomError 처리  
  - Service : MemberService
  
        - Create : 회원 생성
        - findById : pk 값으로 아이디 조회
        - findByUserIde : userId로 회원 조회
        - update : 회원 전체 수정
        - deleteById : pk 값으로 아이디 삭제
        - 각 CustomError 처리 

    - validation : MemberValidation

 **Error**
 - ErrorDto
 - ErrorType : 각 상황에 맞는 Error 별로 Custom화
 - CustomException : RuntimeException을 상속 받음
 - GlobalExceptionHandler : RestControllerAdvise를 사용해서 Controllre에서 발생하는 Error 처리 & 그 밖에 예외는 InternalException으로 처리

### - Lv 6 Vaild
  **Schedule**
  - Entity : Schedule
  - Controller : ScheduleController

        - Controller 부분도 Paging 처리 
        - 각 CustomError 처리
    
  - Dto : ScheduleDto, UpdatedScheduleDto, SingleDateScheduleDto, SearchDto (paging 부분을 Dto화)
  
        - ScheduleDto에 Vaild 처리
          id : 빈값 방지
          title : 200자 내외, 빈값 방지
          author : 작성자 빈값 방지
          passsword : 빈값 방지
          description : 상세 내용 500자 내외 
          
     
  - Converter : ScheduleConverter (entity -> Dto, Dto -> entity)
  - Repository : ScheduleRepositoryImpl

        - Lv 4 : List를 처리 했던 부분을 Paging 처리, totalCount가 offset을 초과하면, Empty로 반환 
        findAllOrderByUpdatedDateDesc 
        findSchedulesByUpdatedDateAndAuthor
        findByDate
        findSchedulesByMemberId
        - 각 CustomError 처리 

  - Service : ScheduleServiceImpl

        - Lv 4 : List를 처리 했던 부분을 Paging 처리
        findByDate
        findByUpdatedDateDesc
        findSchedulesByMemberId
        findByUpdatedDateAndAuthor
    
  - Validation : ScheduleValidation
 
  **Member**
  - Entity : Member
  - Controller : MemberController

        - 각 CustomError 처리 
  - Dto : MemberDto

        - MemberDto에 Valid 처리
         
  - Converter : MemberConverter (entity -> Dto, Dto -> entity)
  - Repository : MemberRepository

        - 각 CustomError 처리
    
  - Service : MemberService
  
        - Create : 회원 생성
        - findById : pk 값으로 아이디 조회
        - findByUserIde : userId로 회원 조회
        - update : 회원 전체 수정
        - deleteById : pk 값으로 아이디 삭제
        - 각 CustomError 처리
    
    - validation : MemberValidation

 **Error**
 - ErrorDto
 - ErrorType : 각 상황에 맞는 Error 별로 Custom화
 - CustomException : RuntimeException을 상속 받음
 - GlobalExceptionHandler : RestControllerAdvise를 사용해서 Controllre에서 발생하는 Error 처리 & 그 밖에 예외는 InternalException으로 처리

-- 
## 🚀 Refactoring 
- update & title을 조히하는 부분을 sql로 작성했다가, 비즈니스 로직에서 해결
- Validation : Dto 값과 Enitiy에 있는 Data 값들을 비교 했다가, Controller에서 RequestBody를 적용할때 RequestBody는 한번만 적용되는데 두 값을 날려야 하다 보니 postMan에서 input 값으로 비교하자라고 생각이 들어 변경 
- Paging 처리 : 페이지 수 초과시 빈 값으로 return
- Dto Valid 부분 : 글자수 수정
- LocalDateTime -> Date로 통일 : LocalDateTime을 사용하다가 pattern에 맞지 않을때 error가 발생해 Date로 변경
- ErrorCode : 좀더 기능별로 Error 발생 할 수 있도록 세분화
- 불필요한 Validation 삭제 회원을 생성하는데 아이디가 있는지 유무를 확인할 필요는 없다고 생각이 들어 그 부분 삭제 

-- 
## 🥵 TroubleShooting
**1. 쿼리 문제**
처음 작성한 쿼리 
```
SELECT *
FROM schedule
WHERE grouping(updated_at)
ORDER BY updated_at DESC;
```
수정한 쿼리
```
SELECT *
FROM schedule
WHERE (updated_at IS NOT NULL OR author = 'someone')
ORDER BY updated_at DESC;
```

처음 작성한 쿼리 
```
String sql = "DELETE FROM member WHERE id = ?";
jdbcTemplate.query(sql, memberId);
```
수정한 쿼리
```
String sql = "DELETE FROM member WHERE id = ?";
jdbcTemplate.update(sql, memberId);
```
데이터 변경 작업을 할땐 update 사용 

처음 작성한 쿼리 
```
String sql = "INSERT INTO member (user_id, password, name, email, updated_at) VALUES (?, ?, ?, ?, ?)";
jdbcTemplate.update(sql, member.getUserId(), member.getPassword(), member.getName(), member.getEmail(), member.getUpdatedAt(), member.getId()); // 오류 발생

```
수정한 쿼리
```
String sql = "INSERT INTO member (user_id, password, name, email, updated_at) VALUES (?, ?, ?, ?, ?)";
jdbcTemplate.update(sql, member.getUserId(), member.getPassword(), member.getName(), member.getEmail(), member.getUpdatedAt());
```
---

**2. 코드 문제**
1. Foreign Key Constraint 오류
Schedule과 회원을 Join 한 뒤 스케줄을 생성하려면 회원의 값을 생성 한 후 스케줄을 생성할 수 있었고, 삭제 또한 회원이 삭제 되지 않으면
스케줄도 삭제되지 않는 문제가 있었다.

문제 코드
```
public void deleteAllMembers() {
    String sql = "DELETE FROM member";
    jdbcTemplate.update(sql); // 이 시점에서 외래 키 오류 발생
}
```
수정한 코드
```
public void deleteAllMembersAndSchedules() {
    // 자식 테이블 데이터 삭제
    String deleteSchedulesSql = "DELETE FROM schedule";
    jdbcTemplate.update(deleteSchedulesSql);

    // 부모 테이블 데이터 삭제
    String deleteMembersSql = "DELETE FROM member";
    jdbcTemplate.update(deleteMembersSql);
}
```
CASCADE를 설정해 회원이 삭제되면 스케줄도 삭제 되게 변경했다. 
```
ALTER TABLE schedule
ADD CONSTRAINT fk_member
FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
```
2. RowMapper 문제
Join을 진행했으면 스케줄 Repository의 RowMapper 부분에 회원에 대한 부분도 있어야 하지만, 없어서 조회시 문제가 발생 
스케줄 RowMapper만 있었던 부분에 Member 추가

3. 중복된 결과로 인해 발생한는 IncorrectResultSize
test를 돌렸을때 쿼리에 대한 값이 하나만 나오기를 예상했지만 쿼리가 두개가 나가는 상황
1) userId를 유니크 키 값 설정
2) 나중에 user_Id의 유니크 값 설정을 풀고, 매 test값을 초기화 함 -> @Transaction과 Beforeach를 사용해도 되었지만, 사용하지 않고 repository에서 초기화 하는 코드를 작성해 초기화 진행 후 test 실행
---

4. NullPointerException (가장 힘들었다..)
1) schedule.getMember()가 null이기 때문에 getId()를 호출할 수 없었음.
```
  public Schedule updateSchedule(Member member, Schedule schedule) {
        if (schedule.getId() == null) {
            throw new IllegalArgumentException("해당 id가 존재하지 않습니다.");
        }

        String sql = "UPDATE schedule SET title = ?, author = ?, description = ?, updated_at = ?, password = ?, created_at = ?, deleted_at = ?, member_id = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                schedule.getTitle(),
                schedule.getAuthor(),
                schedule.getDescription(),
                schedule.getUpdatedAt(),
                schedule.getPassword(),
                schedule.getCreatedAt(),
                schedule.getDeletedAt(),
                member.getId(),
                schedule.getId());  // 마지막 파라미터로 id 추가

        if (updatedRows == 0) {
            throw new IllegalArgumentException("업데이트 실패");
        }
        return schedule;
    }
```
완성된 Code이지만 updatedRows에 member.getId 부분을 빼먹음 

2) existingMember -> Validation에서의 NPE
```
Cannot invoke "com.example.dailyschedule.domain.Schedule.getId()" because "exsitSchedule" is null
java.lang.NullPointerException: Cannot invoke "com.example.dailyschedule.domain.Schedule.getId()" because "exsitSchedule" is null
	at com.example.dailyschedule.service.ScheduleServiceImpl.deleteById(ScheduleServiceImpl.java:118)
``` 
비즈니스로직에서까지 잘 넘어가지만, controller에서 호출이 안됨
```
    public ScheduleDto toDto(Schedule schedule) {

        return ScheduleDto.builder()
                .id(schedule.getId())
                .author(schedule.getAuthor())
                .title(schedule.getTitle())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .deletedAt(schedule.getDeletedAt())
                .description(schedule.getDescription())
                .password(schedule.getPassword())
                .memberDto(schedule.getMember() != null ? memberConverter.toDto(schedule.getMember()) : null)
                .build();
    }
```
알고보니, 스케줄 Dto에서 memeber를 변환하는 부분을 빼멱음... 

3) findByUpdatedDateDesc 메서드에서 byUpdatedDateByDesc 변수가 null인 상태에서 .isEmpty()와 같은 메서드를 호출할 때 NullPointerException이 발생
```
    //Page로 update 날짜 내림차순 조회
    public Page<Schedule> findAllOrderByUpdatedDateDesc(SearchDto searchDto) {

        int limit = searchDto.getLimit();
        int offset = searchDto.getOffset();

        // 전체 레코드 수를 계산하는 쿼리
        String countSql = "SELECT COUNT(*) FROM schedule";
        Long totalCount = jdbcTemplate.queryForObject(countSql, Long.class);

        if (totalCount == 0 || offset >= totalCount) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(offset / limit, limit), totalCount);
        }

        String sql = """
                SELECT s.*, 
                       m.id AS member_id, m.user_id AS user_id, m.password AS member_password, 
                       m.name AS member_name, m.email AS member_email, m.updated_at AS member_updated_at
                  FROM schedule s
                  LEFT JOIN member m ON s.member_id = m.id
                 ORDER BY s.updated_at DESC
                 LIMIT ? OFFSET ?
            """;

        List<Schedule> schedules = jdbcTemplate.query(sql, new Object[]{limit, offset}, scheduleRowMapper());

        return new PageImpl<>(schedules, PageRequest.of(offset / limit, limit), totalCount);
    }
```

처음에 Optional로 처리했다가 계속 Empty값이 나와, 직접 예외 처리를 하고, 알고보니 sql의 value 값이 잘 못들어가있는 것을 확인 했다. 

4) reated_at 필드 문제 (null 오류)
```
public Schedule createSchedule(Schedule schedule) {
    String sql = "INSERT INTO schedule (author, title, created_at, password, description, updated_at, deleted_at, member_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    jdbcTemplate.update(sql,
            schedule.getAuthor(),
            schedule.getTitle(),
            schedule.getCreatedAt(),  // createdAt이 null일 경우 오류 발생
            schedule.getPassword(),
            schedule.getDescription(),
            schedule.getUpdatedAt(),
            schedule.getDeletedAt(),
            schedule.getMember().getId()
    );
}
```
1번째 시도 
```
public Schedule createSchedule(Schedule schedule) {
    String sql = "INSERT INTO schedule (author, title, created_at, password, description, updated_at, deleted_at, member_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    jdbcTemplate.update(sql,
            schedule.getAuthor(),
            schedule.getTitle(),
            schedule.getCreatedAt() != null ? schedule.getCreatedAt() : LocalDateTime.now(),
            schedule.getPassword(),
            schedule.getDescription(),
            schedule.getUpdatedAt() != null ? schedule.getUpdatedAt() : LocalDateTime.now(),
            schedule.getDeletedAt(),
            schedule.getMember().getId()
    );
}
```
2번째 시도 
```
public Schedule createSchedule(Schedule schedule) {
    String sql = "INSERT INTO schedule (author, title, created_at, password, description, updated_at, deleted_at, member_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    jdbcTemplate.update(sql,
            schedule.getAuthor(),
            schedule.getTitle(),
            schedule.getCreatedAt(),
            schedule.getPassword(),
            schedule.getDescription(),
            schedule.getUpdatedAt(),
            schedule.getDeletedAt(),
            schedule.getMember().getId()
    );
}
```
LocalDateTime을 Date로 변경했다. 또한 LocalDateTime의 pattern을 일치시키는데 어려움이 있어 Date로 우회 함 

5) findById에서의 NPE 문제
findById 메서드에서 조회할 때 데이터가 존재하지 않을 경우
```
public Member findById(Long id) {
    if (id == null) {
        throw new IllegalArgumentException("해당 id 값이 없습니다.");
    }

    String sql = "select * from member where id = ?";

    try {
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, memberRowMapper());
    } catch (EmptyResultDataAccessException e) {
        return null; // 또는 Optional.empty()로 반환하여 예외 처리
    }
}
```
EmptyResultDataAccessException에서 null이 발생해 retrun을 null로 처리 
---

5. AutoIncrement 사용했을때 최근 사용된 키 가져오는 문제
```
   public Schedule createSchedule(Schedule schedule, Member member) {

        String sql = "INSERT INTO schedule (author, title, created_at, password, description, updated_at, deleted_at, member_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                schedule.getAuthor(),
                schedule.getTitle(),
                schedule.getCreatedAt(),
                schedule.getPassword(),
                schedule.getDescription(),
                schedule.getUpdatedAt(),
                schedule.getDeletedAt(),
                member.getId() // Member 객체에서 member_id를 가져옴
        );

        // 최근 추가된 ID 조회
        Long generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        return Schedule.builder()
                .id(generatedId)
                .author(schedule.getAuthor())
                .title(schedule.getTitle())
                .password(schedule.getPassword())
                .description(schedule.getDescription())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .deletedAt(schedule.getDeletedAt())
                .member(member) // Member 객체 설정
                .build();
    }
```
최근 조회된 Id 값을 가져오는 Last_insert_id를 사용 
또 다른 방법으로는 GeneratedKeyHolder를 사용해 DB에 종속되지 않는 방식으로 새로 생성된 ID를 가져오는 방법이 있음 

---

6. 예외 처리 부족
1 ) update
```
    //udpate
    public Schedule updateSchedule(Member member, Schedule schedule) {
        if (schedule.getId() == null) {
            throw new IllegalArgumentException("해당 id가 존재하지 않습니다.");
        }

        String sql = "UPDATE schedule SET title = ?, author = ?, description = ?, updated_at = ?, password = ?, created_at = ?, deleted_at = ?, member_id = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                schedule.getTitle(),
                schedule.getAuthor(),
                schedule.getDescription(),
                schedule.getUpdatedAt(),
                schedule.getPassword(),
                schedule.getCreatedAt(),
                schedule.getDeletedAt(),
                member.getId(),
                schedule.getId());  // 마지막 파라미터로 id 추가
        return schedule;
    }
```

```
    //udpate
    public Schedule updateSchedule(Member member, Schedule schedule) {
        if (schedule.getId() == null) {
            throw new IllegalArgumentException("해당 id가 존재하지 않습니다.");
        }

        String sql = "UPDATE schedule SET title = ?, author = ?, description = ?, updated_at = ?, password = ?, created_at = ?, deleted_at = ?, member_id = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                schedule.getTitle(),
                schedule.getAuthor(),
                schedule.getDescription(),
                schedule.getUpdatedAt(),
                schedule.getPassword(),
                schedule.getCreatedAt(),
                schedule.getDeletedAt(),
                member.getId(),
                schedule.getId());  // 마지막 파라미터로 id 추가

        if (updatedRows == 0) {
            throw new IllegalArgumentException("업데이트 실패");
        }
        return schedule;
    }
```
row가 0일 경우 쿼리가 나가지 않았다는 의미 이기 때문에 "=0"으로 처리했다.  

2 ) delete 
```
   //스케줄 아이디로 삭제
    public void deleteScheduleById(Long scheduleId) {

        String sql = "delete from schedule where id = ?";

        int delete = jdbcTemplate.update(sql, scheduleId);

        if (delete == 0) {
            throw new IllegalArgumentException("삭제 살패했습니다.");
        }
    }
```
delete 또한 마찮가지 

3) testCode 작성했을때 IllegalArgumentException 예외 처리 부족

---

7. Paging 처리시 Limit 과 Offset 처리
offset이 Limit을 초과하게 설정해서 Error가 발생했다. 

---

8. Controller에서 GetMapping시 parameter 불일치
```
  @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateScheduleDto updateScheduleDto) {
        try {
            // updateTitleAndAuthor 메서드를 호출하여 업데이트 수행
            ScheduleDto updatedSchedule = scheduleService.updateTitleAndAuthor(scheduleId, updateScheduleDto);
            return ResponseEntity.status(HttpStatus.OK).body(updatedSchedule);
        } catch (CustomException e) {
            log.error("일정을 수정하는데 실패했습니다. : {}", e.getMessage());
            throw new CustomException(ErrorCode.UPDATE_FAILED);
        }
    }
```
pathVariable 일치 시킴 
```
  @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody UpdateScheduleDto updateScheduleDto) {
        try {
            // updateTitleAndAuthor 메서드를 호출하여 업데이트 수행
            ScheduleDto updatedSchedule = scheduleService.updateTitleAndAuthor(scheduleId, updateScheduleDto);
            return ResponseEntity.status(HttpStatus.OK).body(updatedSchedule);
        } catch (CustomException e) {
            log.error("일정을 수정하는데 실패했습니다. : {}", e.getMessage());
            throw new CustomException(ErrorCode.UPDATE_FAILED);
        }
    }
```
9. 작가명과 수정날짜로 조회 할때 500 발생
1) 디버그를 돌려봤지만 값이 서비스에도 들어가지 않는다는 것을 알았다. 알고보니

```
updated_at = ? or udpated_at =? is null =?
```
이렇게 작성을 해놨다. value 값이 안맞으니 당연히 안들어가지..

수정 Code 
```
 String sql = """
                    SELECT s.*, 
                           m.id AS member_id, m.user_id AS user_id, m.password AS member_password, 
                           m.name AS member_name, m.email AS member_email, m.updated_at AS member_updated_at
                      FROM schedule s
                      LEFT JOIN member m ON s.member_id = m.id
                      WHERE DATE(s.updated_at) = DATE(?) 
                       AND s.author = ?
                     ORDER BY s.updated_at DESC
                """;
```
그리고는 들어가는 것을 확인 

2) 하지만 404로 변경됨
지금은 삭제했지만 이전에 ScheduleDto와 MemberDto를 하나로 합쳐서 Controller에 RequestBody로 적용해야겠다라는 생각을 했다.
문제는 MemberDto가 제대로 Mapping이 되지 않았고, AllArg, NoArg도 없었기 때문에 즉 constructor가 없았기 때문에 해당 값을 인식 못함
생각을 해보니... ScheduleDto에 MemberDto를 넣었는데 굳이 하나로 합쳐서 적요할 필요가 없다고 생각이 들어 해당 클래스를 삭제하고, ScheduleDto로
값을 받는 것으로 설정했더니... 하 되었더라...

10. 스케줄 생성할때 Member에 대한 값을 가져오지 못함
우선 SQL문에서 Join 문제인가 라고 생각했다. 하지만 SQL에 직접 쿼리를 실행했을때는 잘 가져왔다. 뭔가 하니 ScheduleRepositoryImpl 부분에 RowMapper에 Schedule에 대한 부분만 있고 Member에 대한 부분이 없어서 발생한 오류였다고 판단해서 했지만, 여전히 null
converter 부분을 Member를 포함시키지 않은것이 문제였다. 
그래서 Member를 포함하고 있는 Converter와 포함하지 않은 Converter로 나눴다. 

--- 
## 👨‍💻 보안할 점 
1. 스케줄을 조회 할때 Member에 대한 정보중 id 값 까지만 불러왔으면 좋겠지만 불필요한 부가 정보까지 불러오는 문제가 있다. Response 값에서 제어를 하는 방법중 찾아보니 JsonIgnore 라는 기능이 있는 것 같아 추후 적용할 예정 
2. SQL문 join 문에 전보다는 익숙해 졌지만 아직 paging 처리라던지 paging 처리 할때 page 초과할 경우 예외를 두는 부분이 어려웠음.
3. Test Code를 짠다고 짰지만 NPE를 못잡는 경우, Controller에서 터지는 Error들을 못잡는 것을 확인, 좀더 세밀하게 짜야 겠다는 생각이 들었다.

--- 
## 👍 좋았던 점 
개인 과제이지만 Team을 이루어 진행했다. Team에서 leader를 맡게 되었고, 나도 많이 부족하지만, Team원 분들이 어렵거나 모르는 부분이 있다면 같이 찾아보고, 한번도 들어가보지 않았던 JDBC Template 내부 기능도 들어가서 Connection, Preparestatement, ReseultSet이 있는 것을 확인하는 계기가 되었다. 
그리고 Controller에서 PathVariable, RequestBody의 annotation을 직접 들어가서 내 눈으로 그 안에 어던 기능이 있는지 다시 한번 확인 하는 계기가 되었다. 
또한 나는 요구사항을 다 적었다고 생각했지만, 요구사항이 빠진 부분을 팀원 분들중 한분께서 발견해주셨고, 그 부분을 덕분에 수정해서 반영하게 되었다. 




