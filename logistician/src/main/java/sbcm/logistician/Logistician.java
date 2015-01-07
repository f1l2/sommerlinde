package sbcm.logistician;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceEntry;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.Employee;
import sbcm.factory.model.QualityCategory;
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

		try {

			MozartContainer containerA = ((MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_A));
			MozartContainer containerB = ((MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_B));

			try {
				this.readRockets(containerA, QualityCategory.A);
			} catch (Exception e) {
				logger.error("Reading A rockets failed: " + e.getMessage());
			}
			try {
				this.readRockets(containerB, QualityCategory.B);
			} catch (Exception e) {
				logger.error("Reading B rockets failed: " + e.getMessage());
			}
			NotificationManager notifManager = this.mozartSpaces.createNotificationManager();

			notifManager.createNotification(containerA.getContainer(), new Logistician.ListenerA(), Operation.WRITE);

			notifManager.createNotification(containerB.getContainer(), new Logistician.ListenerB(), Operation.WRITE);

			Thread.sleep(1000 * 60 * 10);

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void readRockets(MozartContainer mc, QualityCategory qC) throws Exception {

		MozartSelector ms = new MozartSelector(FifoCoordinator.newSelector(5));

		do {

			int workRandomTime = this.workRandomTime();

			MozartTransaction mt = (MozartTransaction) this.mozartSpaces.createTransaction(2000 + workRandomTime);

			this.createPackage(this.mozartSpaces.take(mc, mt, ms), qC);

			logger.info("Package rockets ...");
			Thread.sleep(workRandomTime);

			this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

			logger.info("Rocket package created.");

		} while (true);

	}

	private void createPackage(ArrayList<SpaceEntry> result, QualityCategory qC) throws Exception {

		for (SpaceEntry rocket : result) {
			((Rocket) rocket).getEmployee().add(new Employee(this.employeeId));
		}

		RocketPackage rp = new RocketPackage(this.mozartSpaces.getIDAndIncr(MozartSpaces.PACKAGE_COUNTER));
		rp.setRocket1((Rocket) result.get(0));
		rp.setRocket2((Rocket) result.get(1));
		rp.setRocket3((Rocket) result.get(2));
		rp.setRocket4((Rocket) result.get(3));
		rp.setRocket5((Rocket) result.get(4));
		rp.setQualityCategory(qC);

		this.mozartSpaces.write(MozartSpaces.ROCKET_PACKAGES, rp);

	}

	class ListenerA implements NotificationListener {

		public void entryOperationFinished(Notification arg0, Operation arg1, List<? extends Serializable> arg2) {
			try {
				MozartContainer mc = (MozartContainer) mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_A);
				readRockets(mc, QualityCategory.A);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

	}

	class ListenerB implements NotificationListener {

		public void entryOperationFinished(Notification arg0, Operation arg1, List<? extends Serializable> arg2) {
			try {
				MozartContainer mc = (MozartContainer) mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_B);
				readRockets(mc, QualityCategory.B);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}

	}
}
