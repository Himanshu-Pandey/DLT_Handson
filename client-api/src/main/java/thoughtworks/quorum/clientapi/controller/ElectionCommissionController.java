package thoughtworks.quorum.clientapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thoughtworks.quorum.clientapi.config.Account;
import thoughtworks.quorum.clientapi.config.PodConnectionHolder;
import thoughtworks.quorum.clientapi.contract.Election;
import thoughtworks.quorum.clientapi.service.Web3jService;

import java.math.BigInteger;
import java.util.List;

@CrossOrigin("*")
@RestController
public class ElectionCommissionController {

    @Autowired
    private Web3jService web3jService;

    @Autowired
    private PodConnectionHolder config;

    @RequestMapping("/election-commission/{boothId}/voter-accounts")
    @GetMapping
    public ResponseEntity<List<String>> getAllUserAccounts(@PathVariable String boothId) {
        return ResponseEntity.ok(web3jService.getAccounts(boothId));
    }

    @RequestMapping("/election-commission/{boothId}/voters")
    @GetMapping
    public ResponseEntity<List<Account>> getAllVoters(@PathVariable String boothId) {
        return ResponseEntity.ok(config.getPodConfigurationMap(boothId).getAccounts());
    }

    @RequestMapping("/election-commission/refresh")
    @PostMapping
    public ResponseEntity refresh() {
        try {
            web3jService.deployContract();
            return ResponseEntity.ok("Done");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @RequestMapping("/election-commission/results")
    @GetMapping
    public ResponseEntity getWinner() throws Exception {
        Election electionContract = web3jService.loadContract("EC", "EC1");
        BigInteger candidate1Votes = electionContract.getVotesFor(BigInteger.valueOf(1)).send();
        BigInteger candidate2Votes = electionContract.getVotesFor(BigInteger.valueOf(2)).send();
        switch (candidate1Votes.compareTo(candidate2Votes)) {
            case 1:
                return ResponseEntity.ok().body("candidate 1 is the winner");
            case -1:
                return ResponseEntity.ok().body("candidate 2 is the winner");
            default:
                return ResponseEntity.ok().body("Both Are winner");
        }
    }
}
