package thoughtworks.quorum.clientapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thoughtworks.quorum.clientapi.service.ContractLoader;
import java.math.BigInteger;

@RestController
public class ElectionCommissionController {

    @Autowired
    private ContractLoader contractLoader;

    @RequestMapping("/winner")
    @GetMapping
    public ResponseEntity getWinner() throws Exception {
        BigInteger party1Votes = contractLoader.getContract().getVotesFor(BigInteger.valueOf(1)).send();
        BigInteger party2Votes = contractLoader.getContract().getVotesFor(BigInteger.valueOf(2)).send();
        switch (party1Votes.compareTo(party2Votes)) {
            case 1:
                return ResponseEntity.ok().body("Party 1 is the winner");
            case 2:
                return ResponseEntity.ok().body("Party 2 is the winner");
            default:
                return ResponseEntity.ok().body("Both Are winner");
        }
    }
}
