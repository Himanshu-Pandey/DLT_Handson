package thoughtworks.quorum.clientapi.config;

import thoughtworks.quorum.clientapi.service.PodType;

import java.util.List;

public class PodConfiguration {
    private String host;
    private String port;
    private String constellationPubKey;
    private List<Account> accounts;
    private PodType type;

    public PodConfiguration() {
    }

    public PodConfiguration(String host, String port, String constellationPubKey, List<Account> accounts, PodType type) {
        this.host = host;
        this.port = port;
        this.constellationPubKey = constellationPubKey;
        this.accounts = accounts;
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public PodType getType() {
        return type;
    }

    public void setType(PodType type) {
        this.type = type;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getConstellationPubKey() {
        return constellationPubKey;
    }

    public void setConstellationPubKey(String constellationPubKey) {
        this.constellationPubKey = constellationPubKey;
    }
}
