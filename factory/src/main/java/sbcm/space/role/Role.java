package sbcm.space.role;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbc.space.AlterSpaceClient;
import sbc.space.MozartSpaces;
import sbc.space.SpaceTech;

public abstract class Role {

	protected static final Logger logger = LoggerFactory.getLogger(Role.class);

	protected int employeeId;

	protected SpaceTech mozartSpaces;

	public Role() {

		this.mozartSpaces = new AlterSpaceClient();
		// first receive role id
		this.employeeId = mozartSpaces.getIDAndIncr(MozartSpaces.PRODUCER_COUNTER);

		logger.info(this.employeeId + " started.");

		this.doAction();
	}

	protected abstract void doAction();

	protected int workRandomTime() {

		int min = 1000, max = 3000;
		Random random = new Random();

		return (random.nextInt(max - min) + min);
	}

	protected boolean isDefectRandom(int probability) {

		Random random = new Random();
		int number = random.nextInt(100);

		if (number < probability)
			return true;
		else
			return false;
	}

	protected int workRandomValue() {

		Random random = new Random();
		return random.nextInt(40);
	}
}
