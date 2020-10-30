package eu.siacs.p2;

import eu.siacs.p2.pojo.Target;
import eu.siacs.p2.xmpp.extensions.push.MessageBody;

public interface PushService {
    boolean push(Target target, String sender, MessageBody body);
}
