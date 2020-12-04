package live.punkpanda.p2.pojo;

import live.punkpanda.p2.P2;
import live.punkpanda.p2.Utils;
import rocks.xmpp.addr.Jid;

public class Target {

    private Service service;
    private String account;
    private String device;
    private String domain;
    private String token;
    private String node;
    private String secret;

    private Target(Service service, String account, String device, String domain, String token, String node,
            String secret) {
        this.service = service;
        this.account = account;
        this.device = device;
        this.domain = domain;
        this.token = token;
        this.node = node;
        this.secret = secret;
    }

    public static Target create(Service service, Jid account, String device, String token) {
        String node = Utils.random(3, P2.SECURE_RANDOM);
        String secret = Utils.random(6, P2.SECURE_RANDOM);
        return new Target(service, account.getLocal(), device, account.getDomain(), token, node, secret);
    }

    public Service getService() {
        return service;
    }

    public String getNode() {
        return node;
    }

    public String getSecret() {
        return secret;
    }

    public boolean setToken(String token) {
        if (this.token != null && this.token.equals(token)) {
            return false;
        }
        this.token = token;
        return true;
    }

    public String getAccount() {
        return account;
    }

    public String getDevice() {
        return device;
    }

    public String getToken() {
        return token;
    }

    public String getDomain() {
        return domain;
    }
}
