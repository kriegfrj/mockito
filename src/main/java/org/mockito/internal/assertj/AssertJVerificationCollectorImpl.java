/*
 * Copyright (c) 2016 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.assertj;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.assertj.core.api.AbstractSoftAssertions;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.progress.MockingProgressImpl;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationCollector;
import org.mockito.verification.VerificationMode;
import org.mockito.verification.VerificationStrategy;

/**
 * Mockito implementation of VerificationCollector.
 */
public class AssertJVerificationCollectorImpl implements VerificationCollector {

    AbstractSoftAssertions sa;

    Consumer<Throwable> collectError;
    
    public AssertJVerificationCollectorImpl(AbstractSoftAssertions sa) {
        this.sa = sa;
        
        try {
            Field f = AbstractSoftAssertions.class.getDeclaredField("proxies");
            f.setAccessible(true);
            final Object softProxies = f.get(sa);
            Class<?> sp = Class.forName("org.assertj.core.api.SoftProxies");
            
            final Method m = sp.getDeclaredMethod("collectError",  Throwable.class);
            m.setAccessible(true);
            collectError = new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) {
                    try {
                        m.invoke(softProxies, t);
                    } catch (IllegalAccessException e) {
                    } catch (IllegalArgumentException e) {
                    } catch (InvocationTargetException e) {
                    }
                }
            };
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Couldn't load AssertJ SoftProxies class. Make sure that AssertJ is on the classpath.");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Couldn't find the AssertJ SoftProxies.collectError() method. Make sure you're using an up-to-date version of AssertJ.", e);
        } catch (SecurityException e) {
            throw new IllegalStateException("Couldn't access the AssertJ SoftProxies.collectError() method.", e);
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Couldn't access the AssertJ AbstractSoftAssertions.softProxies field.", e);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Couldn't find the AssertJ AbstractSoftAssertions.softProxies field.", e);
        } 
    }

    @Override
    public void report() throws MockitoAssertionError {
        sa.errorsCollected();
    }

    @Override
    public VerificationCollector assertLazily() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void collectError(Throwable t) {
        collectError.accept(t);
    }

}
