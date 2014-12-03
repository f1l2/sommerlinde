package at.tuwien.sbcm.supplier;

import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.sbcm.factory.FactoryCore;
import at.tuwien.sbcm.factory.model.EffectiveLoad;
import at.tuwien.sbcm.factory.model.Igniter;
import at.tuwien.sbcm.factory.model.Propellant;
import at.tuwien.sbcm.factory.model.WoodenStaff;

public class Supplier {

	private static final Logger logger = LoggerFactory.getLogger(Supplier.class);

	private int producerId;

	private int propDefect = 10;

	public static void main(String[] args) {

		new Supplier();

		System.exit(0);
	}

	public Supplier() {

		FactoryCore.initSpace(Boolean.FALSE);

		// first receive producer id
		this.producerId = FactoryCore.getIDAndIncr(FactoryCore.PRODUCER_COUNTER);

		logger.info(this.producerId + " started.");

		this.supply();
	}

	public void supply() {

		for (int i = 0; i <= 3; i++) {

			Igniter igniter = new Igniter();
			igniter.setId(FactoryCore.getIDAndIncr(FactoryCore.PART_COUNTER));

			try {
				FactoryCore.write(FactoryCore.PARTS, new Entry(igniter));
			} catch (MzsCoreException e) {
				e.printStackTrace();
			}

		}

		for (int i = 0; i <= 1; i++) {

			Propellant propellant = new Propellant();
			propellant.setId(FactoryCore.getIDAndIncr(FactoryCore.PART_COUNTER));
			propellant.setAmount(500);

			try {
				FactoryCore.write(FactoryCore.PARTS, new Entry(propellant));
			} catch (MzsCoreException e) {
				e.printStackTrace();
			}

		}

		for (int i = 0; i <= 3; i++) {

			WoodenStaff woodenStaff = new WoodenStaff();
			woodenStaff.setId(FactoryCore.getIDAndIncr(FactoryCore.PART_COUNTER));

			try {
				FactoryCore.write(FactoryCore.PARTS, new Entry(woodenStaff));
			} catch (MzsCoreException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i <= 3; i++) {

			EffectiveLoad effectiveLoad = new EffectiveLoad();
			effectiveLoad.setId(FactoryCore.getIDAndIncr(FactoryCore.PART_COUNTER));
			effectiveLoad.setIsDefect(FactoryCore.isDefectRandom(this.propDefect));

			try {
				FactoryCore.write(FactoryCore.PARTS, new Entry(effectiveLoad));
			} catch (MzsCoreException e) {
				e.printStackTrace();
			}
		}

	}

}
