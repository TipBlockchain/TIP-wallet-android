package io.tipblockchain.kasakasa.blockchain.eth;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.ScheduledExecutorService;

public class Web3jFactory {

    public static Web3j build(String endpoint) {
        return Web3j.build(new HttpService(endpoint));
    }


    public static Web3j build(Web3jService web3jService, long pollingInterval,
                              ScheduledExecutorService scheduledExecutorService) {
        return Web3j.build(web3jService, pollingInterval, scheduledExecutorService);
    }
}
