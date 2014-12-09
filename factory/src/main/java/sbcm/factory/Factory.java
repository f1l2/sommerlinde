package sbcm.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbc.space.MozartSpaces;

public class Factory {

	private static final Logger logger = LoggerFactory.getLogger(Factory.class);

	public static void main(String[] args) {

		logger.info("Start space ...");

		MozartSpaces mozartSpaces = new MozartSpaces(true);
		mozartSpaces.init();

		logger.info("Space started.");
	}
}
