package com.example.dailyschedule.config;

import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
@Configuration
public class DataSourceConfig {

    private static final String URL = "jdbc:mysql://localhost:3306/schedule";  // 데이터베이스 URL
    private static final String USER = "root";  // 데이터베이스 사용자 이름
    private static final String PASSWORD = "1234";  // 데이터베이스 비밀번호

    public Connection getConnection() throws SQLException {
        try {
            // DriverManager를 통해 데이터베이스 연결을 생성하고 반환
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new IllegalArgumentException("데이터베이스 연결에 실패했습니다.", e);
        }
    }
}
