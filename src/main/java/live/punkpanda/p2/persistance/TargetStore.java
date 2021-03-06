package live.punkpanda.p2.persistance;

import com.google.common.collect.ImmutableMap;
import live.punkpanda.p2.Configuration;
import live.punkpanda.p2.persistance.converter.JidConverter;
import live.punkpanda.p2.pojo.Service;
import live.punkpanda.p2.pojo.Target;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.Quirks;
import rocks.xmpp.addr.Jid;

import java.util.Map;

public class TargetStore {

    private static final Map<Class, Converter> CONVERTERS;
    private static final String CREATE_TARGET_TABLE = "create table if not exists target(service char(5), account char(50), device char(40) NOT NULL, domain varchar(253), token varchar(255), token2 varchar(255), node char(12), secret char(24), index nodeDomain (node,domain));";
    private static final String CREATE_LOG_TABLE = "create table if not exists log(id INT NOT NULL AUTO_INCREMENT, title text, details text, created_at DATETIME, PRIMARY KEY (id));";
    private static TargetStore INSTANCE = null;

    static {
        try {
            CONVERTERS = new ImmutableMap.Builder<Class, Converter>().put(Jid.class, new JidConverter())
                    .put(Class.forName("rocks.xmpp.addr.FullJid"), new JidConverter())
                    .put(Class.forName("rocks.xmpp.addr.FullJid$1"), new JidConverter()).build();
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private final Sql2o database;

    private TargetStore() {
        final Configuration configuration = Configuration.getInstance();
        final Quirks quirks = new NoQuirks(CONVERTERS);
        database = new Sql2o(configuration.getDbUrl(), configuration.getDbUsername(), configuration.getDbPassword(),
                quirks);
        try (Connection connection = database.open()) {
            connection.createQuery(CREATE_TARGET_TABLE).executeUpdate();
            connection.createQuery(CREATE_LOG_TABLE).executeUpdate();
        }
    }

    public static synchronized TargetStore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TargetStore();
        }
        return INSTANCE;
    }

    public void log(String title,String details) {
        try (Connection connection = database.open()) {
            connection.createQuery(
                "INSERT INTO log (title,details,created_at) VALUES(:title,:details,now())")
                .addParameter("title", title)
                .addParameter("details", details)
                .executeUpdate();
        }
    }

    public void create(Target target) {
        Target t = find(target.getService(), target.getAccount(), target.getDevice(), target.getToken());
        if (t == null) {
            try (Connection connection = database.open()) {
                connection.createQuery(
                        "INSERT INTO target (service,account,device,domain,token,token2,node,secret) VALUES(:service,:account,:device,:domain,:token,:token2,:node,:secret)")
                        .bind(target).executeUpdate();
            }
        }
    }

    public Target find(String node) {
        try (Connection connection = database.open()) {
            return connection.createQuery(
                    "select service,account,device,domain,token,token2,node,secret from target where node=:node limit 1")
                    .addParameter("node", node).executeAndFetchFirst(Target.class);
        }
    }

    public Target find(Service service, String account, String device, String token) {
        try (Connection connection = database.open()) {
            return connection.createQuery(
                    "select service,account,device,domain,token,token2,node,secret from target where service=:service and account=:account and (device=:device or token=:token)")
                    .addParameter("service", service).addParameter("account", account).addParameter("device", device).addParameter("token", token)
                    .executeAndFetchFirst(Target.class);
        }
    }

    public boolean update(Target target) {
        try (Connection connection = database.open()) {
            return connection.createQuery("update target set token=:token, token2=:token2 where account=:account and device=:device").bind(target).executeUpdate().getResult() == 1;
        }
    }

    public boolean delete(String account, String device) {
        try (Connection connection = database.open()) {
            return connection.createQuery("delete from target where account=:account and device=:device")
                    .addParameter("account", account).addParameter("device", device).executeUpdate().getResult() == 1;
        }
    }

}
