/*
 * 
 */

package varioush.batch.config;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

import com.jcraft.jsch.ChannelSftp.LsEntry;

// TODO: Auto-generated Javadoc
/**
 * The Class SFTPConfiguration.
 */
@Configuration
public class SFTPConfiguration {

    /** The log. */
    final Logger LOG = LoggerFactory.getLogger(SFTPConfiguration.class);

    /** The sftp host. */
    @Value("${sftp.host}")
    private String sftpHost;

    /** The sftp port. */
    @Value("${sftp.port:22}")
    private int sftpPort;

    /** The sftp user. */
    @Value("${sftp.user}")
    private String sftpUser;

    /** The sftp private key. */
    @Value("${sftp.privateKey:#{null}}")
    private Resource sftpPrivateKey;

    /** The sftp private key passphrase. */
    @Value("${sftp.privateKeyPassphrase:}")
    private String sftpPrivateKeyPassphrase;

    /** The sftp pasword. */
    @Value("${sftp.password:#{null}}")
    private String sftpPasword;

//	@Value("${sftp.remote.directory:/}")
//	private String sftpRemoteDirectory;

    /**
     * Sftp session factory.
     *
     * @return the session factory
     */
    @Bean
    public SessionFactory<LsEntry> sftpSessionFactory() {
        LOG.info("Initializing SFTP Session Factory for the " + "host:[{}] and port:[{}]", sftpHost, sftpPort);
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(sftpHost);
        factory.setPort(sftpPort);
        factory.setUser(sftpUser);
        if (sftpPrivateKey != null) {
            factory.setPrivateKey(sftpPrivateKey);
            factory.setPrivateKeyPassphrase(sftpPrivateKeyPassphrase);
        } else {
            factory.setPassword(sftpPasword);
        }
        factory.setAllowUnknownKeys(true);
        LOG.info("Finishing initialization of SFTP Session Factory" + " for the host:[{}] and port:[{}]", sftpHost,
                sftpPort);
        return new CachingSessionFactory<LsEntry>(factory);
    }

    /**
     * Handler.
     *
     * @return the message handler
     */
    @Bean
    @ServiceActivator(inputChannel = "toSftpChannel")
    public MessageHandler handler() {
        LOG.info("Initializing Handler for SFTP Channel");

        SftpMessageHandler sftp = new SftpMessageHandler(sftpSessionFactory());
        ExpressionParser parser = new SpelExpressionParser();

        Expression expression = parser.parseExpression("headers['path']");
        sftp.setRemoteDirectoryExpression(expression);

        sftp.setAutoCreateDirectory(true);
        sftp.setFileNameGenerator(message -> {

            if (message.getPayload() instanceof File) {
                return ((File) message.getPayload()).getName();
            } else {
                throw new IllegalArgumentException("File expected as payload!");
            }
        });

        LOG.info("Finishing initializing Handler for SFTP Channel");
        return sftp;
    }

    /**
     * The Interface UploadGateway.
     */
    @MessagingGateway
    public interface UploadGateway {

        /**
         * Upload.
         *
         * @param file    the file
         * @param dirName the dir name
         */
        @Gateway(requestChannel = "toSftpChannel")
        void upload(File file, @Header("path") String dirName);

    }

}
