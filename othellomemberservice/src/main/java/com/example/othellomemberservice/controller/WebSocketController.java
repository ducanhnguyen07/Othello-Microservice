package com.example.othellomemberservice.controller;

import com.example.othellomemberservice.model.FriendInvitation;
import com.example.othellomemberservice.dao.FriendInvitationDAO;
import com.example.othellomemberservice.model.Member;
import com.example.othellomemberservice.dao.MemberDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private FriendInvitationDAO friendInvitationDAO;

    @Autowired
    private MemberDAO memberDAO;

    // Handle friend invitation request
    @MessageMapping("/friend-request")
    public void sendFriendRequest(@Payload Map<String, Object> friendRequest) {
        int requesterId = ((Number) friendRequest.get("requesterId")).intValue();
        int receiverId = ((Number) friendRequest.get("receiverId")).intValue();

        // Create and save the friend invitation
        FriendInvitation invitation = new FriendInvitation();
        invitation.setRequestId(requesterId);
        invitation.setReceiveId(receiverId);
        invitation.setStatus("PENDING");
        invitation.setTimeRequest(new Date());

        boolean saved = friendInvitationDAO.saveFriendInvitation(invitation);

        if (saved) {
            // Get requester information to send to the receiver
            Member requester = memberDAO.findById(requesterId);

            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "FRIEND_REQUEST");
            notification.put("invitationId", invitation.getId());
            notification.put("requester", requester);
            notification.put("timestamp", invitation.getTimeRequest());

            // Send notification to the specific user
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(receiverId),
                    "/queue/notifications",
                    notification
            );

            // Send confirmation to the requester
            Map<String, Object> confirmation = new HashMap<>();
            confirmation.put("type", "FRIEND_REQUEST_SENT");
            confirmation.put("invitationId", invitation.getId());
            confirmation.put("receiverId", receiverId);

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(requesterId),
                    "/queue/notifications",
                    confirmation
            );
        }
    }

    // Handle friend request response (accept/reject)
    @MessageMapping("/friend-response")
    public void handleFriendResponse(@Payload Map<String, Object> response) {

        int invitationId = ((Number) response.get("invitationId")).intValue();
        String status = (String) response.get("status"); // "ACCEPTED" or "REJECTED"

        // Update the invitation status
        FriendInvitation invitation = friendInvitationDAO.findById(invitationId);
        System.out.println(
                invitationId + " " +
                invitation.getRequestId() + " "
                + invitation.getReceiveId() + " "
                + invitation.getStatus());
        if (invitation != null) {
            invitation.setStatus(status);
            invitation.setTimeUpdate(new Date());

            boolean updated = friendInvitationDAO.updateFriendInvitation(invitation);

            if (updated) {
                // Notify the original requester about the response
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "FRIEND_REQUEST_RESPONSE");
                notification.put("invitationId", invitationId);
                notification.put("status", status);
                notification.put("responderId", invitation.getReceiveId());

                // Get responder information
                Member responder = memberDAO.findById(invitation.getReceiveId());
                notification.put("responder", responder);

                messagingTemplate.convertAndSendToUser(
                        String.valueOf(invitation.getRequestId()),
                        "/queue/notifications",
                        notification
                );
            }
        }
    }
    @MessageMapping("/update-status")
    public void updateUserStatus(@Payload Map<String, Object> statusUpdate) throws SQLException {
        int userId = ((Number) statusUpdate.get("userId")).intValue();
        int status = ((Number) statusUpdate.get("status")).intValue();

        try {
            // Update user status in database
            Member member = memberDAO.findById(userId);
            if (member != null) {
                member.setStatus(status);
                memberDAO.updateUserStatus(member);

                // Get user's friends to notify them
                List<Member> friends = memberDAO.getListFriend(member);

                // Create status notification
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "FRIEND_STATUS_CHANGE");
                notification.put("userId", userId);
                notification.put("username", member.getUsername());
                notification.put("status", status);

                // Send notification to all friends
                for (Member friend : friends) {
                    messagingTemplate.convertAndSendToUser(
                            String.valueOf(friend.getId()),
                            "/queue/notifications",
                            notification
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}