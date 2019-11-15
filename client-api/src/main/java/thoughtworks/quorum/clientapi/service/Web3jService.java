package thoughtworks.quorum.clientapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.quorum.Quorum;

import java.io.IOException;

@Service
public class Web3jService {

    @Autowired
    Quorum quorum;

    public String getBlockNumber() {
        System.out.println(quorum);
        String blockNumber = "";
        try {
            blockNumber = this.quorum.ethBlockNumber().send().getBlockNumber().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockNumber;
    }

}
