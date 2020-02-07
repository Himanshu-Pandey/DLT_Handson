package thoughtworks.quorum.clientapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import thoughtworks.quorum.clientapi.service.Web3jService;

import java.math.BigInteger;

@CrossOrigin("*")
@RestController
public class VoteController {

    @Autowired
    Web3jService web3jService;

    @RequestMapping("booth/{boothId}/user/{userId}/vote/{candidate}")
    @PostMapping
    public ResponseEntity<String> castVote(@PathVariable String boothId, @PathVariable String userId, @PathVariable Integer candidate) throws Exception {
        TransactionReceipt receipt = web3jService.loadContract(boothId,userId).vote(BigInteger.valueOf(candidate)).send();
        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(receipt));
    }
}
