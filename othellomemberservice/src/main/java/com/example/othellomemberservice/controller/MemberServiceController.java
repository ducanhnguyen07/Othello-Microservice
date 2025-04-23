package com.example.othellomemberservice.controller;

import com.example.othellomemberservice.dao.FriendInvitationDAO;
import com.example.othellomemberservice.dao.MemberDAO;
import com.example.othellomemberservice.model.FriendInvitation;
import com.example.othellomemberservice.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api")
public class MemberServiceController {
    @Autowired
    private FriendInvitationDAO friendInvitationDAO;

    @Autowired
    private MemberDAO memberDAO;

    @PostMapping("/login")
    public Member processLogin(@RequestBody Member member) {

        return new MemberDAO().authenticate(member);
    }

    @PostMapping("/logout")
    public boolean processLogout(@RequestBody Member member) throws SQLException {
        member.setStatus(0);
        return new MemberDAO().updateUserStatus(member);
    }

    @PostMapping("/online-player")
    public List<Member> getOnlinePlayers(@RequestBody Member member) throws SQLException {
        List<Member> listFriend = new MemberDAO().getListFriend(member);
        return listFriend.stream()
                .filter(friend -> friend.getStatus() == 1)
                .collect(Collectors.toList());
    }
//    @PostMapping("/register")
//    public String processRegistration(@ModelAttribute Member member,
//                                      RedirectAttributes redirectAttributes) {
//
//        // Kiểm tra username đã tồn tại chưa
//        if (new MemberDAO().usernameExists(member.getUsername())) {
//            redirectAttributes.addFlashAttribute("registerMessage", "Tên đăng nhập đã tồn tại");
//            return "redirect:/register";
//        }
//
//        // Kiểm tra email đã tồn tại chưa
//        if (new MemberDAO().emailExists(member.getEmail())) {
//            redirectAttributes.addFlashAttribute("registerMessage", "Email đã được sử dụng");
//            return "redirect:/register";
//        }
//
//        // Đăng ký thành viên mới
//        boolean registered = new MemberDAO().register(member);
//
//        if (registered) {
//            redirectAttributes.addFlashAttribute("loginMessage", "Đăng ký thành công. Vui lòng đăng nhập");
//            return "redirect:/login";
//        } else {
//            redirectAttributes.addFlashAttribute("registerMessage", "Đăng ký thất bại. Vui lòng thử lại");
//            return "redirect:/register";
//        }
//    }

    @GetMapping("/friend-invitations/{userId}")
    public List<Map<String, Object>> getPendingFriendInvitations(@PathVariable int userId) {
        List<FriendInvitation> invitations = friendInvitationDAO.getPendingInvitationsForUser(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (FriendInvitation invitation : invitations) {
            Map<String, Object> invitationDetails = new HashMap<>();

            // Get requester details
            Member requester = memberDAO.findById(invitation.getRequestId());

            if (requester != null) {
                invitationDetails.put("invitationId", invitation.getId());
                invitationDetails.put("requesterId", requester.getId());
                invitationDetails.put("requesterUsername", requester.getUsername());
                invitationDetails.put("requesterElo", requester.getElo());
                invitationDetails.put("timeRequest", invitation.getTimeRequest());

                result.add(invitationDetails);
            }
        }

        return result;
    }

    @PostMapping("/send-friend-request")
    public Map<String, Object> sendFriendRequest(@RequestBody Map<String, Integer> request) {
        Map<String, Object> response = new HashMap<>();

        int requesterId = request.get("requesterId");
        int receiverId = request.get("receiverId");

        // Check if users exist
        Member requester = memberDAO.findById(requesterId);
        Member receiver = memberDAO.findById(receiverId);

        if (requester == null || receiver == null) {
            response.put("success", false);
            response.put("message", "Invalid user ID");
            return response;
        }

        // Check if they are already friends
        try {
            List<Member> friends = memberDAO.getListFriend(requester);
            boolean alreadyFriends = friends.stream().anyMatch(friend -> friend.getId() == receiverId);

            if (alreadyFriends) {
                response.put("success", false);
                response.put("message", "Already friends");
                return response;
            }
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Error checking friendship status");
            return response;
        }

        // Check for existing pending invitation
        boolean hasPending = friendInvitationDAO.hasPendingInvitation(requesterId, receiverId);
        if (hasPending) {
            response.put("success", false);
            response.put("message", "Friend request already pending");
            return response;
        }

        // Create new invitation
        FriendInvitation invitation = new FriendInvitation();
        invitation.setRequestId(requesterId);
        invitation.setReceiveId(receiverId);
        invitation.setStatus("PENDING");
        invitation.setTimeRequest(new Date());

        boolean saved = friendInvitationDAO.saveFriendInvitation(invitation);

        if (saved) {
            response.put("success", true);
            response.put("invitationId", invitation.getId());
            response.put("message", "Friend request sent");
        } else {
            response.put("success", false);
            response.put("message", "Failed to send friend request");
        }

        return response;
    }

    @PostMapping("/respond-friend-request")
    public Map<String, Object> respondToFriendRequest(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        int invitationId = (int) request.get("invitationId");
        String status = (String) request.get("status"); // "ACCEPTED" or "REJECTED"

        if (!status.equals("ACCEPTED") && !status.equals("REJECTED")) {
            response.put("success", false);
            response.put("message", "Invalid status value");
            return response;
        }

        FriendInvitation invitation = friendInvitationDAO.findById(invitationId);

        if (invitation == null) {
            response.put("success", false);
            response.put("message", "Invitation not found");
            return response;
        }

        if (!invitation.getStatus().equals("PENDING")) {
            response.put("success", false);
            response.put("message", "Invitation already processed");
            return response;
        }

        invitation.setStatus(status);
        invitation.setTimeUpdate(new Date());

        boolean updated = friendInvitationDAO.updateFriendInvitation(invitation);

        if (updated) {
            response.put("success", true);
            response.put("message", status.equals("ACCEPTED") ?
                    "Friend request accepted" : "Friend request rejected");
        } else {
            response.put("success", false);
            response.put("message", "Failed to process friend request");
        }

        return response;
    }

    @GetMapping("/friends/{userId}")
    public List<Member> getFriends(@PathVariable int userId) {
        try {
            return memberDAO.getListFriend(new Member(userId, null, null, null, 0));
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/invitation-request/{userId}")
    public List<Member> getInvitationRequest(@PathVariable int userId) {
        try {
            return memberDAO.getInvitationRequest(new Member(userId, null, null, null, 0));
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/search-players")
    public List<Map<String, Object>> searchPlayers(@RequestParam String username, @RequestHeader("Member-Id") int memberId) {
        List<Map<String, Object>> results = new ArrayList<>();

        try {
            // Search for players whose username contains the search term
            List<Member> players = memberDAO.searchPlayersByUsername(username);

            // Get the user's friends for comparison
            List<Member> friends = memberDAO.getListFriend(new Member(memberId, null, null, null, 0));

            // Check for pending invitations
            List<FriendInvitation> sentInvitations = friendInvitationDAO.getSentInvitationsForUser(memberId);

            for (Member player : players) {
                // Skip the current user
                if (player.getId() == memberId) {
                    continue;
                }

                Map<String, Object> playerInfo = new HashMap<>();
                playerInfo.put("id", player.getId());
                playerInfo.put("username", player.getUsername());
                playerInfo.put("elo", player.getElo());
                playerInfo.put("status", player.getStatus());

                // Check if this player is already a friend
                boolean isFriend = friends.stream().anyMatch(friend -> friend.getId() == player.getId());
                playerInfo.put("isFriend", isFriend);

                // Check if there's a pending invitation
                boolean isPending = sentInvitations.stream()
                        .anyMatch(inv -> inv.getReceiveId() == player.getId());
                playerInfo.put("pendingRequest", isPending);

                results.add(playerInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    @DeleteMapping("/friends/{userId}/{friendId}")
    public Map<String, Object> removeFriend(@PathVariable int userId, @PathVariable int friendId) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean removed = memberDAO.removeFriend(userId, friendId);

            if (removed) {
                response.put("success", true);
                response.put("message", "Friend removed successfully");
            } else {
                response.put("success", false);
                response.put("message", "Failed to remove friend. Friendship may not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error removing friend: " + e.getMessage());
        }

        return response;
    }


}
