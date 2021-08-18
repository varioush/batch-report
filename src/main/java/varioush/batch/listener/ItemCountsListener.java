package varioush.batch.listener;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

import varioush.batch.constant.Constants;

public class ItemCountsListener implements ItemProcessListener<Object, Object> {

	@SuppressWarnings(Constants.OTHER.UNUSED)
	private static final Logger logger = LoggerFactory.getLogger(ItemCountsListener.class);

	
	private static final AtomicLong count = new AtomicLong(1);

	public void afterProcess(Object item, Object result) {
		count.getAndIncrement();
	}

	public void beforeProcess(Object item) {
	}

	public void onProcessError(Object item, Exception e) {
	}
}