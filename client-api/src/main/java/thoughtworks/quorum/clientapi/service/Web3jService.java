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
import thoughtworks.quorum.clientapi.config.PodConfiguration;
import thoughtworks.quorum.clientapi.config.PodConnectionHolder;
import thoughtworks.quorum.clientapi.contract.Election;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class Web3jService {

    public static final String EC = "EC";
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

    private PodConnectionHolder podConnectionHolder;

    public Web3jService(PodConnectionHolder podConnectionHolder) {
        this.podConnectionHolder = podConnectionHolder;
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
        ScheduledExecutorService scheduledExecutorService = Async.defaultExecutorService();
        Admin admin1 = Admin.build(this.getHttpService(boothId), (long) this.rpcPollingInterval, scheduledExecutorService);
        Election election = Election.load(electionAddress, admin1, getPrivateTransactionManagerForNodeAndEC(boothId, userId), BigInteger.valueOf(gasPrice),
                BigInteger.valueOf(gasLimit));

            return election;
    }
    public String getBlockNumber() {
        Quorum quorum = Quorum.build(this.getHttpService(PodType.EC.name()));
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
        String quorumUrl = podConnectionHolder.getQuorumUrl(boothId);
        Admin admin = Admin.build(new HttpService(quorumUrl));
        try {
            return admin.personalListAccounts().send().getAccountIds();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ClientTransactionManager getPrivateTransactionManagerForAllNodes(String boothId) {
        String account = podConnectionHolder.getAccountAddress(EC, "EC1");
        Quorum quorum = Quorum.build(this.getHttpService(boothId));
        return new ClientTransactionManager(quorum, account, Arrays.asList(booth1ConstellationPubKey, booth2ConstellationPubKey), 3, this.txMgrSleepDuration);
    }

    private ClientTransactionManager getPrivateTransactionManagerForNodeAndEC(String boothId, String userId) {
        String accountAddress = podConnectionHolder.getAccountAddress(boothId, userId);
        Quorum quorum = Quorum.build(this.getHttpService(boothId));
        PodConfiguration podConfigurationMap = podConnectionHolder.getPodConfigurationMap(EC);
        return new ClientTransactionManager(quorum, accountAddress, Arrays.asList(podConfigurationMap.getConstellationPubKey()), 3, this.txMgrSleepDuration);
    }

    public HttpService getHttpService(String boothId) {
        String quorumUrl = podConnectionHolder.getQuorumUrl(boothId);
        OkHttpClient okHttpClient = (new OkHttpClient.Builder()).connectTimeout((long) this.rpcConnectionTimeout, TimeUnit.MILLISECONDS).readTimeout((long) this.rpcReadTimeout, TimeUnit.MILLISECONDS).connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT)).build();
        return new HttpService(quorumUrl, okHttpClient, false);
    }
}
