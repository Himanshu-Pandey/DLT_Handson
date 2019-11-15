package thoughtworks.quorum.clientapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;

import java.io.IOException;
import java.util.List;

@Service
public class Web3jService {

    @Autowired
    Quorum quorum;

    @Value("${quorum.host}")
    private String quorumHost;

    @Value("${quorum.port}")
    private String quorumPort;

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

    public List<String> getAccounts() {
        String quorumUrl = String.format("http://%s:%s", quorumHost, quorumPort);
        Admin admin = Admin.build(new HttpService(quorumUrl));

        try {
            return admin.personalListAccounts().send().getAccountIds();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
