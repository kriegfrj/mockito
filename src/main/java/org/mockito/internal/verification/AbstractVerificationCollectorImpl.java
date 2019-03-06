/*
 * Copyright (c) 2016 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.progress.MockingProgressImpl;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationCollector;
import org.mockito.verification.VerificationMode;
import org.mockito.verification.VerificationStrategy;

/**
 * Mockito implementation of VerificationCollector.
 */
public abstract class AbstractVerificationCollectorImpl implements VerificationCollector {
    public void report() throws MockitoAssertionError {
        mockingProgress().setVerificationStrategy(MockingProgressImpl.getDefaultVerificationStrategy());

        doReport();
    }

    protected abstract void doReport() throws MockitoAssertionError;
    
    public VerificationCollector assertLazily() {
        mockingProgress().setVerificationStrategy(new VerificationStrategy() {
            public VerificationMode maybeVerifyLazily(VerificationMode mode) {
                return new VerificationWrapper(mode);
            }
        });
        return this;
    }

    private class VerificationWrapper implements VerificationMode {

        private final VerificationMode delegate;

        private VerificationWrapper(VerificationMode delegate) {
            this.delegate = delegate;
        }

        public void verify(VerificationData data) {
            try {
                this.delegate.verify(data);
            } catch (AssertionError error) {
                collectError(error);
            }
        }

        public VerificationMode description(String description) {
            throw new IllegalStateException("Should not fail in this mode");
        }
    }

}
