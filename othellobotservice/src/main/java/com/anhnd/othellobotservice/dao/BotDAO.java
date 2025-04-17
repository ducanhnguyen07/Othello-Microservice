package com.anhnd.othellobotservice.dao;
import com.anhnd.othellobotservice.model.Bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BotDAO extends BotServiceDAO {
    public BotDAO() {
        super();
    }

    public Bot getBot(int id) {
        String sql = "SELECT * FROM Bot WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.getString("id"));
                    Bot bot = new Bot();
                    bot.setId(rs.getInt("id"));
                    bot.setUrlModel(rs.getString("urlModel"));
                    return bot;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



}
