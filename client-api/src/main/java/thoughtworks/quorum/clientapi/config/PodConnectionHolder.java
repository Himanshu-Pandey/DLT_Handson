package thoughtworks.quorum.clientapi.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PodConnectionHolder {
    private Map<String, PodConfiguration> podConfigurationMap;

    public PodConnectionHolder(@Value("${pod.config}") String configJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        podConfigurationMap =
            mapper.readValue(configJson, new TypeReference<Map<String, PodConfiguration>>() {
            });
    }

    public PodConfiguration getPodConfigurationMap(String podId) {
        return podConfigurationMap.get(podId);
    }

    public  String getQuorumUrl(String bootId){
        PodConfiguration podConfiguration = podConfigurationMap.get(bootId);
        return String.format("http://%s:%s", podConfiguration.getHost(), podConfiguration.getPort());

    }

    public String getAccountAddress(String boothId, String userId) {
        Optional<Account> first = podConfigurationMap.get(boothId).getAccounts().stream().filter(account -> account.getName().equalsIgnoreCase(userId)).findFirst();
        if (first.isPresent()) {
            return first.get().getAddress();
        } else {
            return "";
        }
    }

}
