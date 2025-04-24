package com.example.othellomemberservice.dao;

import com.example.othellomemberservice.model.FriendInvitation;
import com.example.othellomemberservice.model.Member;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                    member.setStatus(1);
                    updateUserStatus(member);
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

    /**
     * update user status
     */
    public boolean updateUserStatus(Member member) throws SQLException {
        String sql = "UPDATE Member SET status = ? WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, member.getStatus());
            ps.setInt(2, member.getId());
            // execute query
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * get friend
     */
    public List<Member> getListFriend(Member member) throws SQLException {
        String sql = """
        SELECT m.*
        FROM friendinvitation f
        JOIN member m ON (f.requestId = m.id OR f.receiveId = m.id)
        WHERE f.status = 'ACCEPTED' 
          AND (f.requestId = ? OR f.receiveId = ?)
          AND m.id != ?;
    """;

        List<Member> listFriend = new ArrayList<>();

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, member.getId());
            ps.setInt(2, member.getId());
            ps.setInt(3, member.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Member friend = new Member();
                    friend.setId(rs.getInt("id"));
                    friend.setUsername(rs.getString("username"));
                    friend.setPassword(rs.getString("password"));
                    friend.setEmail(rs.getString("email"));
                    friend.setElo(rs.getInt("elo"));
                    friend.setStatus(rs.getInt("status"));
                    listFriend.add(friend);
                }
            }
        }
        return listFriend;
    }

    /**
     * friend websocket
     */
    public Member findById(int id) {
        String sql = "SELECT * FROM Member WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setUsername(rs.getString("username"));
                    member.setPassword(rs.getString("password"));
                    member.setEmail(rs.getString("email"));
                    member.setElo(rs.getInt("elo"));
                    member.setStatus(rs.getInt("status"));
                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Member> searchPlayersByUsername(String username) {
        String sql = "SELECT * FROM Member WHERE username LIKE ? LIMIT 20";
        List<Member> results = new ArrayList<>();

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + username + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setUsername(rs.getString("username"));
                    member.setPassword(rs.getString("password"));
                    member.setEmail(rs.getString("email"));
                    member.setElo(rs.getInt("elo"));
                    member.setStatus(rs.getInt("status"));

                    results.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<FriendInvitation> getInvitationRequest(Member member) throws SQLException {
        String sql = """
            SELECT f.*
            FROM member m
            JOIN friendinvitation f ON m.id = f.receiveId
            WHERE f.status = 'PENDING'
                and f.receiveId = ?;
        """;

        List<FriendInvitation> listInvitationRequest = new ArrayList<>();

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, member.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FriendInvitation invitation = new FriendInvitation();
                    Member requester = findById(rs.getInt("requestId"));
                    Member receiver = findById(rs.getInt("receiveId"));
                    invitation.setId(rs.getInt("id"));
                    invitation.setRequestMem(requester);
                    invitation.setReceiveMem(receiver);
                    invitation.setStatus(rs.getString("status"));
                    listInvitationRequest.add(invitation);
                }
            }
        }
        return listInvitationRequest;
    }

    public boolean removeFriend(int memberId, int friendId) throws SQLException {
        String sql = "DELETE FROM friendinvitation WHERE " +
                "((requestId = ? AND receiveId = ?) OR (requestId = ? AND receiveId = ?)) " +
                "AND status = 'ACCEPTED'";

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ps.setInt(2, friendId);
            ps.setInt(3, friendId);
            ps.setInt(4, memberId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

}