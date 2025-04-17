package com.example.othellomemberservice.dao;

import com.example.othellomemberservice.model.Member;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class MemberDAO extends MemberServiceDAO {

    public MemberDAO() {
        super();
    }

    /**
     * Tìm member theo username
     */
    public Member findByUsername(String username) {
        String sql = "SELECT * FROM Member WHERE username = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setUsername(rs.getString("username"));
                    member.setPassword(rs.getString("password"));
                    member.setEmail(rs.getString("email"));
                    member.setElo(rs.getInt("elo"));
                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Xác thực đăng nhập
     */
    public Member authenticate(Member member) {
        String sql = "SELECT * FROM Member WHERE username = ? AND password = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getUsername());
            ps.setString(2, member.getPassword());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setUsername(rs.getString("username"));
                    member.setPassword(rs.getString("password"));
                    member.setEmail(rs.getString("email"));
                    member.setElo(rs.getInt("elo"));
                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Đăng ký thành viên mới
     */
    public boolean register(Member member) {
        String sql = "INSERT INTO Member (username, password, email, elo) VALUES (?, ?, ?, ?)";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getUsername());
            ps.setString(2, member.getPassword()); // Trong thực tế nên sử dụng hàm băm mật khẩu
            ps.setString(3, member.getEmail());
            ps.setInt(4, member.getElo());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Member WHERE username = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Member WHERE email = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}