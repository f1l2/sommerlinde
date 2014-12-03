package at.tuwien.sbcm.producer;

import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.sbcm.factory.FactoryCore;
import at.tuwien.sbcm.factory.model.Rocket;

public class Producer {

	private static final Logger logger = LoggerFactory.getLogger(Producer.class);

	private int producerId;

	public static void main(String[] args) {

		new Producer();
	}

	public Producer() {

		FactoryCore.initSpace(Boolean.FALSE);

		// first receive producer id
		this.producerId = FactoryCore.getIDAndIncr(FactoryCore.PRODUCER_COUNTER);

		this.produce();
	}

	public void produce() {

		Rocket rocket1 = new Rocket();
		rocket1.setId(FactoryCore.getIDAndIncr(FactoryCore.ROCKET_COUNTER));
		rocket1.setProducerId(this.producerId);

		try {
			FactoryCore.write(FactoryCore.ROCKET, new Entry(rocket1));

			logger.info(this.producerId + " produces a rock.");

		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Rocket rocket2 = new Rocket();
		rocket2.setId(FactoryCore.getIDAndIncr(FactoryCore.ROCKET_COUNTER));
		rocket2.setProducerId(this.producerId);

		try {
			FactoryCore.write(FactoryCore.ROCKET, new Entry(rocket2));

			logger.info( this.producerId + " produces a rock.");
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
