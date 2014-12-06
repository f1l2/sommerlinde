package sbcm.factory;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Factory {

	private static final Logger logger = LoggerFactory.getLogger(Factory.class);

	public static void main(String[] args) {

		logger.info("Start space ...");

		FactoryCore.initSpace(Boolean.TRUE);

		logger.info("Space started.");

		// RUN
		Date time = new Date();
		while ((time.getTime() + 460 * 1000) > new Date().getTime()) {

			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		FactoryCore.stopSpace();

		logger.info("Space stopped.");

	}
}
