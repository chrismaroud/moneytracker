package com.bitsfromspace.moneytracker.model.appengine;

        import com.bitsfromspace.moneytracker.model.Dao;
        import com.bitsfromspace.moneytracker.model.DaoTest;
        import com.bitsfromspace.moneytracker.utils.TimeProvider;
        import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
        import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
        import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
        import org.junit.After;
        import org.junit.Before;

/**
 * @author chris
 * @since 28-10-15.
 */
public class AppEngineDaoTest extends DaoTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                    new LocalMemcacheServiceTestConfig());

    @Before
    public void setup(){
        helper.setUp();
        super.setup();
    }
    @After
    public void shutdown(){
        helper.tearDown();
    }

    @Override
    protected Dao getDao(TimeProvider timeProvider) {
        return new AppEngineDao();
    }
}
