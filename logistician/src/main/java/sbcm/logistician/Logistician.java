package sbcm.logistician;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;*/

import sbc.space.*;
import sbc.space.AlterSpaceClient;
//import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
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

		this.init();

		do {
			try {
				Thread.sleep(1000000);
			} catch (Exception e) {
				continue;
			}
		} while (true);
	}

	private void init() {

		try {
/*			this.containerA = (this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_A));
			this.containerB = (this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_B));*/

/*			try {
				this.readRockets(containerA, QualityCategory.A);
			} catch (Exception e) {
				logger.error("Reading A rockets failed: " + e.getMessage());
			}
			try {
				this.readRockets(containerB, QualityCategory.B);
			} catch (Exception e) {
				logger.error("Reading B rockets failed: " + e.getMessage());
			}*/
/*			NotificationManager notifManager = this.mozartSpaces.createNotificationManager();

			notifManager.createNotification(containerA.getContainer(), new Logistician.ListenerA(), Operation.WRITE);

			notifManager.createNotification(containerB.getContainer(), new Logistician.ListenerB(), Operation.WRITE);*/
			Thread t1 = new Thread(new Logistician.Listener(QualityCategory.A, MozartSpaces.GOOD_ROCKETS_A));
			t1.start();
			Thread t2 = new Thread(new Logistician.Listener(QualityCategory.B, MozartSpaces.GOOD_ROCKETS_B));
			t2.start();
			
			t1.join();
			t2.join();

			//Thread.sleep(1000 * 60 * 10);

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void readRockets(AlterSpaceClient space, Container mc, QualityCategory qC) throws Exception {
		int workRandomTime = this.workRandomTime();

		SpaceTransaction mt = space.createTransaction(2000 + workRandomTime);

		RocketPackage rp = this.createPackage(space, space.take(mc, mt, SpaceTech.SelectorType.SEL_FIFO, 5), qC);

		space.write(space.findContainer(MozartSpaces.ROCKET_PACKAGES), mt, rp);

		logger.info("Package rockets ...");
		Thread.sleep(workRandomTime);

		space.endTransaction(mt, TransactionEndType.TET_COMMIT);

		logger.info("Rocket package created.");
	}

	private RocketPackage createPackage(AlterSpaceClient space, ArrayList<SpaceEntry> result, QualityCategory qC) throws Exception {

		for (SpaceEntry rocket : result) {
			((Rocket) rocket).getEmployee().add(new Employee(this.employeeId));
		}

		RocketPackage rp = new RocketPackage(space.getIDAndIncr(MozartSpaces.PACKAGE_COUNTER));
		rp.setRocket1((Rocket) result.get(0));
		rp.setRocket2((Rocket) result.get(1));
		rp.setRocket3((Rocket) result.get(2));
		rp.setRocket4((Rocket) result.get(3));
		rp.setRocket5((Rocket) result.get(4));
		rp.setQualityCategory(qC);
		return rp;
	}

	class Listener implements Runnable {
		private QualityCategory myQuality;
		private String container_str;
		private Container container;
		private AlterSpaceClient space;
		public Listener(QualityCategory qC, String c) {
			super();
			myQuality = qC;
			container_str = c;
			try {
				space = new AlterSpaceClient();
				container = space.findContainer(container_str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void run() {
			while (true) {
				try {
					readRockets(space, container, myQuality);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	/*class ListenerB implements Runnable {

		public void run() {
			while (true) {
				try {
					readRockets(containerB, QualityCategory.B);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}*/
}
