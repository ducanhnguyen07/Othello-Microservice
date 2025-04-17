package com.example.othellomemberservice;

import com.example.othellomemberservice.dao.MemberDAO;
import com.example.othellomemberservice.model.Member;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api")
public class MemberServiceController {
    @PostMapping("/login")
    public Member processLogin(@RequestBody Member member) {

        return new MemberDAO().authenticate(member);
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

}
