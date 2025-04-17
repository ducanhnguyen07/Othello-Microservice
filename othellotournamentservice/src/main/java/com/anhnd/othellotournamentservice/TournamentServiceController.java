package com.anhnd.othellotournamentservice;

import com.anhnd.othellotournamentservice.dao.TournamentDAO;
import com.anhnd.othellotournamentservice.model.Tournament;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TournamentServiceController {
    @PostMapping("create-tournament")
    public boolean createTournament(@RequestBody Tournament tournament) {
        return new TournamentDAO().createTournament(tournament);
    }
}
