package live.punkpanda.p2.controller;

import live.punkpanda.p2.Configuration;
import live.punkpanda.p2.PushService;
import live.punkpanda.p2.PushServiceManager;
import live.punkpanda.p2.persistance.TargetStore;
import live.punkpanda.p2.pojo.Service;
import live.punkpanda.p2.pojo.Target;
import live.punkpanda.p2.xmpp.extensions.push.MessageBody;
import live.punkpanda.p2.xmpp.extensions.push.Notification;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.stanza.IQHandler;
import rocks.xmpp.core.stanza.model.IQ;
import rocks.xmpp.core.stanza.model.errors.Condition;
import rocks.xmpp.extensions.commands.model.Command;
import rocks.xmpp.extensions.data.model.DataForm;
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.PubSub;

import java.util.*;

import com.google.gson.Gson;

public class PushController {

    private static final String COMMAND_NODE_REGISTER_PREFIX = "register-push-";
    private static final String COMMAND_NODE_UNREGISTER_PREFIX = "unregister-push-";

    public static IQHandler commandHandler = (iq -> {
        final Command command = iq.getExtension(Command.class);
        if (command != null && command.getAction() == Command.Action.EXECUTE) {
            final String node = command.getNode();
            if (node != null && node.startsWith(COMMAND_NODE_REGISTER_PREFIX)) {
                return register(iq, command);
            } else if (node != null && node.startsWith(COMMAND_NODE_UNREGISTER_PREFIX)) {
                return unregister(iq, command);
            }
        }
        return iq.createError(Condition.BAD_REQUEST);
    });

    public static IQHandler pubsubHandler = (iq -> {
        final PubSub pubSub = iq.getExtension(PubSub.class);
        if (pubSub != null && iq.getType() == IQ.Type.SET) {

            TargetStore.getInstance().log("Step1", "enter");

            final PubSub.Publish publish = pubSub.getPublish();
            final String node = publish != null ? publish.getNode() : null;
            final Jid jid = iq.getFrom();
            final DataForm publishOptions = pubSub.getPublishOptions();
            final String secret = publishOptions != null ? publishOptions.findValue("secret") : null;
            final DataForm pushSummary = findPushSummary(publish);

            TargetStore.getInstance().log("Step2", jid.getLocal());

            if (node != null && secret != null) {
                TargetStore.getInstance().log("Step3", jid.getLocal());
                final Target target = TargetStore.getInstance().find(node);
                TargetStore.getInstance().log("Step4", jid.getLocal());
                if (target != null) {
                    TargetStore.getInstance().log("Step5", jid.getLocal());
                    if (secret.equals(target.getSecret())) {
                        TargetStore.getInstance().log("Step6", jid.getLocal());
                        final PushService pushService;
                        try {
                            TargetStore.getInstance().log("Step7", jid.getLocal());
                            pushService = PushServiceManager.getPushServiceInstance(target.getService());
                            TargetStore.getInstance().log("Step8", jid.getLocal());
                        } catch (IllegalStateException e) {
                            TargetStore.getInstance().log("Step9", jid.getLocal());
                            e.printStackTrace();
                            return iq.createError(Condition.INTERNAL_SERVER_ERROR);
                        }

                        TargetStore.getInstance().log("Step10", jid.getLocal());
                        if (pushSummary != null) {
                            TargetStore.getInstance().log("Step11", jid.getLocal());
                            if (target.getAccount() != jid.getLocal()) {
                                TargetStore.getInstance().log("Step12", jid.getLocal());
                                String messageSender = pushSummary.findValue("last-message-sender");
                                Jid messageSenderJid = Jid.ofEscaped(messageSender);
                                TargetStore.getInstance().log("Step13", jid.getLocal());
                                Gson gson = new Gson();
                                MessageBody messageBody = gson.fromJson(pushSummary.findValue("last-message-body"),
                                        MessageBody.class);

                                TargetStore.getInstance().log("Step14", jid.getLocal());
                                if (pushService.push(target, messageSenderJid.getLocal(), messageBody)) {
                                    TargetStore.getInstance().log("Step15", jid.getLocal());
                                    return iq.createResult();
                                } else {
                                    TargetStore.getInstance().log("Step16", jid.getLocal());
                                    return iq.createError(Condition.RECIPIENT_UNAVAILABLE);
                                }
                            } else {
                                TargetStore.getInstance().log("Step17", jid.getLocal());
                                return iq.createError(Condition.RECIPIENT_UNAVAILABLE);
                            }
                        } else {
                            TargetStore.getInstance().log("Step18", jid.getLocal());
                            // Group message
                            if (pushService.push(target, "", null)) {
                                TargetStore.getInstance().log("Step19", jid.getLocal());
                                return iq.createResult();
                            } else {
                                TargetStore.getInstance().log("Step20", jid.getLocal());
                                return iq.createError(Condition.RECIPIENT_UNAVAILABLE);
                            }
                        }
                    } else {
                        TargetStore.getInstance().log("Step21", jid.getLocal());
                        return iq.createError(Condition.FORBIDDEN);
                    }
                } else {
                    TargetStore.getInstance().log("Step22", jid.getLocal());
                    return iq.createError(Condition.ITEM_NOT_FOUND);
                }
            } else {
                TargetStore.getInstance().log("Step23", jid.getLocal());
                return iq.createError(Condition.FORBIDDEN);
            }
        }
        TargetStore.getInstance().log("Step24", "end");
        return iq.createError(Condition.BAD_REQUEST);
    });

    private static DataForm findPushSummary(final PubSub.Publish publish) {
        final Item item = publish == null ? null : publish.getItem();
        final Object payload = item == null ? null : item.getPayload();
        if (payload instanceof Notification) {
            return ((Notification) payload).getPushSummary();
        } else {
            return null;
        }
    }

    private static IQ register(final IQ iq, final Command command) {
        IQ res;
        String log = "";

        final Optional<DataForm> optionalData = command.getPayloads().stream().filter(p -> p instanceof DataForm)
                .map(p -> (DataForm) p).findFirst();
        final Jid from = iq.getFrom().asBareJid();
        log += "from=" + from.toEscapedString();
        if (optionalData.isPresent()) {
            final DataForm data = optionalData.get();
            final String deviceId = findDeviceId(data);
            final String token = data.findValue("token");

            if (isNullOrEmpty(token) || isNullOrEmpty(deviceId)) {
                return iq.createError(Condition.BAD_REQUEST);
            }

            final Service service;
            try {
                service = findService(COMMAND_NODE_REGISTER_PREFIX, command.getNode());
            } catch (IllegalArgumentException e) {
                return iq.createError(Condition.ITEM_NOT_FOUND);
            }

            Target target = TargetStore.getInstance().find(service, from.getLocal(), deviceId);

            log += ", deviceId=" + deviceId + ", token=" + token + ", target="
                    + ((target != null) ? target.toString() : "null");

            Boolean isEnabled = (target != null);
            if (target != null) {
                if (target.setToken(token)) {
                    log += ", another token";
                    if (!TargetStore.getInstance().update(target)) {
                        res = iq.createError(Condition.INTERNAL_SERVER_ERROR);
                    } else {
                        log += ", token updated";
                    }
                } else {
                    log += ", same token";
                }
            } else {
                target = Target.create(service, from, deviceId, token);
                TargetStore.getInstance().create(target);
                log += ", target created, target= " + target.toString();
            }

            final Command result = new Command(command.getNode(), String.valueOf(System.currentTimeMillis()),
                    Command.Status.COMPLETED, null, null,
                    Collections.singletonList(createRegistryResponseDataForm(isEnabled, target.getNode(), target.getSecret())));
            log += ", successfully end";
            res = iq.createResult(result);
        } else {
            log += ", Bad request";
            res = iq.createError(Condition.BAD_REQUEST);
        }

        TargetStore.getInstance().log("register", log);
        return res;
    }

    private static String findDeviceId(DataForm data) {
        final String deviceId = data.findValue("device-id");
        if (isNullOrEmpty(deviceId)) {
            return data.findValue("android-id");
        }
        return deviceId;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static Service findService(final String prefix, final String node) {
        if (isNullOrEmpty(node)) {
            throw new IllegalArgumentException("Command node can not be null or empty");
        }
        if (prefix.length() >= node.length()) {
            throw new IllegalArgumentException("Command node too short");
        }
        final String service = node.substring(prefix.length()).toUpperCase(Locale.US);
        try {
            return Service.valueOf(service);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(service + " is not a known push service");
        }
    }

    private static DataForm createRegistryResponseDataForm(Boolean isEnabled, String node, String secret) {
        List<DataForm.Field> fields = new ArrayList<>();
        fields.add(DataForm.Field.builder().var("enabeld").value(isEnabled).build());
        fields.add(DataForm.Field.builder().var("node").value(node).build());
        fields.add(DataForm.Field.builder().var("secret").value(secret).build());
        return new DataForm(DataForm.Type.FORM, fields);
    }

    private static IQ unregister(final IQ iq, final Command command) {
        final Optional<DataForm> optionalData = command.getPayloads().stream().filter(p -> p instanceof DataForm)
                .map(p -> (DataForm) p).findFirst();
        final Jid from = iq.getFrom().asBareJid();
        if (optionalData.isPresent()) {

            try {
                findService(COMMAND_NODE_UNREGISTER_PREFIX, command.getNode());
            } catch (IllegalArgumentException e) {
                return iq.createError(Condition.ITEM_NOT_FOUND);
            }

            final DataForm data = optionalData.get();
            final String deviceId = findDeviceId(data);
            if (isNullOrEmpty(deviceId)) {
                return iq.createError(Condition.BAD_REQUEST);
            }
            if (TargetStore.getInstance().delete(from.getLocal(), deviceId)) {
                final Command result = new Command(command.getNode(), Command.Action.COMPLETE);
                return iq.createResult(result);
            } else {
                return iq.createError(Condition.ITEM_NOT_FOUND);
            }
        } else {
            return iq.createError(Condition.BAD_REQUEST);
        }
    }

}
