package thoughtworks.quorum.clientapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import thoughtworks.quorum.clientapi.service.Web3jService;

import java.math.BigInteger;

@RestController
public class VoteController {

    @Autowired
    Web3jService web3jService;

    @RequestMapping("booth/{boothId}/user/{userId}/vote/{party}")
    @PostMapping
    public ResponseEntity<String> castVote(@PathVariable String boothId, @PathVariable String userId, @PathVariable Integer party) throws Exception {
        TransactionReceipt receipt = web3jService.loadContract(boothId,userId).vote(BigInteger.valueOf(party)).send();
        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(receipt));
    }
}
