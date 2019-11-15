package thoughtworks.quorum.clientapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;

@Configuration
public class Web3j {

    @Value("${quorum.host}")
    private String quorumHost;

    @Value("${quorum.port}")
    private String quorumPort;

    @Bean
    Quorum quorum() {
        String quorumUrl = String.format("http://%s:%s", quorumHost, quorumPort);
        return Quorum.build(new HttpService(quorumUrl));
    }

}
