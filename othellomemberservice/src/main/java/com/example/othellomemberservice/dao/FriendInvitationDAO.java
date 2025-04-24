package com.example.othellomemberservice.dao;

import com.example.othellomemberservice.model.FriendInvitation;
import com.example.othellomemberservice.model.Member;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FriendInvitationDAO extends MemberServiceDAO {

    public FriendInvitation findById(int id) {
        String sql = "SELECT * FROM friendinvitation WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FriendInvitation invitation = new FriendInvitation();
                    Member requestMem = new Member();
                    Member receiveMem = new Member();
                    requestMem.setId(rs.getInt("requestId"));
                    receiveMem.setId(rs.getInt("receiveId"));
                    invitation.setId(rs.getInt("id"));
                    invitation.setRequestMem(requestMem);
                    invitation.setReceiveMem(receiveMem);
                    invitation.setStatus(rs.getString("status"));
                    invitation.setTimeRequest(rs.getTimestamp("timeRequest"));
                    invitation.setTimeUpdate(rs.getTimestamp("timeUpdate"));
                    return invitation;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FriendInvitation> getPendingInvitationsForUser(int userId) {
        String sql = "SELECT * FROM friendinvitation WHERE receiveId = ? AND status = 'PENDING'";
        List<FriendInvitation> invitations = new ArrayList<>();

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FriendInvitation invitation = new FriendInvitation();
                    Member requestMem = new Member();
                    Member receiveMem = new Member();
                    requestMem.setId(rs.getInt("requestId"));
                    receiveMem.setId(rs.getInt("receiveId"));
                    invitation.setId(rs.getInt("id"));
                    invitation.setRequestMem(requestMem);
                    invitation.setReceiveMem(receiveMem);

                    invitation.setStatus(rs.getString("status"));
                    invitation.setTimeRequest(rs.getTimestamp("timeRequest"));
                    invitation.setTimeUpdate(rs.getTimestamp("timeUpdate"));
                    invitations.add(invitation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invitations;
    }

    public List<FriendInvitation> getSentInvitationsForUser(int userId) {
        String sql = "SELECT * FROM friendinvitation WHERE requestId = ? AND status = 'PENDING'";
        List<FriendInvitation> invitations = new ArrayList<>();

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FriendInvitation invitation = new FriendInvitation();
                    Member requestMem = new Member();
                    Member receiveMem = new Member();
                    invitation.setId(rs.getInt("id"));
                    requestMem.setId(rs.getInt("requestId"));
                    receiveMem.setId(rs.getInt("receiveId"));
                    invitation.setId(rs.getInt("id"));
                    invitation.setRequestMem(requestMem);
                    invitation.setReceiveMem(receiveMem);
                    invitation.setStatus(rs.getString("status"));
                    invitation.setTimeRequest(rs.getTimestamp("timeRequest"));
                    invitation.setTimeUpdate(rs.getTimestamp("timeUpdate"));
                    invitations.add(invitation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invitations;
    }

    public boolean saveFriendInvitation(FriendInvitation invitation) {
        String sql = "INSERT INTO friendinvitation (requestId, receiveId, status, timeRequest) VALUES (?, ?, ?, ?)";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, invitation.getRequestMem().getId());
            ps.setInt(2, invitation.getReceiveMem().getId());

            ps.setString(3, invitation.getStatus());
            ps.setTimestamp(4, new Timestamp(invitation.getTimeRequest().getTime()));

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        invitation.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateFriendInvitation(FriendInvitation invitation) {
        String sql = "UPDATE friendinvitation SET status = ?, timeUpdate = ? WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invitation.getStatus());
            ps.setTimestamp(2, new Timestamp(invitation.getTimeUpdate().getTime()));
            ps.setInt(3, invitation.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasPendingInvitation(int requesterId, int receiverId) {
        String sql = "SELECT COUNT(*) FROM friendinvitation WHERE requestId = ? AND receiveId = ? AND status = 'PENDING'";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requesterId);
            ps.setInt(2, receiverId);
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