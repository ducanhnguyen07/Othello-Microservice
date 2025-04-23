package com.anhnd.othelloclient;

import com.anhnd.othelloclient.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
public class MultiplayerGameController {

    @Autowired
    private HttpSession session;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${game.service.url}")
    private String gameServiceUrl;

    /**
     * View for finding online players
     */
    @GetMapping("/find-player")
    public String findPlayer(Model model, HttpSession session) {
        // Check if user is logged in
        if (session.getAttribute("loggedInMember") == null) {
            return "redirect:/login";
        }

        model.addAttribute("member", session.getAttribute("loggedInMember"));
        return "find-player";
    }

    /**
     * View for creating a private game room
     */
    @GetMapping("/create-room")
    public String createRoom(Model model, HttpSession session) {
        // Check if user is logged in
        if (session.getAttribute("loggedInMember") == null) {
            return "redirect:/login";
        }

        model.addAttribute("member", session.getAttribute("loggedInMember"));
        return "create-room";
    }

    /**
     * View for multiplayer game
     */
    @GetMapping("/play-multiplayer/{gameId}")
    public String playMultiplayer(@PathVariable String gameId, Model model, HttpSession session) {
        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return "redirect:/login";
        }

        // Fetch game details from game service
        String url = gameServiceUrl + "/api/game/multiplayer/" + gameId + "/details";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return "redirect:/play.html?error=invalid_game";
        }

        Map<String, Object> gameDetails = responseEntity.getBody();

        // Add game details to model
        model.addAttribute("gameId", gameId);
        model.addAttribute("playerColor", gameDetails.get("playerColor"));
        model.addAttribute("player1", gameDetails.get("player1"));
        model.addAttribute("player2", gameDetails.get("player2"));
        model.addAttribute("board", gameDetails.get("board"));
        model.addAttribute("currentPlayer", gameDetails.get("currentPlayer"));

        // Add scores
        Map<String, Integer> scores = new HashMap<>();
        scores.put("black", (Integer) gameDetails.get("blackScore"));
        scores.put("white", (Integer) gameDetails.get("whiteScore"));
        model.addAttribute("scores", scores);

        // Add logged in member to model
        model.addAttribute("member", loggedInMember);

        return "play-multiplayer";
    }

    /**
     * API endpoint to join a room by code
     */
    @PostMapping("/api/game/room/join")
    @ResponseBody
    public Map<String, Object> joinRoom(@RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để tham gia phòng chơi");
            return response;
        }

        String roomCode = request.get("roomCode");

        // Call game service to join room
        String url = gameServiceUrl + "/api/game/room/join";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("roomCode", roomCode);

        HttpEntity<Map<String, String>> httpRequest = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể tham gia phòng: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to get online players
     */
    @GetMapping("/api/players/online")
    @ResponseBody
    public List<Map<String, Object>> getOnlinePlayers(HttpSession session) {
        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return new ArrayList<>();
        }

        // Call game service to get online players
        String url = gameServiceUrl + "/api/players/online";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * API endpoint to search players by username
     */
    @GetMapping("/api/players/search")
    @ResponseBody
    public List<Map<String, Object>> searchPlayers(@RequestParam String username, HttpSession session) {
        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return new ArrayList<>();
        }

        // Call game service to search players
        String url = gameServiceUrl + "/api/players/search?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * API endpoint to create a room
     */
    @PostMapping("/api/game/room/create")
    @ResponseBody
    public Map<String, Object> createGameRoom(@RequestBody Map<String, Object> request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để tạo phòng");
            return response;
        }

        // Call game service to create room
        String url = gameServiceUrl + "/api/game/room/create";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể tạo phòng: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to get players in a room
     */
    @GetMapping("/api/game/room/{roomId}/players")
    @ResponseBody
    public Map<String, Object> getRoomPlayers(@PathVariable String roomId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            response.put("players", new ArrayList<>());
            return response;
        }

        // Call game service to get room players
        String url = gameServiceUrl + "/api/game/room/" + roomId + "/players";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể lấy danh sách người chơi: " + e.getMessage());
            response.put("players", new ArrayList<>());
            return response;
        }
    }

    /**
     * API endpoint to leave a room
     */
    @PostMapping("/api/game/room/{roomId}/leave")
    @ResponseBody
    public Map<String, Object> leaveRoom(@PathVariable String roomId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to leave room
        String url = gameServiceUrl + "/api/game/room/" + roomId + "/leave";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể rời phòng: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to start a game from a room
     */
    @PostMapping("/api/game/room/{roomId}/start")
    @ResponseBody
    public Map<String, Object> startGame(@PathVariable String roomId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to start game
        String url = gameServiceUrl + "/api/game/room/" + roomId + "/start";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể bắt đầu trò chơi: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to send a challenge to another player
     */
    @PostMapping("/api/game/challenge")
    @ResponseBody
    public Map<String, Object> challengePlayer(@RequestBody Map<String, Object> request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to send challenge
        String url = gameServiceUrl + "/api/game/challenge";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        // Add challengedPlayerId to request body

        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể gửi lời thách đấu: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to check status of a challenge
     */
    @GetMapping("/api/game/challenge/{challengeId}/status")
    @ResponseBody
    public Map<String, Object> getChallengeStatus(@PathVariable String challengeId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("status", "ERROR");
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to get challenge status
        String url = gameServiceUrl + "/api/game/challenge/" + challengeId + "/status";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Không thể kiểm tra trạng thái thách đấu: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to get incoming challenges
     */
    @GetMapping("/api/game/challenges/incoming")
    @ResponseBody
    public List<Map<String, Object>> getIncomingChallenges(HttpSession session) {
        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return new ArrayList<>();
        }

        // Call game service to get incoming challenges
        String url = gameServiceUrl + "/api/game/challenges/incoming";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * API endpoint to accept a challenge
     */
    @PostMapping("/api/game/challenge/{challengeId}/accept")
    @ResponseBody
    public Map<String, Object> acceptChallenge(@PathVariable String challengeId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to accept challenge
        String url = gameServiceUrl + "/api/game/challenge/" + challengeId + "/accept";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể chấp nhận thách đấu: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to reject a challenge
     */
    @PostMapping("/api/game/challenge/{challengeId}/reject")
    @ResponseBody
    public Map<String, Object> rejectChallenge(@PathVariable String challengeId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to reject challenge
        String url = gameServiceUrl + "/api/game/challenge/" + challengeId + "/reject";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể từ chối thách đấu: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to get game state
     */
    @GetMapping("/api/game/multiplayer/{gameId}/state")
    @ResponseBody
    public Map<String, Object> getGameState(@PathVariable String gameId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("error", "Unauthorized");
            return response;
        }

        // Call game service to get game state
        String url = gameServiceUrl + "/api/game/multiplayer/" + gameId + "/state";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("error", "Không thể lấy trạng thái trò chơi: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to make a move
     */
    @PostMapping("/api/game/multiplayer/{gameId}/move")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> makeMove(@PathVariable String gameId,
                                                        @RequestBody Map<String, Integer> moveData,
                                                        HttpSession session) {
        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Unauthorized");
            return ResponseEntity.status(401).body(errorResponse);
        }

        // Call game service to make move
        String url = gameServiceUrl + "/api/game/multiplayer/" + gameId + "/move";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Map<String, Integer>> httpRequest = new HttpEntity<>(moveData, headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity;
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Không thể thực hiện nước đi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * API endpoint to resign from a game
     */
    @PostMapping("/api/game/multiplayer/{gameId}/resign")
    @ResponseBody
    public Map<String, Object> resignGame(@PathVariable String gameId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to resign
        String url = gameServiceUrl + "/api/game/multiplayer/" + gameId + "/resign";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể đầu hàng: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to offer a draw
     */
    @PostMapping("/api/game/multiplayer/{gameId}/offer-draw")
    @ResponseBody
    public Map<String, Object> offerDraw(@PathVariable String gameId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to offer draw
        String url = gameServiceUrl + "/api/game/multiplayer/" + gameId + "/offer-draw";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể đề nghị hoà: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to notify timeout
     */
    @PostMapping("/api/game/multiplayer/{gameId}/timeout")
    @ResponseBody
    public Map<String, Object> notifyTimeout(@PathVariable String gameId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to notify timeout
        String url = gameServiceUrl + "/api/game/multiplayer/" + gameId + "/timeout";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không thể thông báo hết giờ: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to save game result
     */
    @PostMapping("/api/game/multiplayer/{gameId}/save-result")
    @ResponseBody
    public Map<String, Object> saveGameResult(@PathVariable String gameId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("saved", false);
            response.put("message", "Unauthorized");
            return response;
        }

        // Call game service to save result
        String url = gameServiceUrl + "/api/game/multiplayer/" + gameId + "/save-result";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            response.put("saved", false);
            response.put("message", "Không thể lưu kết quả: " + e.getMessage());
            return response;
        }
    }

    /**
     * API endpoint to send chat message
     */
    @PostMapping("/api/game/multiplayer/{gameId}/chat")
    @ResponseBody
    public ResponseEntity<Void> sendChatMessage(@PathVariable String gameId,
                                                @RequestBody Map<String, String> messageData,
                                                HttpSession session) {
        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Call game service to send message
        String url = gameServiceUrl + "/api/game/multiplayer/" + gameId + "/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Map<String, String>> httpRequest = new HttpEntity<>(messageData, headers);

        try {
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    Void.class
            );

            return responseEntity;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * API endpoint to get chat messages
     */
    @GetMapping("/api/game/multiplayer/{gameId}/chat")
    @ResponseBody
    public List<Map<String, Object>> getChatMessages(@PathVariable String gameId, HttpSession session) {
        // Check if user is logged in
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return new ArrayList<>();
        }

        // Call game service to get messages
        String url = gameServiceUrl + "/api/game/multiplayer/" + gameId + "/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}