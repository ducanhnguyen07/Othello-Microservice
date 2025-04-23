//package com.anhnd.othelloclient;
//
//import com.anhnd.othelloclient.model.*;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//public class ClientController {
//    @Autowired
//    private HttpSession session;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//
//    /**
//     * Authen + Author
//     */
//    @GetMapping("/login")
//    public String showLoginForm(Model model, HttpSession session) {
//        if (session.getAttribute("loggedInMember") != null) {
//            return "redirect:/home.html";
//        }
//
//        model.addAttribute("loginMessage", model.asMap().get("loginMessage"));
//        return "login";
//    }
//
//    @PostMapping("/login")
//    public String processLogin(@RequestParam("username") String username,
//                               @RequestParam("password") String password,
//                               HttpSession session,
//                               RedirectAttributes redirectAttributes) {
//        RestTemplate restTemplate = new RestTemplate();
//        String externalServiceUrl = "http://localhost:8081/api/login";
//
//        HttpHeaders headers = new HttpHeaders();
//
//        Member member = new Member();
//        member.setUsername(username);
//        member.setPassword(password);
//        HttpEntity<Member> request = new HttpEntity<>(member, headers);
//        ResponseEntity<Member> responseEntity = restTemplate.exchange(
//                externalServiceUrl,
//                HttpMethod.POST,
//                request,
//                new ParameterizedTypeReference<Member>() {}
//        );
//        member = responseEntity.getBody();
//
//        if (member != null) {
//            // Đăng nhập thành công, lưu thông tin vào session
//            session.setAttribute("loggedInMember", member);
//            return "redirect:/home";
//        } else {
//            // Đăng nhập thất bại
//            redirectAttributes.addFlashAttribute("loginMessage", "Tên đăng nhập hoặc mật khẩu không đúng");
//            return "redirect:/login";
//        }
//    }
//
//    @GetMapping("/register")
//    public String showRegisterForm(Model model, HttpSession session) {
//        // Nếu đã đăng nhập, chuyển hướng đến trang chủ
//        if (session.getAttribute("loggedInMember") != null) {
//            return "redirect:/home";
//        }
//
//        model.addAttribute("member", new Member());
//        model.addAttribute("registerMessage", model.asMap().get("registerMessage"));
//        return "register";
//    }
//
//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/login";
//    }
//
//    /**
//     * REST endpoint for game
//     */
//    @GetMapping("/home")
//    public String home(HttpSession session, Model model) {
//        if (session.getAttribute("loggedInMember") != null) {
//            model.addAttribute("member", session.getAttribute("loggedInMember"));
//        }
//        return "home";
//    }
//
//    @GetMapping("/play.html")
//    public String play(HttpSession session) {
//        // Kiểm tra đăng nhập trước khi cho phép chơi
//        if (session.getAttribute("loggedInMember") == null) {
//            return "redirect:/login";
//        }
//        return "play";
//    }
//
//    @GetMapping("/play-with-computer")
//    public String playWithBot(Model model, HttpSession session) {
//        // Kiểm tra đăng nhập trước khi cho phép chơi với máy
//        if (session.getAttribute("loggedInMember") == null) {
//            return "redirect:/login";
//        }
//
//        // Create a new game if one doesn't exist
//        if (session.getAttribute("gameBoard") == null) {
//            Board board = new Board();
//            session.setAttribute("gameBoard", board);
//        }
//
//        Board board = (Board) session.getAttribute("gameBoard");
//        model.addAttribute("board", board);
//        model.addAttribute("currentPlayer", board.getCurrentPlayer());
//        model.addAttribute("boardState", boardToString(board));
//        model.addAttribute("validMoves", board.getValidMoves(board.getCurrentPlayer()));
//
//        // Add scores
//        Map<String, Integer> scores = new HashMap<>();
//        scores.put("black", board.getBlackCount());
//        scores.put("white", board.getWhiteCount());
//        model.addAttribute("scores", scores);
//
//        // Thêm thông tin người dùng đăng nhập vào model
//        model.addAttribute("member", session.getAttribute("loggedInMember"));
//
//        return "play-with-computer";
//    }
//
//    /**
//     * REST endpoint cho client sử dụng để lấy trạng thái bàn cờ
//     */
//    @GetMapping("/api/game/board")
//    @ResponseBody
//    public Map<String, Object> getBoardState(HttpSession session) {
//        // Kiểm tra đăng nhập
//        if (session.getAttribute("loggedInMember") == null) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Unauthorized");
//            errorResponse.put("message", "Vui lòng đăng nhập để tiếp tục");
//            return errorResponse;
//        }
//
//        Board board = (Board) session.getAttribute("gameBoard");
//        if (board == null) {
//            board = new Board();
//            session.setAttribute("gameBoard", board);
//        }
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("boardState", boardToString(board));
//        response.put("currentPlayer", board.getCurrentPlayer());
//        response.put("blackScore", board.getBlackCount());
//        response.put("whiteScore", board.getWhiteCount());
//        response.put("gameOver", board.isGameOver());
//        response.put("winner", board.getWinner());
//
//        return response;
//    }
//
//    @PostMapping("/api/game/move")
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> makeMove(@RequestBody Map<String, Integer> moveData, HttpSession session) {
//        // Kiểm tra đăng nhập
//        if (session.getAttribute("loggedInMember") == null) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Unauthorized");
//            errorResponse.put("message", "Vui lòng đăng nhập để tiếp tục");
//            return ResponseEntity.status(401).body(errorResponse);
//        }
//
//        int row = moveData.get("row");
//        int col = moveData.get("col");
//        int player = moveData.get("player");
//
//        Board board = (Board) session.getAttribute("gameBoard");
//        if (board == null) {
//            board = new Board();
//            session.setAttribute("gameBoard", board);
//        }
//
//        Map<String, Object> response = new HashMap<>();
//
//        // Kiểm tra nước đi có hợp lệ không
//        if (board.isValidMove(row, col, player)) {
//            // Thực hiện nước đi, call bot-service
//            board.makeMove(row, col, player);
//
//            // Cập nhật bàn cờ trong session
//            session.setAttribute("gameBoard", board);
//
//            // Trả về trạng thái mới
//            response.put("boardState", boardToString(board));
//            response.put("currentPlayer", board.getCurrentPlayer());
//            response.put("blackScore", board.getBlackCount());
//            response.put("whiteScore", board.getWhiteCount());
//            response.put("success", true);
//            response.put("gameOver", board.isGameOver());
//            response.put("winner", board.getWinner());
//
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "Nước đi không hợp lệ");
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @GetMapping("/api/game/bot-move")
//    @ResponseBody
//    public Map<String, Object> makeBotMove(HttpSession session) {
//        // Kiểm tra đăng nhập
//        if (session.getAttribute("loggedInMember") == null) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Unauthorized");
//            errorResponse.put("message", "Vui lòng đăng nhập để tiếp tục");
//            return errorResponse;
//        }
//
//        Board board = (Board) session.getAttribute("gameBoard");
//        if (board == null) {
//            board = new Board();
//            session.setAttribute("gameBoard", board);
//        }
//
//        // Đảm bảo lượt chơi hiện tại là bot (WHITE = 2)
//        if (board.getCurrentPlayer() == 2) {
//            // Gọi BotService để lấy nước đi của bot
//            String externalServiceUrl = "http://localhost:8082/api/game/move?botId=2";
//            HttpHeaders headers = new HttpHeaders();
//
//            HttpEntity<Board> request = new HttpEntity<>(board, headers);
//            ResponseEntity<Board> responseEntity = restTemplate.exchange(
//                    externalServiceUrl,
//                    HttpMethod.POST,
//                    request,
//                    new ParameterizedTypeReference<Board>() {}
//            );
//            board = responseEntity.getBody();
//
////            board = botService.makeBotMove(board, 1);
//
//            // Cập nhật bàn cờ trong session
//            session.setAttribute("gameBoard", board);
//        }
//
//        // Trả về trạng thái mới
//        Map<String, Object> response = new HashMap<>();
//        response.put("boardState", boardToString(board));
//        response.put("currentPlayer", board.getCurrentPlayer());
//        response.put("blackScore", board.getBlackCount());
//        response.put("whiteScore", board.getWhiteCount());
//        response.put("gameOver", board.isGameOver());
//        response.put("winner", board.getWinner());
//
//        return response;
//    }
//
//    @PostMapping("/save-game-result")
//    @ResponseBody
//    public Map<String, Object> saveGameResult(HttpSession session) {
//        Map<String, Object> response = new HashMap<>();
//
//        // Kiểm tra đăng nhập
//        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
//        if (loggedInMember == null) {
//            response.put("saved", false);
//            response.put("message", "Vui lòng đăng nhập để lưu kết quả");
//            return response;
//        }
//
//        // Lấy trạng thái bàn cờ từ session
//        Board board = (Board) session.getAttribute("gameBoard");
//        if (board == null) {
//            response.put("saved", false);
//            response.put("message", "Không tìm thấy trạng thái trò chơi");
//            return response;
//        }
//
//        // Kiểm tra trò chơi đã kết thúc chưa
//        if (!board.isGameOver()) {
//            response.put("saved", false);
//            response.put("message", "Trò chơi chưa kết thúc");
//            return response;
//        }
//
//        // Xác định kết quả
//        boolean playerWon = board.getWinner() == Board.BLACK;
//
//        String externalServiceUrl = String.format(
//                "http://localhost:8083/api/save-result?memberId=%d&botId=1&result=%b",
//                loggedInMember.getId(),
//                playerWon
//        );
//        HttpHeaders headers = new HttpHeaders();
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
//                externalServiceUrl,
//                HttpMethod.POST,
//                request,
//                Boolean.class
//        );
//
//        Boolean saved = responseEntity.getBody();
//
//        response.put("saved", saved);
//        response.put("playerWon", playerWon);
//        response.put("message", saved ? "Kết quả đã được lưu" : "Lỗi khi lưu kết quả");
//
//        return response;
//    }
//
//    @PostMapping("/create-tournament")
//    public String createTournament(@RequestParam String description, Model model, HttpSession session) {
//        // Kiểm tra đăng nhập
//        if (session.getAttribute("loggedInMember") == null) {
//            return "redirect:/login";
//        }
//
//        // Tạo đối tượng Tournament
//        Tournament tournament = new Tournament();
//        tournament.setDescription(description);
//
//        // Tạo tournament trong database
////        TournamentDAO tournamentDAO = new TournamentDAO();
////        boolean created = tournamentDAO.createTournament(tournament);
//        String externalServiceUrl = "http://localhost:8084/api/create-tournament";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON); // optional but good practice
//
//        // Giả sử bạn có một Tournament object đã khởi tạo
//        HttpEntity<Tournament> request = new HttpEntity<>(tournament, headers);
//
//        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
//                externalServiceUrl,
//                HttpMethod.POST,
//                request,
//                Boolean.class
//        );
//
//        Boolean created = responseEntity.getBody();
//
//        if (created) {
//            return "redirect:/tournament.html?success=true";
//        } else {
//            model.addAttribute("error", "Failed to create tournament");
//            return "create-tournament";
//        }
//    }
//
//    private String boardToString(Board board) {
//        StringBuilder sb = new StringBuilder();
//        int[][] cells = board.getCells();
//
//        for (int row = 0; row < 8; row++) {
//            for (int col = 0; col < 8; col++) {
//                sb.append(cells[row][col]);
//            }
//        }
//
//        return sb.toString();
//    }
//}
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class ClientController {
    @Autowired
    private HttpSession session;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${game.service.url}")
    private String gameServiceUrl;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    /**
     * Authen + Author
     */
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInMember") != null) {
            return "redirect:/home.html";
        }

        model.addAttribute("loginMessage", model.asMap().get("loginMessage"));
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();
        String externalServiceUrl = authServiceUrl + "/api/login";

        HttpHeaders headers = new HttpHeaders();

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(password);
        HttpEntity<Member> request = new HttpEntity<>(member, headers);
        ResponseEntity<Member> responseEntity = restTemplate.exchange(
                externalServiceUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Member>() {}
        );
        member = responseEntity.getBody();

        if (member != null) {
            // Đăng nhập thành công, lưu thông tin vào session
            session.setAttribute("loggedInMember", member);
            return "redirect:/home";
        } else {
            // Đăng nhập thất bại
            redirectAttributes.addFlashAttribute("loginMessage", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpSession session) {
        // Nếu đã đăng nhập, chuyển hướng đến trang chủ
        if (session.getAttribute("loggedInMember") != null) {
            return "redirect:/home";
        }

        model.addAttribute("member", new Member());
        model.addAttribute("registerMessage", model.asMap().get("registerMessage"));
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute Member member,
                                      RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();
        String externalServiceUrl = authServiceUrl + "/api/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set default ELO value if not provided
        if (member.getElo() <= 0) {
            member.setElo(1000);
        }

        HttpEntity<Member> request = new HttpEntity<>(member, headers);

        try {
            ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                    externalServiceUrl,
                    HttpMethod.POST,
                    request,
                    Boolean.class
            );

            Boolean registered = responseEntity.getBody();

            if (registered != null && registered) {
                redirectAttributes.addFlashAttribute("loginMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("registerMessage", "Đăng ký thất bại. Tên đăng nhập hoặc email đã tồn tại.");
                return "redirect:/register";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("registerMessage", "Đăng ký thất bại: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * REST endpoint for game
     */
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        if (session.getAttribute("loggedInMember") != null) {
            model.addAttribute("member", session.getAttribute("loggedInMember"));
        }
        return "home";
    }

    @GetMapping("/play.html")
    public String play(HttpSession session, Model model) {
        // Kiểm tra đăng nhập trước khi cho phép chơi
        if (session.getAttribute("loggedInMember") == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", session.getAttribute("loggedInMember"));
        return "play";
    }

    @GetMapping("/play-with-computer")
    public String playWithBot(Model model, HttpSession session) {
        // Kiểm tra đăng nhập trước khi cho phép chơi với máy
        if (session.getAttribute("loggedInMember") == null) {
            return "redirect:/login";
        }

        // Create a new game if one doesn't exist
        if (session.getAttribute("gameBoard") == null) {
            Board board = new Board();
            session.setAttribute("gameBoard", board);
        }

        Board board = (Board) session.getAttribute("gameBoard");
        model.addAttribute("board", board);
        model.addAttribute("currentPlayer", board.getCurrentPlayer());
        model.addAttribute("boardState", boardToString(board));
        model.addAttribute("validMoves", board.getValidMoves(board.getCurrentPlayer()));

        // Add scores
        Map<String, Integer> scores = new HashMap<>();
        scores.put("black", board.getBlackCount());
        scores.put("white", board.getWhiteCount());
        model.addAttribute("scores", scores);

        // Thêm thông tin người dùng đăng nhập vào model
        model.addAttribute("member", session.getAttribute("loggedInMember"));

        return "play-with-computer";
    }

    @GetMapping("/join-room")
    public String joinRoom(Model model, HttpSession session) {
        // Kiểm tra đăng nhập trước khi cho phép tham gia phòng
        if (session.getAttribute("loggedInMember") == null) {
            return "redirect:/login";
        }

        model.addAttribute("member", session.getAttribute("loggedInMember"));
        return "join-room";
    }

    @GetMapping("/play-room/{roomId}")
    public String playRoom(@PathVariable String roomId, Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return "redirect:/login";
        }

        // Lấy thông tin phòng từ game service
        String url = gameServiceUrl + "/api/game/room/" + roomId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<GameRoom> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    GameRoom.class
            );

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                return "redirect:/play.html?error=invalid_room";
            }

            GameRoom room = responseEntity.getBody();
            model.addAttribute("room", room);
            model.addAttribute("member", loggedInMember);

            return "play-room";
        } catch (Exception e) {
            return "redirect:/play.html?error=room_error";
        }
    }

    /**
     * REST endpoint cho client sử dụng để lấy trạng thái bàn cờ
     */
    @GetMapping("/api/game/board")
    @ResponseBody
    public Map<String, Object> getBoardState(HttpSession session) {
        // Kiểm tra đăng nhập
        if (session.getAttribute("loggedInMember") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", "Vui lòng đăng nhập để tiếp tục");
            return errorResponse;
        }

        Board board = (Board) session.getAttribute("gameBoard");
        if (board == null) {
            board = new Board();
            session.setAttribute("gameBoard", board);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("boardState", boardToString(board));
        response.put("currentPlayer", board.getCurrentPlayer());
        response.put("blackScore", board.getBlackCount());
        response.put("whiteScore", board.getWhiteCount());
        response.put("gameOver", board.isGameOver());
        response.put("winner", board.getWinner());

        return response;
    }

    @PostMapping("/api/game/move")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> makeMove(@RequestBody Map<String, Integer> moveData, HttpSession session) {
        // Kiểm tra đăng nhập
        if (session.getAttribute("loggedInMember") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", "Vui lòng đăng nhập để tiếp tục");
            return ResponseEntity.status(401).body(errorResponse);
        }

        int row = moveData.get("row");
        int col = moveData.get("col");
        int player = moveData.get("player");

        Board board = (Board) session.getAttribute("gameBoard");
        if (board == null) {
            board = new Board();
            session.setAttribute("gameBoard", board);
        }

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra nước đi có hợp lệ không
        if (board.isValidMove(row, col, player)) {
            // Thực hiện nước đi, call bot-service
            board.makeMove(row, col, player);

            // Cập nhật bàn cờ trong session
            session.setAttribute("gameBoard", board);

            // Trả về trạng thái mới
            response.put("boardState", boardToString(board));
            response.put("currentPlayer", board.getCurrentPlayer());
            response.put("blackScore", board.getBlackCount());
            response.put("whiteScore", board.getWhiteCount());
            response.put("success", true);
            response.put("gameOver", board.isGameOver());
            response.put("winner", board.getWinner());

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Nước đi không hợp lệ");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/game/bot-move")
    @ResponseBody
    public Map<String, Object> makeBotMove(HttpSession session) {
        // Kiểm tra đăng nhập
        if (session.getAttribute("loggedInMember") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", "Vui lòng đăng nhập để tiếp tục");
            return errorResponse;
        }

        Board board = (Board) session.getAttribute("gameBoard");
        if (board == null) {
            board = new Board();
            session.setAttribute("gameBoard", board);
        }

        // Đảm bảo lượt chơi hiện tại là bot (WHITE = 2)
        if (board.getCurrentPlayer() == 2) {
            // Gọi BotService để lấy nước đi của bot
            String externalServiceUrl = gameServiceUrl + "/api/game/move?botId=2";
            HttpHeaders headers = new HttpHeaders();

            HttpEntity<Board> request = new HttpEntity<>(board, headers);
            ResponseEntity<Board> responseEntity = restTemplate.exchange(
                    externalServiceUrl,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Board>() {}
            );
            board = responseEntity.getBody();

            // Cập nhật bàn cờ trong session
            session.setAttribute("gameBoard", board);
        }

        // Trả về trạng thái mới
        Map<String, Object> response = new HashMap<>();
        response.put("boardState", boardToString(board));
        response.put("currentPlayer", board.getCurrentPlayer());
        response.put("blackScore", board.getBlackCount());
        response.put("whiteScore", board.getWhiteCount());
        response.put("gameOver", board.isGameOver());
        response.put("winner", board.getWinner());

        return response;
    }

    @PostMapping("/save-game-result")
    @ResponseBody
    public Map<String, Object> saveGameResult(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra đăng nhập
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("saved", false);
            response.put("message", "Vui lòng đăng nhập để lưu kết quả");
            return response;
        }

        // Lấy trạng thái bàn cờ từ session
        Board board = (Board) session.getAttribute("gameBoard");
        if (board == null) {
            response.put("saved", false);
            response.put("message", "Không tìm thấy trạng thái trò chơi");
            return response;
        }

        // Kiểm tra trò chơi đã kết thúc chưa
        if (!board.isGameOver()) {
            response.put("saved", false);
            response.put("message", "Trò chơi chưa kết thúc");
            return response;
        }

        // Xác định kết quả
        boolean playerWon = board.getWinner() == Board.BLACK;

        String externalServiceUrl = String.format(
                gameServiceUrl + "/api/save-result?memberId=%d&botId=1&result=%b",
                loggedInMember.getId(),
                playerWon
        );
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                externalServiceUrl,
                HttpMethod.POST,
                request,
                Boolean.class
        );

        Boolean saved = responseEntity.getBody();

        response.put("saved", saved);
        response.put("playerWon", playerWon);
        response.put("message", saved ? "Kết quả đã được lưu" : "Lỗi khi lưu kết quả");

        return response;
    }

    @PostMapping("/create-tournament")
    public String createTournament(@RequestParam String description, Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        if (session.getAttribute("loggedInMember") == null) {
            return "redirect:/login";
        }

        // Tạo đối tượng Tournament
        Tournament tournament = new Tournament();
        tournament.setDescription(description);

        // Tạo tournament trong database
        String externalServiceUrl = gameServiceUrl + "/api/create-tournament";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Tournament> request = new HttpEntity<>(tournament, headers);

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                externalServiceUrl,
                HttpMethod.POST,
                request,
                Boolean.class
        );

        Boolean created = responseEntity.getBody();

        if (created) {
            return "redirect:/tournament.html?success=true";
        } else {
            model.addAttribute("error", "Failed to create tournament");
            return "create-tournament";
        }
    }

    @GetMapping("/reset-game")
    public String resetGame(HttpSession session) {
        // Xoá bàn cờ hiện tại
        session.removeAttribute("gameBoard");
        return "redirect:/play-with-computer";
    }

    @GetMapping("/tournament.html")
    public String tournament(Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        if (session.getAttribute("loggedInMember") == null) {
            return "redirect:/login";
        }

        model.addAttribute("member", session.getAttribute("loggedInMember"));
        return "tournament";
    }

    @GetMapping("/api/game/room/{roomId}/status")
    @ResponseBody
    public Map<String, Object> getRoomStatus(@PathVariable String roomId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra đăng nhập
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            response.put("error", "Unauthorized");
            return response;
        }

        // Call game service
        String url = gameServiceUrl + "/api/game/room/" + roomId + "/status";

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
            response.put("error", "Error fetching room status: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/api/game/room/{roomId}/chat")
    @ResponseBody
    public ResponseEntity<Void> sendRoomChatMessage(@PathVariable String roomId,
                                                    @RequestBody Map<String, String> messageData,
                                                    HttpSession session) {
        // Kiểm tra đăng nhập
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Call game service
        String url = gameServiceUrl + "/api/game/room/" + roomId + "/chat";

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

    @GetMapping("/api/game/room/{roomId}/chat")
    @ResponseBody
    public List<ChatMessage> getRoomChatMessages(@PathVariable String roomId, HttpSession session) {
        // Kiểm tra đăng nhập
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return new ArrayList<>();
        }

        // Call game service
        String url = gameServiceUrl + "/api/game/room/" + roomId + "/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", String.valueOf(loggedInMember.getId()));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<ChatMessage>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<List<ChatMessage>>() {}
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String boardToString(Board board) {
        StringBuilder sb = new StringBuilder();
        int[][] cells = board.getCells();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                sb.append(cells[row][col]);
            }
        }

        return sb.toString();
    }
}