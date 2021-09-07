/*
 * 
 */

package varioush.batch.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import varioush.batch.utils.Functions;

/**
 * The Class CustomItemWriter.
 */

@Component
@StepScope
public class CustomItemWriter implements ItemWriter<String> {

    /** The Constant LOG. */

    private static final Logger LOG = LoggerFactory.getLogger(CustomItemWriter.class);

    /** The filename. */

    @Value(Functions.JOB_PARAM_FILENAME)
    private String filename;

    /**
     * Write.
     *
     * @param items the items
     * @throws Exception the exception
     */
    @Override
    public void write(final List<? extends String> items) throws Exception {

        LOG.info("Writing is in progress!, filename:{}", filename);

        if (items != null) {

            String content = Functions.BLANK;

            for (Object item : items) {
                content = content.concat(Functions.NEW_LINE);
                content = content.concat(item.toString());
            }

            Functions.write(filename, content);

        }

        LOG.info("Writing Finished, filename:{}", filename);
    }

}
