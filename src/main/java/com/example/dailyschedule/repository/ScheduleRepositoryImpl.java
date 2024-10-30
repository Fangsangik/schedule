package com.example.dailyschedule.repository;
import com.example.dailyschedule.config.DataSourceConfig;
import com.example.dailyschedule.domain.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Repository
public class ScheduleRepositoryImpl {

    private final DataSourceConfig dataSource;
    public ScheduleRepositoryImpl(DataSourceConfig dataSource) throws SQLException {
        this.dataSource = dataSource;
    }


    public Schedule createSchedule(Schedule schedule) {
        String sql = "INSERT INTO schedule (author, title, createdAt, password, description, updatedAt, deletedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";


        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, schedule.getAuthor());
            preparedStatement.setString(2, schedule.getTitle());
            preparedStatement.setObject(3, schedule.getCreatedAt());
            preparedStatement.setString(4, schedule.getPassword());
            preparedStatement.setString(5, schedule.getDescription());
            preparedStatement.setObject(6, schedule.getUpdatedAt());
            preparedStatement.setObject(7, schedule.getDeletedAt());

            int affectedRows = preparedStatement.executeUpdate();

            // 데이터 삽입이 성공적이었는지 확인
            if (affectedRows == 0) {
                throw new SQLException("스케줄 저장에 실패했습니다. 삽입된 행이 없습니다.");
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                return Schedule.builder()
                        .id(generatedKeys.getLong(1))  // 자동 생성된 ID로 새로운 Schedule 객체 생성
                        .author(schedule.getAuthor())
                        .title(schedule.getTitle())
                        .createdAt(schedule.getCreatedAt())
                        .password(schedule.getPassword())
                        .description(schedule.getDescription())
                        .updatedAt(schedule.getUpdatedAt())
                        .deletedAt(schedule.getDeletedAt())
                        .build();
            } else {
                throw new SQLException("ID를 가져오는 데 실패했습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("스케줄 저장하는데 실페했습니다.", e);
        } finally {
            closeResources(connection, preparedStatement, resultSet);

        }
    }


    public Schedule updateSchedule(Schedule schedule) {
        if (schedule.getId() == null) {
            throw new IllegalArgumentException("해당 id가 존재하지 않습니다.");
        }

        String sql = "UPDATE schedule SET title = ?, author = ?, description = ?, updatedAt = ?, password = ?, createdAt = ?, deletedAt = ? WHERE id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            // 파라미터 설정: SQL 쿼리 순서에 맞게 설정
            preparedStatement.setString(1, schedule.getTitle());
            preparedStatement.setString(2, schedule.getAuthor());
            preparedStatement.setString(3, schedule.getDescription());
            preparedStatement.setObject(4, schedule.getUpdatedAt());
            preparedStatement.setString(5, schedule.getPassword());
            preparedStatement.setObject(6, schedule.getCreatedAt());
            preparedStatement.setObject(7, schedule.getDeletedAt());
            preparedStatement.setLong(8, schedule.getId()); // WHERE 절의 ID

            // 쿼리 실행 및 업데이트 확인
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("해당 id 스케줄을 찾을 수 없습니다.");
            }

            // 업데이트된 Schedule 객체 반환
            return Schedule.builder()
                    .id(schedule.getId())
                    .title(schedule.getTitle())
                    .author(schedule.getAuthor())
                    .description(schedule.getDescription())
                    .updatedAt(schedule.getUpdatedAt())
                    .password(schedule.getPassword())
                    .createdAt(schedule.getCreatedAt())
                    .deletedAt(schedule.getDeletedAt())
                    .build();


        } catch (SQLException e) {
            throw new RuntimeException("update에 실패했습니다.", e);
        } finally {
            closeResources(connection, preparedStatement, resultSet);

        }
    }

    public Schedule findScheduleById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("해당 id 값이 null 입니다.");
        }

        // 필요한 모든 필드를 조회하도록 쿼리 작성
        String sql = "SELECT id, author, title, password, description, createdAt, updatedAt, deletedAt FROM schedule WHERE id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);

            System.out.println("Searching for Schedule ID: " + id);

            resultSet = preparedStatement.executeQuery();

            // 결과가 존재할 경우 Schedule 객체를 생성하여 반환
            if (resultSet.next()) {
                Schedule schedule = Schedule.builder()
                        .id(resultSet.getLong("id"))
                        .author(resultSet.getString("author"))
                        .title(resultSet.getString("title"))
                        .password(resultSet.getString("password"))
                        .description(resultSet.getString("description"))
                        .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("updatedAt", LocalDateTime.class))
                        .deletedAt(resultSet.getObject("deletedAt", LocalDateTime.class))
                        .build();
                System.out.println("Found Schedule: " + schedule);
                return schedule;
            }

        } catch (SQLException e) {
            throw new RuntimeException("해당 스케줄을 조회 할 수 없습니다.", e);
        } finally {

            closeResources(connection, preparedStatement, resultSet);

        }

        return null;
    }

    public void deleteScheduleById(Long scheduleId) {
        if (scheduleId == null) {
            throw new IllegalArgumentException("해당 아이디가 없습니다.");
        }

        String sql = "delete from schedule where id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setLong(1, scheduleId);

            int update = preparedStatement.executeUpdate();
            if (update == 0) {
                throw new IllegalArgumentException("해당 Id의 스케줄이 존재하지 않습니다.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("해당 schedule을 삭제하는데 실패했습니다.");
        } finally {
            closeResources(connection, preparedStatement, resultSet);

        }
    }


    public void deleteAll() {
        String sql = "delete from schedule";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("data를 삭제하는데 실패했습니다.");
        } finally {
            closeResources(connection, preparedStatement, resultSet);


        }
    }


    public Schedule findAllByUpdatedDateAndAuthor(LocalDateTime updatedAt, String author) {
        if (updatedAt == null && author == null) {
            throw new IllegalArgumentException("해당 이름으로 수정된 날짜를 찾을 수 없습니다.");
        }

        String sql = "SELECT * from schedule " +
                " WHERE (updatedAt = ? or ? is null = ?)" +
                "AND (author = ? or ? is null)" +
                "Order by updatedAt desc";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, updatedAt);
            preparedStatement.setString(2, author);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Schedule schedule = Schedule.builder()
                        .id(resultSet.getLong("id"))
                        .author(resultSet.getString("author"))
                        .title(resultSet.getString("title"))
                        .password(resultSet.getString("password"))
                        .description(resultSet.getString("description"))
                        .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("updatedAt", LocalDateTime.class))
                        .deletedAt(resultSet.getObject("deletedAt", LocalDateTime.class))
                        .build();

                return schedule;
            }

        } catch (SQLException e) {
            throw new RuntimeException("해당 조회 값을 찾을 수 없습니다.");
        } finally {
            closeResources(connection, preparedStatement, resultSet);

        }

        return null;
    }

    public List<Schedule> findAllOrderByUpdatedDateDesc() {
        String sql = "SELECT id, title, author, password, description, createdAt, updatedAt, deletedAt FROM schedule " +
                "ORDER BY updatedAt DESC";

        List<Schedule> schedules = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Schedule schedule = Schedule.builder()
                        .id(resultSet.getLong("id"))
                        .author(resultSet.getString("author"))
                        .title(resultSet.getString("title"))
                        .password(resultSet.getString("password"))
                        .description(resultSet.getString("description"))
                        .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("updatedAt", LocalDateTime.class))
                        .deletedAt(resultSet.getObject("deletedAt", LocalDateTime.class))
                        .build();

                schedules.add(schedule);
            }

        } catch (SQLException e) {
            throw new RuntimeException("스케줄 조회에 실패했습니다.", e);
        }

        return schedules;
    }

    public Schedule findByDate(LocalDateTime date) {

        if (date == null) {
            throw new IllegalArgumentException("해당 날짜가 없습니다.");
        }

        String sql = "SELECT id, title, author, password, description, createdAt, updatedAt, deletedAt FROM schedule " +
                "WHERE createdAt = ? OR updatedAt = ? OR deletedAt = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, date);
            preparedStatement.setObject(2, date);
            preparedStatement.setObject(3, date);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Schedule schedule = Schedule.builder()
                        .id(resultSet.getLong("id"))
                        .author(resultSet.getString("author"))
                        .title(resultSet.getString("title"))
                        .password(resultSet.getString("password"))
                        .description(resultSet.getString("description"))
                        .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                        .updatedAt(resultSet.getObject("updatedAt", LocalDateTime.class))
                        .deletedAt(resultSet.getObject("deletedAt", LocalDateTime.class))
                        .build();

                return schedule;
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("스케줄 조회에 실패했습니다.");
        } finally {
            closeResources(connection, preparedStatement, resultSet);
        }
        return null;
    }

    //Lv2
    //sql을 효율적으로 사용해서 resource를 잘 사용
    //병목 현상을 DB에서 가져갈 꺼냐,
    //Lock, DB에서 정합성
    public Schedule updateByDateByAuthorByTitle(Long id, LocalDateTime date, String author, String title, String password) {
        String updateSql = "UPDATE schedule SET title = ?, author = ?, updatedAt = ?" +
                "where id = ? and password = ?";

        String selectSql = "SELECT id, author, title, password, description, createdAt, updatedAt, deletedAt FROM schedule WHERE id = ?";


        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(updateSql);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setObject(3, date);
            preparedStatement.setLong(4, id);
            preparedStatement.setString(5, password);

            int update = preparedStatement.executeUpdate();
            if (update > 0) {
                preparedStatement = connection.prepareStatement(selectSql);
                preparedStatement.setLong(1, id);

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    Schedule schedule = Schedule.builder()
                            .id(resultSet.getLong("id"))
                            .author(resultSet.getString("author"))
                            .title(resultSet.getString("title"))
                            .password(resultSet.getString("password"))
                            .description(resultSet.getString("description"))
                            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
                            .updatedAt(resultSet.getObject("updatedAt", LocalDateTime.class))
                            .deletedAt(resultSet.getObject("deletedAt", LocalDateTime.class))
                            .build();

                    return schedule;
                }
            } else {
                throw new IllegalArgumentException("일치하는 데이터가 없습니다.");
            }

        } catch (SQLException e) {
            throw new IllegalArgumentException("update에 실패했습니다.");
        } finally {
            closeResources(connection, preparedStatement, resultSet);
        }

        return null;
    }

    // closeResources 메서드
    public void closeResources(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        closeResource(resultSet);
        closeResource(preparedStatement);
        closeResource(connection);
    }

    // 개별 자원을 닫기 위한 메서드
    private void closeResource(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
