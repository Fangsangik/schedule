package com.example.dailyschedule.repository;

import com.example.dailyschedule.domain.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.sql.Date.*;

@Repository
public class ScheduleRepositoryImpl {

    private final DataSource dataSource;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;


    public ScheduleRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Schedule createSchedule(Schedule schedule) {
        String sql = "INSERT INTO schedule (author, title, createdAt, password, description, updatedAt, deletedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";


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

            preparedStatement.executeUpdate();

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
            connectionClose();
        }
    }


    public Schedule updateSchedule(Schedule schedule) {
        if (schedule.getId() == null) {
            throw new IllegalArgumentException("해당 id가 존재하지 않습니다.");
        }

        String sql = "UPDATE schedule SET title = ?, author = ?, description = ?, updatedAt = ?, password = ?, createdAt = ?, deletedAt = ? WHERE id = ?";


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
            connectionClose();
        }
    }

    public Schedule findScheduleById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("해당 id 값이 null 입니다.");
        }

        // 필요한 모든 필드를 조회하도록 쿼리 작성
        String sql = "SELECT id, author, title, password, description, createdAt, updatedAt, deletedAt FROM schedule WHERE id = ?";

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
                        .createdAt(resultSet.getObject("createdAt", LocalDate.class))
                        .updatedAt(resultSet.getObject("updatedAt", LocalDate.class))
                        .deletedAt(resultSet.getObject("deletedAt", LocalDate.class))
                        .build();
                System.out.println("Found Schedule: " + schedule);
                return schedule;
            }

        } catch (SQLException e) {
            throw new RuntimeException("해당 스케줄을 조회 할 수 없습니다.", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            connectionClose();
        }

        return null;
    }

    public void deleteScheduleById(Long scheduleId) {
        if (scheduleId == null) {
            throw new IllegalArgumentException("해당 아이디가 없습니다.");
        }

        String sql = "delete from schedule where id = ?";

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
            connectionClose();
        }
    }


    public void deleteAll() {
        String sql = "delete from schedule";

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("data를 삭제하는데 실패했습니다.");
        } finally {
            connectionClose();

        }
    }


    public Schedule findByUpdateAndAuthor(LocalDate updatedAt, String author) {
        if (updatedAt == null && author == null) {
            throw new IllegalArgumentException("해당 이름으로 수정된 날짜를 찾을 수 없습니다.");
        }

        String sql = "SELECT id, title, author, password, description, createdAt, updatedAt, deletedAt FROM schedule WHERE updatedAt = ? AND author = ?";

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
                        .createdAt(resultSet.getObject("createdAt", LocalDate.class))
                        .updatedAt(resultSet.getObject("updatedAt", LocalDate.class))
                        .deletedAt(resultSet.getObject("deletedAt", LocalDate.class))
                        .build();

                return schedule;
            }

        } catch (SQLException e) {
            throw new RuntimeException("해당 조회 값을 찾을 수 없습니다.");
        } finally {
            connectionClose();
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
                        .createdAt(resultSet.getObject("createdAt", LocalDate.class))
                        .updatedAt(resultSet.getObject("updatedAt", LocalDate.class))
                        .deletedAt(resultSet.getObject("deletedAt", LocalDate.class))
                        .build();

                schedules.add(schedule);
            }

        } catch (SQLException e) {
            throw new RuntimeException("스케줄 조회에 실패했습니다.", e);
        }

        return schedules;
    }


    private void connectionClose() {

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
