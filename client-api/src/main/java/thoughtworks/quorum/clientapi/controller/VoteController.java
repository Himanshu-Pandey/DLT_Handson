package thoughtworks.quorum.clientapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thoughtworks.quorum.clientapi.service.Web3jService;

import java.io.IOException;
import java.util.List;

@RestController
public class VoteController {

    @Autowired
    Web3jService web3jService;

    @RequestMapping("/voters")
    @GetMapping
    public ResponseEntity<List<String>> getAllUserAccounts() throws IOException {
        return ResponseEntity.ok(web3jService.getAccounts());
    }
}
