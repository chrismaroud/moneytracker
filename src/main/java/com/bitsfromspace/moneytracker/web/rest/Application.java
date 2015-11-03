package com.bitsfromspace.moneytracker.web.rest;

import com.bitsfromspace.moneytracker.model.Dao;
import com.bitsfromspace.moneytracker.model.appengine.AppEngineDao;
import com.bitsfromspace.moneytracker.utils.TimeProvider;
import com.bitsfromspace.moneytracker.utils.TimeProviderImpl;
import com.bitsfromspace.moneytracker.web.rest.appengine.GoogleUserProvider;
import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;

/**
 * @author chris
 * @since 29-10-15.
 */
@ApplicationPath("rest")
public class Application extends ResourceConfig {

    @Inject
    public Application(ServiceLocator serviceLocator, ServletContext servletContext){
        packages(true, "com.bitsfromspace.moneytracker.web.rest");
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector((Injector) servletContext.getAttribute(Injector.class.getName()));

    }

    public static class Binder extends AbstractBinder{
        @Override
        protected void configure() {
            bind(TimeProviderImpl.class).to(TimeProvider.class);
//            bind(new AppEngineDao(new TimeProviderImpl())).to(Dao.class);
            bind(AppEngineDao.class).to(Dao.class);
            bind(GoogleUserProvider.class).to(UserProvider.class);
              bind(TimeProvider.class).to(TimeProviderImpl.class);
              bind(UserProvider.class).to(GoogleUserProvider.class);
//            bind(Dao.class).to(AppEngineDao.class);
//            bind(OverviewService.class);


        }
    }
}
