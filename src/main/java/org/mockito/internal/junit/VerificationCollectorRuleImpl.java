/*
 * Copyright (c) 2016 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.junit;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.internal.progress.MockingProgressImpl;
import org.mockito.internal.verification.VerificationCollectorImpl;
import org.mockito.verification.VerificationCollector;

/**
 * Mockito implementation of VerificationCollector.
 */
public class VerificationCollectorRuleImpl implements TestRule {
    
    private VerificationCollector collector;
    
    public VerificationCollectorRuleImpl(VerificationCollector collector) {
        this.collector = collector;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    collector.assertLazily();
                    try {
                        base.evaluate();
                    } catch (Throwable t) {
                        collector.collectError(t);
                    }
                }
                finally {
                    try {
                        collector.report();
                    } finally {
                        // If base.evaluate() throws an error, we must explicitly reset the VerificationStrategy
                        // to prevent subsequent tests to be assert lazily
                        mockingProgress().setVerificationStrategy(MockingProgressImpl.getDefaultVerificationStrategy());
                    }
                }
            }
        };
    }
}
