package com.durable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import com.google.gson.Gson;

public class StepStore {
    private final Connection conn;
    private final Gson gson = new Gson();

    public StepStore() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:durable.db");
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS steps (
                    workflow_id TEXT,
                    step_key TEXT,
                    result TEXT,
                    PRIMARY KEY (workflow_id, step_key)
                )
            """);
        }
    }

    public synchronized Optional<String> getResult(String workflowId, String stepKey) throws SQLException {
        String sql = "SELECT result FROM steps WHERE workflow_id = ? AND step_key = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            pstmt.setString(2, stepKey);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getString("result"));
            }
        }
        return Optional.empty();
    }

    public synchronized void saveResult(String workflowId, String stepKey, String result) throws SQLException {
        String sql = "INSERT OR REPLACE INTO steps (workflow_id, step_key, result) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            pstmt.setString(2, stepKey);
            pstmt.setString(3, result);
            pstmt.executeUpdate();
        }
    }
}