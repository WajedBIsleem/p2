package live.punkpanda.p2;

import live.punkpanda.p2.pojo.Target;
import live.punkpanda.p2.xmpp.extensions.push.MessageBody;

public interface PushService {
    boolean push(Target target, String sender, MessageBody body);
}
