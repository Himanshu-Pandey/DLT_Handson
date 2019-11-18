package thoughtworks.quorum.clientapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import thoughtworks.quorum.clientapi.service.ContractLoader;
import thoughtworks.quorum.clientapi.service.Web3jService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@RestController
public class VoteController {

    @Autowired
    Web3jService web3jService;

    @Autowired
    private ContractLoader contractLoader;

    @RequestMapping("/voters")
    @GetMapping
    public ResponseEntity<List<String>> getAllUserAccounts() throws IOException {
        return ResponseEntity.ok(web3jService.getAccounts());
    }

    @RequestMapping("/vote/{party}")
    @PostMapping
    public ResponseEntity castVote(@PathVariable Integer party) throws Exception {
        TransactionReceipt receipt = contractLoader.loadContract().vote(BigInteger.valueOf(party)).send();
        return ResponseEntity.ok().build();
    }
}
