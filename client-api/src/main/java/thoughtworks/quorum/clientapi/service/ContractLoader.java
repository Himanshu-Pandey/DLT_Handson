package thoughtworks.quorum.clientapi.service;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.tx.ClientTransactionManager;
import org.web3j.utils.Async;
import thoughtworks.quorum.clientapi.contract.Election;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ContractLoader {

    @Autowired
    private Quorum quorum;

    private Admin admin;

    @Autowired
    private Web3jService web3jService;

    @Value("${quorum.constellation.pub.key1}")
    private String constellationPubKeyNode1;

    @Value("${quorum.constellation.pub.key2}")
    private String constellationPubKeyNode2;

    @Value("${quorum.constellation.pub.key3}")
    private String constellationPubKeyNode3;

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

    @Value("${quorum.port}")
    private String quorumPort;

    @Value("${web3.gas.price}")
    private Long gasPrice;

    @Value("${web3.gas.limit}")
    private Long gasLimit;

    @Value("${web3.rpc.polling.interval}")
    private Integer rpcPollingInterval;

    private Election election;

    @PostConstruct
    public void init() throws Exception {
        ScheduledExecutorService scheduledExecutorService = Async.defaultExecutorService();
        this.admin = Admin.build(this.getHttpService(), (long) this.rpcPollingInterval, scheduledExecutorService);
        this.quorum = Quorum.build(this.getHttpService());
        this.election = Election.deploy(admin,
            getPrivateTransactionManager(),
            BigInteger.valueOf(gasPrice),
            BigInteger.valueOf(gasLimit)).send();
    }

    public Election loadContract() {
        String contractAddress = getContract().getContractAddress();
        Election election = Election.load(contractAddress, this.admin, getPrivateTransactionManager(), BigInteger.valueOf(gasPrice),
            BigInteger.valueOf(gasLimit));

        return election;
    }

    public ClientTransactionManager getPrivateTransactionManager() {
        String account = web3jService.getAccounts().get(0);
        return new ClientTransactionManager(quorum, account, Arrays.asList(constellationPubKeyNode2, constellationPubKeyNode3), 3, this.txMgrSleepDuration);
    }

    public HttpService getHttpService() {
        String quorumUrl = String.format("http://%s:%s", quorumHost, quorumPort);
        OkHttpClient okHttpClient = (new OkHttpClient.Builder()).connectTimeout((long)this.rpcConnectionTimeout, TimeUnit.MILLISECONDS).readTimeout((long)this.rpcReadTimeout, TimeUnit.MILLISECONDS).connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT)).build();
        return new HttpService(quorumUrl, okHttpClient, false);
    }

    public Election getContract() {
        return election;
    }
}
