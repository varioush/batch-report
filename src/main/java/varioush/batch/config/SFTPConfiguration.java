package varioush.batch.config;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
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

@Configuration
public class SFTPConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(SFTPConfiguration.class);

	@Value("${sftp.host}")
	private String sftpHost;

	@Value("${sftp.port:22}")
	private int sftpPort;

	@Value("${sftp.user}")
	private String sftpUser;

	@Value("${sftp.privateKey:#{null}}")
	private Resource sftpPrivateKey;

	@Value("${sftp.privateKeyPassphrase:}")
	private String sftpPrivateKeyPassphrase;

	@Value("${sftp.password:#{null}}")
	private String sftpPasword;

	@Value("${sftp.remote.directory:/}")
	private String sftpRemoteDirectory;

	@Bean
	public SessionFactory<LsEntry> sftpSessionFactory() {
		logger.info("Initializing SFTP Session Factory for the host:[{}] and port:[{}]", sftpHost, sftpPort);
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
		logger.info("Finishing initialization of SFTP Session Factory for the host:[{}] and port:[{}]", sftpHost,
				sftpPort);
		return new CachingSessionFactory<LsEntry>(factory);
	}

	@Bean
	@ServiceActivator(inputChannel = "toSftpChannel")
	public MessageHandler handler() {
		logger.info("Initializing Handler for SFTP Channel");

		SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());
		ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

		handler.setRemoteDirectoryExpression(EXPRESSION_PARSER.parseExpression("headers['path']"));

		handler.setAutoCreateDirectory(true);
		handler.setFileNameGenerator(message -> {

			if (message.getPayload() instanceof File) {
				return (((File) message.getPayload()).getName());
			} else {
				throw new IllegalArgumentException("File expected as payload!");
			}
		});

		logger.info("Finishing initializing Handler for SFTP Channel");
		return handler;
	}

	
	@MessagingGateway
	public interface UploadGateway {

		@Gateway(requestChannel = "toSftpChannel")
		void upload(File file, @Header("path") String dirName);

	}

}
