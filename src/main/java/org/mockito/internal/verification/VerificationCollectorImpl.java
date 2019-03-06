/*
 * Copyright (c) 2016 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification;

import org.mockito.exceptions.base.MockitoAssertionError;

/**
 * Mockito implementation of VerificationCollector.
 */
public class VerificationCollectorImpl extends AbstractVerificationCollectorImpl {

    private StringBuilder builder;
    private int numberOfFailures;

    public VerificationCollectorImpl() {
        this.resetBuilder();
    }

    private void resetBuilder() {
        this.builder = new StringBuilder()
                .append("There were multiple verification failures:");
        this.numberOfFailures = 0;
    }

    private void append(String message) {
        this.numberOfFailures++;
        this.builder.append('\n')
                .append(this.numberOfFailures).append(". ")
                .append(message.trim()).append('\n');
    }

    @Override
    protected void doReport() throws MockitoAssertionError {
        if (this.numberOfFailures > 0) {
            String error = this.builder.toString();

            this.resetBuilder();

            throw new MockitoAssertionError(error);
        }
    }

    @Override
    public void collectError(Throwable error) {
        append(error.getMessage());
    }
}
