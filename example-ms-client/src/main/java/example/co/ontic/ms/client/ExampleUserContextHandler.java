package example.co.ontic.ms.client;

import co.ontic.ms.core.UserContextHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author rajesh
 * @since 19/01/25 16:18
 */
public class ExampleUserContextHandler implements UserContextHandler {
    public static class UserContext {
        private static final ThreadLocal<UserContext> contextHolder = new ThreadLocal<>();
        private static final UserContext ROOT = new UserContext() {
            @Override
            public void start() {
            }

            @Override
            public void stop() {
            }

            @Override
            public <T> T getContextVariable(String name, T defaultValue) {
                return defaultValue;
            }

            @Override
            public <T> void setContextVariable(String name, T value) {
            }
        };
        private final UserContext parent;
        private final Map<String, Object> contextVariables = new HashMap<>();

        public UserContext() {
            UserContext previous = contextHolder.get();
            if (previous == null) {
                parent = ROOT;
            } else {
                parent = previous;
            }
        }

        public void start() {
            contextHolder.set(this);
        }

        public void stop() {
            if (parent != ROOT) {
                parent.start();
            } else {
                contextHolder.remove();
            }
        }

        public <T> T getContextVariable(String name, T defaultValue) {
            //noinspection unchecked
            return (T) contextVariables.getOrDefault(name, defaultValue);
        }

        public <T> void setContextVariable(String name, T value) {
            contextVariables.put(name, value);
        }

        public static UserContext current() {
            UserContext current = contextHolder.get();
            if (current == null) {
                return ROOT;
            }
            return current;
        }

        public static UserContext startNew() {
            UserContext userContext = new UserContext();
            userContext.start();
            return userContext;
        }
    }

    @Override
    public byte[] userContext() {
        int userId = UserContext.current().getContextVariable("USER", 1000000);

        return new byte[]{(byte) (userId >> 24), (byte) (userId >> 16), (byte) (userId >> 8), (byte) (userId)};

    }

    @Override
    public <T> T executeInContext(byte[] userContext, Supplier<T> callable) {
        int userId = ((userContext[0] & 0xFF) << 24) | ((userContext[1] & 0xFF) << 16) | ((userContext[2] & 0xFF) << 8) | (userContext[3] & 0xFF);

        UserContext context = UserContext.startNew();
        context.setContextVariable("USER", userId);
        try {
            return callable.get();
        } finally {
            context.stop();
        }
    }
}
