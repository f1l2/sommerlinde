package sbcm.producer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.Employee;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Order;
import sbcm.factory.model.OrderStatus;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.Rocket;
import sbcm.factory.model.WoodenStaff;
import sbcm.space.role.Role;

/**
 * @author Manuel
 * 
 */
public class Producer extends Role implements NotificationListener {

	private MozartTransaction mt = null;

	private MozartContainer mcParts = null, mcOrders = null;

	public static void main(String[] args) {

		new Producer();
		System.exit(0);
	}

	public Producer() {
		super();
	}

	@Override
	protected void doAction() {

		try {
			mcParts = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PARTS);
			mcOrders = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.ORDERS);

			NotificationManager notifManager = this.mozartSpaces.createNotificationManager();
			notifManager.createNotification(((MozartContainer) mozartSpaces.findContainer(MozartSpaces.ORDERS)).getContainer(), this,
					Operation.WRITE);
		} catch (Exception e) {

		}

		do {

			if (this.isOpenOrder()) {

			}

			try {
				mt = (MozartTransaction) this.mozartSpaces.createTransaction(TransactionTimeout.INFINITE);

				this.produceRocket();
				this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

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

	private void produceRocket() throws Exception {
		this.produceRocket(null);
	}

	private void produceRocket(Order order) throws Exception {
		Rocket rocket = new Rocket(this.mozartSpaces.getIDAndIncr(MozartSpaces.ROCKET_COUNTER), null, false);
		List<Employee> employees = new ArrayList<Employee>();
		employees.add(new Employee(this.employeeId));
		rocket.setEmployee(employees);

		this.gatherIgniter(rocket);
		this.gatherWoodenStaff(rocket);

		if (order != null)
			this.gatherEffectLoad(rocket, order);
		else
			this.gatherEffectLoad(rocket);

		this.gatherPropellant(rocket);

		logger.info("Produce rocket.");
		Thread.sleep(this.workRandomTime());

		this.mozartSpaces.write(MozartSpaces.PRODUCED_ROCKETS, rocket);
		logger.info(this.employeeId + " produced a rock (ID = " + rocket.getId() + ").");
	}

	private void gatherPropellant(Rocket rocket) throws Exception {

		int propellantAmount = 110 + this.workRandomValue();

		rocket.setFillingQuantity(propellantAmount);
		rocket.setPropellant(new ArrayList<Propellant>());

		ComparableProperty amount = ComparableProperty.forName("amount");
		Query query = new Query().sortup(amount).cnt(1);

		Boolean isPropellantReached = false;
		while (!isPropellantReached) {

			Propellant propellant = (Propellant) this.mozartSpaces.take(mcParts, mt,
					new MozartSelector(QueryCoordinator.newSelector(query))).get(0);

			rocket.getPropellant().add(propellant);

			if (propellant.getAmount() >= propellantAmount) {

				propellant.setAmount(propellant.getAmount() - propellantAmount);

				if (propellant.getAmount() > 0) {
					this.mozartSpaces.write(MozartSpaces.PARTS, propellant);
				}

				isPropellantReached = true;

			} else {
				propellantAmount = propellantAmount - propellant.getAmount();
			}
		}
	}

	private void gatherWoodenStaff(Rocket rocket) throws Exception {
		MozartSelector woodenStaffSelector = new MozartSelector(LindaCoordinator.newSelector(new WoodenStaff(), 1));
		ArrayList<WoodenStaff> resultWoodenStaff = this.mozartSpaces.take(mcParts, mt, woodenStaffSelector);

		rocket.setWoodenStaff(resultWoodenStaff.get(0));
	}

	private void gatherEffectLoad(Rocket rocket, Order order) throws Exception {

		// color 1
		EffectLoad el = new EffectLoad();
		el.setColor(order.getEffectLoadColor1());

		MozartSelector effectLoadSelector = new MozartSelector(LindaCoordinator.newSelector(new EffectLoad(), 1));
		EffectLoad effectLoad = (EffectLoad) this.mozartSpaces.take(mcParts, mt, effectLoadSelector).get(0);

		ArrayList<EffectLoad> resultEffectLoads = new ArrayList<EffectLoad>();
		resultEffectLoads.add(effectLoad);

		// color 2
		el.setColor(order.getEffectLoadColor2());
		effectLoadSelector = new MozartSelector(LindaCoordinator.newSelector(new EffectLoad(), 1));

		effectLoad = (EffectLoad) this.mozartSpaces.take(mcParts, mt, effectLoadSelector).get(0);
		resultEffectLoads.add(effectLoad);

		// color 3
		el.setColor(order.getEffectLoadColor3());
		effectLoadSelector = new MozartSelector(LindaCoordinator.newSelector(new EffectLoad(), 1));

		effectLoad = (EffectLoad) this.mozartSpaces.take(mcParts, mt, effectLoadSelector).get(0);
		resultEffectLoads.add(effectLoad);

		rocket.setEffectiveLoad(resultEffectLoads);

	}

	private void gatherEffectLoad(Rocket rocket) throws Exception {

		MozartSelector effectLoadSelector = new MozartSelector(LindaCoordinator.newSelector(new EffectLoad(), 3));

		ArrayList<EffectLoad> resultEffectLoad = this.mozartSpaces.take(mcParts, mt, effectLoadSelector);
		rocket.setEffectiveLoad(resultEffectLoad);
	}

	private void gatherIgniter(Rocket rocket) throws Exception {

		MozartSelector igniterSelector = new MozartSelector(LindaCoordinator.newSelector(new Igniter(), 1));

		ArrayList<Igniter> resultIgniter = this.mozartSpaces.take(mcParts, mt, igniterSelector);
		rocket.setIgniter(resultIgniter.get(0));

	}

	private Boolean isOpenOrder() {

		try {
			mt = (MozartTransaction) this.mozartSpaces.createTransaction(1000);
			Order template = new Order();
			template.setStatus(OrderStatus.IN_PROCESS);

			MozartSelector orderSelector = new MozartSelector(LindaCoordinator.newSelector(template, 1));

			this.mozartSpaces.read(mcOrders, mt, orderSelector);

			this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

			return true;

		} catch (Exception e) {
			return false;
		}

	}

	public void entryOperationFinished(Notification arg0, Operation arg1, List<? extends Serializable> arg2) {
		// TODO Auto-generated method stub

	}
}
