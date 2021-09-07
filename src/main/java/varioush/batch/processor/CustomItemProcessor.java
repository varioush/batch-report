/*
 * 
 */

package varioush.batch.processor;

import java.util.Map;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import varioush.batch.utils.Functions;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomItemProcessor.
 */
@Component
@StepScope
public class CustomItemProcessor implements ItemProcessor<Map<String, Object>, String> {

    /** The subject. */
    @Value(Functions.JOB_PARAM_SUBJECT)
    private String subject;

    /** The functions. */
    @Autowired
    private Functions functions;

    /**
     * Process.
     *
     * @param item the item
     * @return the string
     * @throws Exception the exception
     */
    @Override
    public String process(final Map<String, Object> item) throws Exception {
        // no logger used to avoid over-logging

        String prefix = functions.get(subject, Functions.PREFIX);
        String postfix = functions.get(subject, Functions.POSTFIX);

        String content = Functions.BLANK;

        String delimterExpression = functions.get(subject, Functions.DELIMITER);

        String delimiter = functions.get(delimterExpression);

        if (prefix != null) {
            content = content.concat(prefix);
        }

        String[] columnArray = functions.getSubject(subject).getColumnArray();

        for (String column : columnArray) {

            Object obj = item.get(column);

            String value = Functions.BLANK;
            if (obj != null) {
                value = obj.toString();
            }
            content = content.concat(delimiter).concat(value);

        }

        if (postfix != null) {
            content = content.concat(delimiter).concat(postfix);
        }

        return content;
    }

}
