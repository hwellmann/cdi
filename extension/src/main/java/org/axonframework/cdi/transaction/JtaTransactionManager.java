/*
 * Copyright 2018 OPS4J Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.cdi.transaction;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.TransactionMethodExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
@Dependent
public class JtaTransactionManager implements TransactionManager {

    private static final Logger LOG = LoggerFactory.getLogger(JtaTransactionManager.class);

    @Inject
    private UserTransaction tx;

    @Override
    public Transaction startTransaction() {
        try {
            if (tx.getStatus() == Status.STATUS_NO_TRANSACTION) {
                LOG.trace("starting tx");
                tx.begin();
                return new JtaTransaction(tx, true);
            }
            else {
                return new JtaTransaction(tx, false);
            }
        }
        catch (SystemException | NotSupportedException exc) {
            throw new TransactionMethodExecutionException(exc.getMessage(), exc);
        }
    }

}
