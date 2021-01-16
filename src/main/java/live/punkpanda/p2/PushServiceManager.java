package live.punkpanda.p2;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import live.punkpanda.p2.apns.ApnsPushService;
import live.punkpanda.p2.fcm.FcmPushService;
import live.punkpanda.p2.pushy.PushyPushService;
import live.punkpanda.p2.pojo.Service;

public class PushServiceManager {

    private static final ImmutableClassToInstanceMap<PushService> SERVICES;

    private static final ImmutableMap<Service, Class<? extends PushService>> SERVICE_TO_CLASS;

    static {
        SERVICES = ImmutableClassToInstanceMap.<PushService>builder()
                .put(FcmPushService.class, new FcmPushService())
                .put(ApnsPushService.class, new ApnsPushService())
                .put(PushyPushService.class, new PushyPushService())
                .build();

        SERVICE_TO_CLASS = ImmutableMap.of(Service.FCM, FcmPushService.class, Service.APNS, ApnsPushService.class, Service.PUSHY, PushyPushService.class);
    }

    public static PushService getPushServiceInstance(Service service, boolean isVoip) {
        
        if(isVoip){
            return new ApnsVoipPushService();
        }

        final Class<? extends PushService> clazz = SERVICE_TO_CLASS.get(service);
        if (clazz == null) {
            throw new IllegalStateException(String.format("No corresponding class found for service=%s", service));
        }
        final PushService pushService = SERVICES.getInstance(clazz);
        if (pushService == null) {
            throw new IllegalStateException(String.format("No instance found for %s", clazz.getName()));
        }
        return SERVICES.getInstance(clazz);
    }
}
