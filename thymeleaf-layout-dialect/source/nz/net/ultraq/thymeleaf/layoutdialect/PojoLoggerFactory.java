package nz.net.ultraq.thymeleaf.layoutdialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class PojoLoggerFactory {
	public Logger getLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}
}
