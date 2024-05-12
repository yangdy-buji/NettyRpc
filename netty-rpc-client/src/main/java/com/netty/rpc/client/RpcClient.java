package com.netty.rpc.client;

import com.netty.rpc.annotation.RpcAutowired;
import com.netty.rpc.client.connect.ConnectionManager;
import com.netty.rpc.client.discovery.ServiceDiscovery;
import com.netty.rpc.client.proxy.ObjectProxy;
import com.netty.rpc.client.proxy.RpcService;
import com.netty.rpc.util.ServiceUtil;
import com.netty.rpc.util.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * RPC Client（Create RPC proxy）
 *
 * @author luxiaoxun
 * @author g-yu
 */
public class RpcClient implements ApplicationContextAware, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ServiceDiscovery serviceDiscovery;

    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtil.createThreadPool(RpcClient.class.getSimpleName(), 8, 16);

    private static Map<String, ObjectProxy> proxies = new ConcurrentHashMap<>();

    private static Map<String, Object> services = new ConcurrentHashMap<>();

    public RpcClient(String address) {
        this.serviceDiscovery = new ServiceDiscovery(address);
        threadPoolExecutor.submit(() -> this.serviceDiscovery.discoveryService());
    }

    @SuppressWarnings("unchecked")
    public static <T> T createService(Class<T> interfaceClass, String version) {
        String key = ServiceUtil.makeServiceKey(interfaceClass.getName(), version);

        return (T) services.computeIfAbsent(key, k -> Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                proxies.computeIfAbsent(key, p -> new ObjectProxy(interfaceClass, version)))
        );
    }
    public static RpcService createAsyncService(Class interfaceClass) {
        return createAsyncService(interfaceClass,ServiceUtil.DEFAULT_VERSION);
    }
    public static RpcService createAsyncService(Class interfaceClass, String version) {
        String key = ServiceUtil.makeServiceKey(interfaceClass.getName(), version);
        return proxies.computeIfAbsent(key, k -> new ObjectProxy(interfaceClass, version));
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public void stop() {
        threadPoolExecutor.shutdown();
        serviceDiscovery.stop();
        ConnectionManager.getInstance().stop();
    }

    @Override
    public void destroy() {
        this.stop();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Field[] fields = bean.getClass().getDeclaredFields();
            try {
                for (Field field : fields) {
                    RpcAutowired rpcAutowired = field.getAnnotation(RpcAutowired.class);
                    if (rpcAutowired != null) {
                        String version = rpcAutowired.version();
                        field.setAccessible(true);
                        field.set(bean, createService(field.getType(), version));
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error(e.toString());
            }
        }
    }
}

