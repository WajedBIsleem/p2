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

        TargetStore.getInstance().log("Step1", iq.getType().toString());

        final PubSub pubSub = iq.getExtension(PubSub.class);

        // TargetStore.getInstance().log("Step2", "pubSub : " + pubSub != null ? "true":
        // "false");

        if (pubSub != null && iq.getType() == IQ.Type.SET) {
            final PubSub.Publish publish = pubSub.getPublish();
            // TargetStore.getInstance().log("Step3", "publish : " + publish.getNode());

            final String node = publish != null ? publish.getNode() : null;
            // TargetStore.getInstance().log("Step4", "node : " + node);

            final Jid jid = iq.getFrom();
            // TargetStore.getInstance().log("Step5", "jid : " + jid.toEscapedString());

            final DataForm publishOptions = pubSub.getPublishOptions();
            // TargetStore.getInstance().log("Step6", "publishOptions : " + publishOptions
            // != null ? "true": "false");

            final String secret = publishOptions != null ? publishOptions.findValue("secret") : null;
            // TargetStore.getInstance().log("Step7", "secret : " + secret);

            final Item item = publish == null ? null : publish.getItem();
            // TargetStore.getInstance().log("Step8", "item : " + item != null ? "true":
            // "false");

            final Object payload = item == null ? null : item.getPayload();
            // TargetStore.getInstance().log("Step9", "payload : " + payload != null ?
            // "true": "false");

            if (payload instanceof Notification) {
                // TargetStore.getInstance().log("Step10", "payload is instanceof
                // Notification");

                DataForm pushSummary = ((Notification) payload).getPushSummary();
                // TargetStore.getInstance().log("Step11", "data : " + data != null ? "true":
                // "false");

                String messageSender = pushSummary.findValue("last-message-sender");
                String messageBody = pushSummary.findValue("last-message-body");

                TargetStore.getInstance().log("Step12", "messageSender : " + messageSender + ", messageBody" + messageBody);
            }
        }

        return iq.createResult();

        // final PubSub pubSub = iq.getExtension(PubSub.class);
        // if (pubSub != null && iq.getType() == IQ.Type.SET) {

        // TargetStore.getInstance().log("wajed", "pubsubHandler", "Step2");

        // final PubSub.Publish publish = pubSub.getPublish();
        // final String node = publish != null ? publish.getNode() : null;
        // final Jid jid = iq.getFrom();
        // final DataForm publishOptions = pubSub.getPublishOptions();
        // final String secret = publishOptions != null ?
        // publishOptions.findValue("secret") : null;
        // final DataForm pushSummary = findPushSummary(publish);

        // if (node != null && secret != null && jid.isBareJid()) {

        // final Target target = TargetStore.getInstance().find(node);

        // if (target != null) {
        // if (secret.equals(target.getSecret())) {
        // final PushService pushService;
        // try {
        // pushService = PushServiceManager.getPushServiceInstance(target.getService());
        // } catch (IllegalStateException e) {
        // TargetStore.getInstance().log("wajed", "isleem", "Step8");
        // e.printStackTrace();
        // return iq.createError(Condition.INTERNAL_SERVER_ERROR);
        // }

        // try {
        // if (target.getAccount() != jid.getLocal()) {

        // String messageSender = pushSummary.findValue("last-message-sender");
        // Jid messageSenderJid = Jid.ofEscaped(messageSender);

        // Gson gson = new Gson();
        // MessageBody messageBody =
        // gson.fromJson(pushSummary.findValue("last-message-body"),
        // MessageBody.class);

        // if (pushService.push(target, messageSenderJid.getLocal(), messageBody)) {
        // TargetStore.getInstance().log("wajed", "isleem", "Step7");
        // return iq.createResult();
        // } else {
        // TargetStore.getInstance().log("wajed", "isleem", "Step6");
        // return iq.createError(Condition.RECIPIENT_UNAVAILABLE);
        // }
        // } else {
        // TargetStore.getInstance().log("wajed", "isleem", "Step5");
        // return iq.createError(Condition.RECIPIENT_UNAVAILABLE);
        // }
        // } catch (Exception e) {
        // TargetStore.getInstance().log("wajed", "error", e.getMessage());
        // }
        // } else {
        // TargetStore.getInstance().log("wajed", "isleem", "Step4");
        // return iq.createError(Condition.FORBIDDEN);
        // }
        // } else {
        // return iq.createError(Condition.ITEM_NOT_FOUND);
        // }
        // } else {
        // TargetStore.getInstance().log("wajed", "isleem", "Step2");
        // return iq.createError(Condition.FORBIDDEN);
        // }
        // }
        // TargetStore.getInstance().log("wajed", "isleem", "Step1");
        // return iq.createError(Condition.BAD_REQUEST);
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
        final Optional<DataForm> optionalData = command.getPayloads().stream().filter(p -> p instanceof DataForm)
                .map(p -> (DataForm) p).findFirst();
        final Jid from = iq.getFrom().asBareJid();
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

            if (target != null) {
                if (target.setToken(token)) {
                    if (!TargetStore.getInstance().update(target)) {
                        return iq.createError(Condition.INTERNAL_SERVER_ERROR);
                    }
                }
            } else {
                target = Target.create(service, from, deviceId, token);
                TargetStore.getInstance().create(target);
            }

            final Command result = new Command(command.getNode(), String.valueOf(System.currentTimeMillis()),
                    Command.Status.COMPLETED, null, null,
                    Collections.singletonList(createRegistryResponseDataForm(target.getNode(), target.getSecret())));
            return iq.createResult(result);
        } else {
            return iq.createError(Condition.BAD_REQUEST);
        }
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

    private static DataForm createRegistryResponseDataForm(String node, String secret) {
        List<DataForm.Field> fields = new ArrayList<>();
        fields.add(DataForm.Field.builder().var("jid").value(Configuration.getInstance().getJid()).build());
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
