package sbcm.logistician;

import java.util.ArrayList;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;

import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.Employee;
import sbcm.factory.model.Rocket;
import sbcm.factory.model.RocketPackage;
import sbcm.space.role.Role;

public class Logistician extends Role {

	public static void main(String[] args) {
		new Logistician();

		System.exit(0);
	}

	public Logistician() {
		super();
	}

	@Override
	protected void doAction() {

		do {

			MozartTransaction mt = null;

			try {

				mt = (MozartTransaction) this.mozartSpaces.createTransaction(TransactionTimeout.INFINITE);

				MozartContainer mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS);

				ArrayList<Rocket> result = this.mozartSpaces.take(mc, mt, new MozartSelector(FifoCoordinator.newSelector(5)));

				logger.info("Package rockets ...");
				Thread.sleep(this.workRandomTime());

				for (Rocket rocket : result) {
					rocket.getEmployee().add(new Employee(this.employeeId));
				}

				RocketPackage rp = new RocketPackage(this.mozartSpaces.getIDAndIncr(MozartSpaces.PACKAGE_COUNTER));
				rp.setRocket1(result.get(0));
				rp.setRocket2(result.get(1));
				rp.setRocket3(result.get(2));
				rp.setRocket4(result.get(3));
				rp.setRocket5(result.get(4));

				this.mozartSpaces.write(MozartSpaces.ROCKET_PACKAGES, rp);

				this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

				logger.info("Rocket package created.");

			} catch (Exception e) {

				logger.error("", e);

				try {
					this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_ROLLBACK);

				} catch (Exception e1) {
					logger.error("", e1);
				}
			}

		} while (true);

	}
}
