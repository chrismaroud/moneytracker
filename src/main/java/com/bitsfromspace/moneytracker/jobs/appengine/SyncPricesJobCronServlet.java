package com.bitsfromspace.moneytracker.jobs.appengine;

import com.bitsfromspace.moneytracker.jobs.SyncPricesJob;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author chris
 * @since 02-11-15.
 */
@Singleton
public class SyncPricesJobCronServlet extends HttpServlet {

    private final SyncPricesJob job;

    @Inject
    public SyncPricesJobCronServlet(SyncPricesJob job) {
        this.job = job;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        job.run();
    }
}
