package com.bitsfromspace.moneytracker.utils;

import com.bitsfromspace.moneytracker.model.Currency;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author chris
 * @since 04-11-15.
 */
public class CacheTest {

    private TimeProvider timeProviderMock;
    private Cache.Delegate<String, String> delegateMock;
    private Cache<String, String> cache;
    private long retentionMillis = TimeUnit.HOURS.toMillis(1);

    @Before
    public void setup(){
        timeProviderMock = mock(TimeProvider.class);
        when(timeProviderMock.getTime()).thenReturn(System.currentTimeMillis());
        //noinspection unchecked
        delegateMock = mock(Cache.Delegate.class);
        cache = new Cache<>(timeProviderMock, retentionMillis, delegateMock);
    }

    @Test
    public void testExceptionsArePropogated(){
        when(delegateMock.get("key")).thenThrow(new RuntimeException("blast"));

        try{
            cache.get("key");
            fail();
        } catch (RuntimeException re){
            assertEquals("blast", re.getMessage());
        }
    }


    @Test
    public void testDelegate(){
        when(delegateMock.get("keyA")).thenReturn("valueA");
        when(delegateMock.get("keyB")).thenReturn("valueB");
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueB", cache.get("keyB"));
        verify(delegateMock, times(2)).get(anyString());
        verifyNoMoreInteractions(delegateMock);
    }

    @Test
    public void testCached(){
        when(delegateMock.get("keyA")).thenReturn("valueA");

        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));

        verify(delegateMock, times(1)).get(anyString());
        verifyNoMoreInteractions(delegateMock);

    }
    @Test
    public void testNullIsCached(){
        when(delegateMock.get("keyA")).thenReturn(null);

        assertNull(cache.get("keyA"));
        assertNull(cache.get("keyA"));
        assertNull(cache.get("keyA"));
        assertNull(cache.get("keyA"));
        assertNull(cache.get("keyA"));
        assertNull(cache.get("keyA"));

        verify(delegateMock, times(1)).get(anyString());
        verifyNoMoreInteractions(delegateMock);
    }
    @Test
    public void testExpiration(){
        reset(timeProviderMock);
        when(timeProviderMock.getTime()).thenReturn(TimeUnit.DAYS.toMillis(17_000));
        when(delegateMock.get("keyA")).thenReturn("valueA");
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
         verify(delegateMock, times(1)).get(anyString());
        verifyNoMoreInteractions(delegateMock);

        reset(delegateMock, timeProviderMock);
        when(timeProviderMock.getTime()).thenReturn(TimeUnit.DAYS.toMillis(17_000) + retentionMillis - 1);
        when(delegateMock.get("keyA")).thenReturn("valueA");
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        verify(delegateMock, times(0)).get(anyString());
        verifyNoMoreInteractions(delegateMock);

        reset(delegateMock, timeProviderMock);
        when(timeProviderMock.getTime()).thenReturn(TimeUnit.DAYS.toMillis(17_000) + retentionMillis + 1);
        when(delegateMock.get("keyA")).thenReturn("valueA");
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        assertEquals("valueA", cache.get("keyA"));
        verify(delegateMock, times(1)).get(anyString());
        verifyNoMoreInteractions(delegateMock);

    }

}