package com.bitsfromspace.moneytracker;

import com.bitsfromspace.moneytracker.jobs.appengine.SyncPricesJobCronServlet;
import com.bitsfromspace.moneytracker.model.Dao;
import com.bitsfromspace.moneytracker.model.appengine.AppEngineDao;
import com.bitsfromspace.moneytracker.services.BloombergQuoteService;
import com.bitsfromspace.moneytracker.services.CurrencyRateProvider;
import com.bitsfromspace.moneytracker.services.QuoteService;
import com.bitsfromspace.moneytracker.services.YahooCurrencyService;
import com.bitsfromspace.moneytracker.utils.TimeProvider;
import com.bitsfromspace.moneytracker.utils.TimeProviderImpl;
import com.bitsfromspace.moneytracker.web.rest.UserProvider;
import com.bitsfromspace.moneytracker.web.rest.appengine.GoogleUserProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author chris
 * @since 13-10-15.
 */
public class MoneyTrackerConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                //requestStaticInjection(MoneyTrackerConfig.class);
                Properties configProperties = new Properties();
                try (InputStream in = getClass().getResourceAsStream("/application.properties")) {
                    configProperties.load(in);
                } catch (IOException ioex) {
                    throw new IllegalStateException("Unable to load application.properties", ioex);
                }
                Names.bindProperties(binder(), configProperties);

                bind(QuoteService.class).to(BloombergQuoteService.class);
                bind(Dao.class).to(AppEngineDao.class);
                bind(TimeProvider.class).to(TimeProviderImpl.class);
                bind(UserProvider.class).to(GoogleUserProvider.class);
                bind(CurrencyRateProvider.class).to(YahooCurrencyService.class);

                serve("/syncPrices").with(SyncPricesJobCronServlet.class);

//                serve(configProperties.getProperty("queueTask.pollStation.url")).with(PollStationServlet.class);
//                serve(configProperties.getProperty("cronJob.pollAllStations.url")).with(PollAllStationsServlet.class);
//                filter("/*").through(new ObjectifyFilter());
            }
        });
    }
}
