package thoughtworks.quorum.clientapi.controller;

import thoughtworks.quorum.clientapi.service.Web3jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StatusController {

    @Autowired
    private Web3jService web3jService;

    @RequestMapping("/status")
    public ResponseEntity<Map> status() {
        Map<String,String> statusJson = new HashMap<>();

        try{
            statusJson.put("status","ok");
            statusJson.put("blockNumber",web3jService.getBlockNumber());
        }
        catch(Exception ex){
            statusJson.put("status","error");
            statusJson.put("error",ex.getMessage());
        }

        return ResponseEntity.ok(statusJson);
    }
}
