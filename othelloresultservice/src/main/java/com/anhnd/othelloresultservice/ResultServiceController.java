package com.anhnd.othelloresultservice;

import com.anhnd.othelloresultservice.dao.ResultDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResultServiceController {
    @PostMapping("/save-result")
    public boolean saveGameResult(
            @RequestParam("memberId") Integer memberId,
            @RequestParam("botId") Integer botId,
            @RequestParam("result") Boolean result)
    {
        return new ResultDAO().saveGameResult(memberId, botId, result);
    }

}
