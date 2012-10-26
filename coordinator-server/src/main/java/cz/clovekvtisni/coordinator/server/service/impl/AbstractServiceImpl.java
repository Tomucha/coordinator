package cz.clovekvtisni.coordinator.server.service.impl;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyOpts;
import com.googlecode.objectify.ObjectifyService;
import cz.clovekvtisni.coordinator.server.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ConcurrentModificationException;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:25 PM
 */
public class AbstractServiceImpl implements Service {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private ObjectifyFactory objectifyFactory;

    @Autowired
    public void setObjectifyFactory(ObjectifyFactory objectifyFactory) {
        this.objectifyFactory = objectifyFactory;
    }

    protected Objectify noTransactionalObjectify() {
        return objectifyFactory.begin();
    }

    protected <T> T transactionWithResult(final String transactionName, TransactionWithResultCallback<T> withResultCallback) {
        logger.debug("Running transaction '{}'", transactionName);
        for (int i = 5; i > 0; i--) {
            try {
                /*
                    An XG transaction that touches only a single entity group has exactly the same performance and cost as a single-group,
                    non-XG transaction. In an XG transaction that touches multiple entity groups,
                    operations cost the same as if they were performed in a non-XG transaction, but may experience higher latency.
                */
                TransactionOptions to = TransactionOptions.Builder.withXG(true);
                ObjectifyOpts opts = new ObjectifyOpts().setTransactionOptions(to);
                opts.setBeginTransaction(true);
                final Objectify ofy = ObjectifyService.begin(opts);
                try {
                    if (!ofy.getTxn().isActive()) throw new IllegalStateException("Internal ofy error");
                    final T toReturn = withResultCallback.runInTransaction(ofy);
                    ofy.getTxn().commit();
                    return toReturn;
                } finally {
                    if (ofy.getTxn().isActive()) {
                        logger.warn("Rollback in '{}'", transactionName);
                        ofy.getTxn().rollback();
                    }
                }
            } catch (DatastoreTimeoutException e) {
                processException(e, transactionName, i);
            } catch (TransientFailureException e) {
                processException(e, transactionName, i);
            } catch (ConcurrentModificationException e) {
                processException(e, transactionName, i);
            }
        }
        throw new IllegalStateException("this shouldn't happened");
    }

    private void processException(RuntimeException ex, String transactionName, int remainCounter) {
        logger.warn("ConcurrentModificationException running '{}', round={}", transactionName, remainCounter);
        if (remainCounter <= 1) throw ex;
        delayBeforeNextRetry();
    }

    private void delayBeforeNextRetry() {
        try {
            // chvili spime natuty a pak se poradne rozstrelime
            Thread.sleep(500 + (int) (Math.random() * 1000));
        } catch (InterruptedException e1) {
            //ok
        }
    }

    protected void transaction(boolean crossGroup, String transactionName, final TransactionCallback callback) {
        transactionWithResult(transactionName, new TransactionWithResultCallback<Void>() {
            @Override
            public Void runInTransaction(Objectify ofy) {
                callback.runInTransaction(ofy);
                return null;
            }
        });
    }

    protected interface TransactionWithResultCallback<T> {
        T runInTransaction(Objectify ofy);
    }

    protected interface TransactionCallback {
        void runInTransaction(Objectify ofy);
    }

    protected void assertCrossGroupTransaction(Objectify ofy) {
   		if (ofy.getTxn() == null) throw new IllegalStateException("No transaction");
   		if (!ofy.getTxn().isActive()) throw new IllegalStateException("Transaction is not active!");
           // TODO PHASE2: nevim jak overit, ze je cross group. Az na to prijdu, tak poradne otestovat, natuty to na tom assertu zacne padat.
   	}

}
