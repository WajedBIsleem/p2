package eu.siacs.p2.persistance;

import com.google.common.collect.ImmutableMap;
import eu.siacs.p2.Configuration;
import eu.siacs.p2.persistance.converter.JidConverter;
import eu.siacs.p2.pojo.Service;
import eu.siacs.p2.pojo.Target;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.Quirks;
import rocks.xmpp.addr.Jid;

import java.util.Map;

public class LogStore {

    private static final Map<Class, Converter> CONVERTERS;
    private static final String CREATE_TARGET_TABLE = "create table if not exists log(id INT NOT NULL AUTO_INCREMENT, account CHAR(50), device CHAR(50), details CHAR(255), created_at DATETIME, PRIMARY KEY (id));";
    private static LogStore INSTANCE = null;

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

    private LogStore() {
        final Configuration configuration = Configuration.getInstance();
        final Quirks quirks = new NoQuirks(CONVERTERS);
        database = new Sql2o(configuration.getDbUrl(), configuration.getDbUsername(), configuration.getDbPassword(),
                quirks);
        try (Connection connection = database.open()) {
            connection.createQuery(CREATE_TARGET_TABLE).executeUpdate();
        }
    }

    public static synchronized LogStore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LogStore();
        }
        return INSTANCE;
    }

    public void create(String account, String device, String details) {
        try (Connection connection = database.open()) {
            connection.createQuery(
                    "INSERT INTO log (account,device,details,created_at) VALUES(:account,:device,:details,now())")
                    .addParameter("account", account).addParameter("device", device).addParameter("details", details)
                    .executeUpdate();
        }
    }
}
