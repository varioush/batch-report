/*
 * 
 */

package varioush.batch.listener;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.batch.core.ItemProcessListener;

/**
 * The listener interface for receiving itemCounts events. The class that is
 * interested in processing a itemCounts event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's addItemCountsListener method. When the itemCounts event occurs,
 * that object's appropriate method is invoked.
 *
 * 
 */
public final class ItemCountsListener implements ItemProcessListener<Object, Object> {

    /** The Constant COUNT. */
    private static final AtomicLong COUNT = new AtomicLong(1);

    /**
     * After process.
     *
     * @param item   the item
     * @param result the result
     */
    public void afterProcess(final Object item, final Object result) {
        COUNT.getAndIncrement();
    }

    /**
     * Before process.
     *
     * @param item the item
     */
    public void beforeProcess(final Object item) {
    }

    /**
     * On process error.
     *
     * @param item the item
     * @param e    the e
     */
    public void onProcessError(final Object item, final Exception e) {
    }
}
