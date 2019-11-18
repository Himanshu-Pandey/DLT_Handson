package thoughtworks.quorum.clientapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thoughtworks.quorum.clientapi.service.Web3jService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@RestController
public class ElectionCommissionController {

    @Autowired
    private Web3jService web3jService;

    @RequestMapping("/election-commission/{boothId}/voters")
    @GetMapping
    public ResponseEntity<List<String>> getAllUserAccounts(@PathVariable String boothId) throws IOException {
        return ResponseEntity.ok(web3jService.getAccounts(boothId));
    }

    @RequestMapping("/election-commission/results")
    @GetMapping
    public ResponseEntity getWinner() throws Exception {
        BigInteger party1Votes = web3jService.getContract().getVotesFor(BigInteger.valueOf(1)).send();
        BigInteger party2Votes = web3jService.getContract().getVotesFor(BigInteger.valueOf(2)).send();
        switch (party1Votes.compareTo(party2Votes)) {
            case 1:
                return ResponseEntity.ok().body("Party 1 is the winner");
            case -1:
                return ResponseEntity.ok().body("Party 2 is the winner");
            default:
                return ResponseEntity.ok().body("Both Are winner");
        }
    }
}
