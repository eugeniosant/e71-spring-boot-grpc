/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.entando.en7;

//import com.agiletec.aps.system.SystemConstants;
import java.util.Arrays;
import org.entando.entando.ent.system.IEntandoApplicationContext;
import org.entando.entando.ent.system.remotebean.IRemoteBeanCatalogManager;
import org.entando.entando.ent.system.remotebean.annotation.ProxyAutowired;
import org.entando.entando.ent.system.remotebean.model.Endpoint;
import org.entando.entando.ent.system.remotebean.model.ExposedBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

/**
 * @author E.Santoboni
 */
public class MsApplicationContext extends AnnotationConfigServletWebServerApplicationContext implements IEntandoApplicationContext {
    
    private static final Logger logger = LoggerFactory.getLogger(MsApplicationContext.class);
    
    public static final String REMOTE_BEAN_CATALOG_MANAGER = "RemoteBeanCatalogManager";
    
    @Override
    public Object getBean(String beanName, ProxyAutowired.Strategy strategy) throws BeansException {
        try {
            IRemoteBeanCatalogManager rbcm = super.getBean(IRemoteBeanCatalogManager.class);
            Class requiredType = rbcm.getClassFromBeanName(beanName);
            return this.getBean(requiredType, strategy);
        } catch (BeansException e) {
            return e;
        } catch (Exception e) {
            throw new RuntimeException("Error extracting bean " + beanName, e);
        }
    }
    
    @Override
    public <T> T getBean(Class<T> requiredType, ExposedBean.Strategy strategy) {
        //ONLY_LOCAL, LOCAL_FIRST, ONLY_PROXY
        switch (strategy) {
            case ONLY_LOCAL:
                return this.getConcreteRegisteredBean(requiredType);
            case ONLY_PROXY:
                return this.getProxyBean(requiredType, true);
            default:
                T concrete = this.getConcreteRegisteredBean(requiredType);
                return this.getBean(requiredType, ProxyAutowired.Strategy.LOCAL_FIRST, concrete);
        }
    }
    
    @Override
    public <T> T getBean(Class<T> requiredType, ProxyAutowired.Strategy strategy) throws BeansException {
        if (strategy.equals(ProxyAutowired.Strategy.FORCE_PROXY)) {
            return this.getProxyBean(requiredType, true);
        }
        T concrete = this.getConcreteRegisteredBean(requiredType);
        return this.getBean(requiredType, strategy, concrete);
    }

    private <T> T getConcreteRegisteredBean(Class<T> requiredType) throws BeansException {
        String[] namesByType = super.getBeanNamesForType(requiredType);
        if (namesByType.length >= 1) {
            long countMaster = Arrays.asList(namesByType).stream().filter(t -> !t.startsWith(Endpoint.PROXY_BEAN_NAME_PREFIX)).count();
            if (countMaster > 1) {
                throw new NoUniqueBeanDefinitionException(requiredType, (int) countMaster, "There is more than one master bean for type " + requiredType.toString());
            }
            String name = Arrays.asList(namesByType).stream().filter(t -> !t.startsWith(Endpoint.PROXY_BEAN_NAME_PREFIX)).findFirst().orElse(null);
            if (null != name) {
                return super.getBean(name, requiredType);
            } else {
                return null;
            }
        }
        return null;
    }
	
    @Override
    public <T> T getBean(String name, Class<T> requiredType, ProxyAutowired.Strategy strategy) throws BeansException {
        if (strategy.equals(ProxyAutowired.Strategy.FORCE_PROXY)) {
            return this.getProxyBean(requiredType, true);
        }
        T concrete = (this.getBeanFactory().containsBean(name)) ? super.getBean(name, requiredType) : null;
        return this.getBean(requiredType, strategy, concrete);
    }
    
    private <T> T getBean(Class<T> requiredType, ProxyAutowired.Strategy strategy, T concreteLocalBean) throws BeansException {
        /*
        if (strategy.equals(ProxyAutowired.Strategy.FORCE_PROXY)) {
            return this.getProxyBean(requiredType, true);
        }
        */
        try {
            IRemoteBeanCatalogManager rbcm = super.getBean(IRemoteBeanCatalogManager.class);
            if (strategy.equals(ProxyAutowired.Strategy.FORCE_PROXY_IF_EXISTS)) {
                Endpoint endpoint = rbcm.getEndpoint(requiredType);
                if (null != endpoint) {
                    return rbcm.getProxy(requiredType, false);
                } else {
                    return concreteLocalBean;
                }
            } else /*if (strategy.equals(ProxyAutowired.Strategy.LOCAL_FIRST))*/ {
                if (null != concreteLocalBean) {
                    return concreteLocalBean;
                } else {
                    return rbcm.getProxy(requiredType, false);
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting bean of type " + requiredType.toString() + " - proxy bean strategy " + strategy, e);
            throw new FatalBeanException("Error extracting bean of type " + requiredType.toString() + " - proxy bean strategy " + strategy, e);
        }
    }
    
    /**
     * Build and return an instance of proxy bean. If the endpoint doesn't exist, return a mock bean
     * @param <T> The class of required proxy bean to return
     * @param requiredType The type of proxy bean
     * @param canBeMock If true, create a mock if the endpoint cannot exists
     * @return The required proxy bean
     * @throws BeansException In case of error
     */
    @Override
	public <T> T getProxyBean(Class<T> requiredType, boolean canBeMock) throws BeansException {
        IRemoteBeanCatalogManager remoteBeanCatalogManager = super.getBean(REMOTE_BEAN_CATALOG_MANAGER, IRemoteBeanCatalogManager.class);
        try {
            String proxyBeanName = Endpoint.getProxyBeanName(requiredType);
            if (this.getBeanFactory().containsBean(proxyBeanName)) {
                // already istanziated
                T remoteBean = super.getBean(proxyBeanName, requiredType);
                if (!canBeMock && remoteBean.toString().endsWith(Endpoint.MOCK_PROXY_BEAN_TO_STRING_SUFFIX)) {
                    return null;
                } else {
                    return remoteBean;
                }
            }
            T remoteBean = remoteBeanCatalogManager.getProxy(requiredType, canBeMock);
            if (!canBeMock && remoteBean.toString().endsWith(Endpoint.MOCK_PROXY_BEAN_TO_STRING_SUFFIX)) {
                return null;
            }
            this.getBeanFactory().registerSingleton(proxyBeanName, remoteBean);
            return remoteBean;
        } catch (BeansException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating proxy bean by class {}", requiredType, e);
            throw new RuntimeException("Error creating proxy bean by class " + requiredType, e);
        }
    }
    
}
