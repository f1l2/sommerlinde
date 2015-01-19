package sbcm.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbc.space.AlterSpaceServer;
import sbc.space.SpaceTech;

public class Factory {

	private static final Logger logger = LoggerFactory.getLogger(Factory.class);

	public static void main(String[] args) {

		logger.info("Start space ...");

		SpaceTech mozartSpaces = new AlterSpaceServer(0);
		mozartSpaces.init();

		logger.info("Space started.");

	}
}