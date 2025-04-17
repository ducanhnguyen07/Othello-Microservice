package com.anhnd.othellotournamentservice.dao;

import com.anhnd.othellotournamentservice.model.Tournament;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class TournamentDAO extends TournamentServiceDAO {
    public TournamentDAO() {
        super();
    }

    // Tạo một giải đấu mới
    public boolean createTournament(Tournament tournament) {
        String sql = "INSERT INTO tournament (`description`) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tournament.getDescription());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Trả về false nếu có lỗi
    }
}