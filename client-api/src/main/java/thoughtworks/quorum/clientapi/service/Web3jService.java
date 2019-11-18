package thoughtworks.quorum.clientapi.service;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.tx.ClientTransactionManager;
import org.web3j.utils.Async;
import thoughtworks.quorum.clientapi.contract.Election;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static thoughtworks.quorum.clientapi.service.PodType.EC;

@Service
public class Web3jService {

    private Admin admin;

    @Value("${quorum.constellation.pub.key1}")
    private String electionCommisionConstellationPubKey;

    @Value("${quorum.constellation.pub.key2}")
    private String booth1ConstellationPubKey;

    @Value("${quorum.constellation.pub.key3}")
    private String booth2ConstellationPubKey;

    @Value("${web3.tx.mgr.connection.attempts}")
    public int txMgrConnectionAttempts;

    @Value("${web3.tx.mgr.sleep.duration}")
    public int txMgrSleepDuration;

    @Value("${web3.rpc.connection.timeout}")
    private Integer rpcConnectionTimeout;

    @Value("${web3.rpc.read.timeout}")
    private Integer rpcReadTimeout;

    @Value("${quorum.host}")
    private String quorumHost;

    @Value("${web3.gas.price}")
    private Long gasPrice;

    @Value("${web3.gas.limit}")
    private Long gasLimit;

    @Value("${web3.rpc.polling.interval}")
    private Integer rpcPollingInterval;

    private String electionAddress;

    Map<String,String> userToAccountsMap = new HashMap<>();

    public Web3jService() {
        this.userToAccountsMap.put("ec","0xe050f4397afa2d73cc00003f754405e5764010e7");
        this.userToAccountsMap.put("manali","0xc2d7cf95645d33006175b78989035c7c9061d3f9");
        this.userToAccountsMap.put("juhi","0xa2951d23f20a95716a37b3cc849df67c5302d205");
        this.userToAccountsMap.put("navdeep","0xd9794dbeb48ab17d6acc4ceb0be8755f11caa509");
        this.userToAccountsMap.put("himanshu","0x82e3571a028b57c347cd423031a34f2e712927c1");
    }

    @PostConstruct
    public void deployContract() throws Exception {
        String boothId = PodType.EC.name();
        ScheduledExecutorService scheduledExecutorService = Async.defaultExecutorService();

        this.admin = Admin.build(this.getHttpService(boothId), (long) this.rpcPollingInterval, scheduledExecutorService);
        Election electionHash = Election.deploy(admin,
                getPrivateTransactionManagerForAllNodes(PodType.EC.name()),
                BigInteger.valueOf(gasPrice),
                BigInteger.valueOf(gasLimit)).send();
        electionAddress = electionHash.getContractAddress();

        System.out.println("Election contract deployed at : " + electionAddress);
        System.out.println("Transaction Hash : " + electionHash.getTransactionReceipt().get().getTransactionHash());
    }

    public Election loadContract(String boothId, String userId) {
        Election election = Election.load(electionAddress, this.admin, getPrivateTransactionManagerForNodeAndEC(boothId, userId), BigInteger.valueOf(gasPrice),
                BigInteger.valueOf(gasLimit));

        return election;
    }

    public HttpService getHttpService(String boothId) {
        String quorumUrl = String.format("http://%s:%s", quorumHost, getQuorumPortForPod(boothId));
        OkHttpClient okHttpClient = (new OkHttpClient.Builder()).connectTimeout((long) this.rpcConnectionTimeout, TimeUnit.MILLISECONDS).readTimeout((long) this.rpcReadTimeout, TimeUnit.MILLISECONDS).connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT)).build();
        return new HttpService(quorumUrl, okHttpClient, false);
    }

    public Election getContract() {
        return loadContract(EC.name(), userToAccountsMap.get(EC.toString()));
    }

    public String getBlockNumber() {
        Quorum quorum = Quorum.build(this.getHttpService(EC.name()));
        System.out.println(quorum);
        String blockNumber = "";
        try {
            blockNumber = quorum.ethBlockNumber().send().getBlockNumber().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockNumber;
    }

    public List<String> getAccounts(String boothId) {
        String quorumUrl = String.format("http://%s:%s", quorumHost, getQuorumPortForPod(boothId));
        Admin admin = Admin.build(new HttpService(quorumUrl));

        try {
            return admin.personalListAccounts().send().getAccountIds();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ClientTransactionManager getPrivateTransactionManagerForAllNodes(String boothId) {
        String account = userToAccountsMap.get(EC.toString());
        Quorum quorum = Quorum.build(this.getHttpService(boothId));
        return new ClientTransactionManager(quorum, account, Arrays.asList(booth1ConstellationPubKey, booth2ConstellationPubKey), 3, this.txMgrSleepDuration);
    }

    private ClientTransactionManager getPrivateTransactionManagerForNodeAndEC(String boothId, String userId) {
        String account = userToAccountsMap.get(userId);
        Quorum quorum = Quorum.build(this.getHttpService(boothId));
        return new ClientTransactionManager(quorum, account, Arrays.asList(electionCommisionConstellationPubKey), 3, this.txMgrSleepDuration);
    }

    private String getQuorumPortForPod(String boothId) {
        String port = "24001";
        PodType pd = PodType.valueOf(boothId.toUpperCase());
        switch (pd) {
            case EC:
                port = "24001";
                break;
            case BOOTH1:
                port = "24002";
                break;
            case BOOTH2:
                port = "24003";
                break;
        }
        return port;
    }

}
