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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.axonframework.common.transaction.Transaction;
import org.axonframework.eventhandling.TransactionMethodExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
public class JtaTransaction implements Transaction {

    private static final Logger LOG = LoggerFactory.getLogger(JtaTransactionManager.class);

    private UserTransaction tx;
    private boolean topLevel;


    public JtaTransaction(UserTransaction tx, boolean topLevel) {
        this.tx = tx;
        this.topLevel = topLevel;
    }

    @Override
    public void commit() {
        try {
            if (topLevel) {
                tx.commit();
                LOG.trace("tx committed");
            }
        }
        catch (SecurityException | IllegalStateException | RollbackException
            | HeuristicMixedException | HeuristicRollbackException | SystemException exc) {
            LOG.error("could not commit tx", exc);
            throw new TransactionMethodExecutionException(exc.getMessage(), exc);
        }
    }

    @Override
    public void rollback() {
        try {
            if (topLevel) {
                tx.rollback();
                LOG.trace("tx rolled back");
            }
            else {
                tx.setRollbackOnly();
                LOG.trace("tx marked for rollback");
            }
        }
        catch (IllegalStateException | SecurityException | SystemException exc) {
            LOG.error("could not rollback tx", exc);
            throw new TransactionMethodExecutionException(exc.getMessage(), exc);
        }
    }
}
