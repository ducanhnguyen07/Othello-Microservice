package com.anhnd.othelloresultservice.dao;

import com.anhnd.othelloresultservice.model.Bot;
import com.anhnd.othelloresultservice.model.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResultDAO extends ResultServiceDAO {

    public ResultDAO() {
        super();
    }

    public boolean saveGameResult(int memberAId, int botId, boolean result) {
        String sqlResult = "INSERT INTO Result (idMemberA, idMemberB, idBot, resAtoB, resAtoBot) VALUES (?, NULL, ?, ?, ?)";
        String sqlUpdateElo = "UPDATE Member SET elo = elo + ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmtResult = null;
        PreparedStatement pstmtElo = null;

        try {
            // Lấy kết nối mới
            conn = this.getConnection();

            // Tắt auto-commit
            conn.setAutoCommit(false);


            // Chuẩn bị câu lệnh insert kết quả
            pstmtResult = conn.prepareStatement(sqlResult);
            pstmtResult.setInt(1, memberAId);
            pstmtResult.setInt(2, botId);
            pstmtResult.setBoolean(3, result); // Kết quả của người chơi so với bot
            pstmtResult.setBoolean(4, !result); // Kết quả ngược lại của bot

            // Thực thi insert kết quả
            int resultRowsAffected = pstmtResult.executeUpdate();

            // Chuẩn bị câu lệnh cập nhật ELO
            pstmtElo = conn.prepareStatement(sqlUpdateElo);

            // Tính toán thay đổi ELO
            int eloChange = result ? 20 : -10; // Thắng được +20, thua mất -10
            pstmtElo.setInt(1, eloChange);
            pstmtElo.setInt(2, memberAId);

            // Thực thi cập nhật ELO
            int eloRowsAffected = pstmtElo.executeUpdate();

            // Commit transaction
            conn.commit();

            return resultRowsAffected > 0 && eloRowsAffected > 0;

        } catch (SQLException e) {
            // Rollback nếu có lỗi
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }

            // Log lỗi chi tiết
            System.err.println("Error saving game result: " + e.getMessage());
            e.printStackTrace();

            return false;
        } finally {
            // Đảm bảo đóng tất cả tài nguyên
            try {
                if (pstmtResult != null) pstmtResult.close();
                if (pstmtElo != null) pstmtElo.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }
}