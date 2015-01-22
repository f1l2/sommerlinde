package sbcm.producer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.core.MzsConstants.Selecting;

/*import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;*/

import sbc.space.*;
/*import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;*/
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.EffectLoadColor;
import sbcm.factory.model.Employee;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Order;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.Rocket;
import sbcm.factory.model.WoodenStaff;
import sbcm.space.role.Role;

/**
 * @author Manuel
 * 
 */
public class Producer extends Role {

	private Boolean isOrder;

	private SpaceTransaction mt = null;

	private Container mcParts = null, mcOrders = null, mcRequested = null;

	public static void main(String[] args) {

		new Producer();
		System.exit(0);
	}

	public Producer() {
		super();
	}

	@Override
	protected void doAction() {

		this.init();

		do {
			Rocket rqRocket = null;
			isOrder = true;
			if (isOrder) {
				rqRocket = this.getNextRequestedRocket();
				isOrder = (rqRocket == null) ? false : true;
			}

			if (isOrder) {
				logger.info("--- Produce for order ---");
				this.produceForOrder(rqRocket);
			} else {
				logger.info("--- Produce for stock ---");
				this.produceForStock();
			}

		} while (true);
	}

	private void produceForOrder(Rocket rqRocket) {
		try {
			mt = this.mozartSpaces.createTransaction(-1);
			Order order = this.getOrderById(rqRocket.getOrderId());
			this.produceRocket(order);
			this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

		} catch (Exception e) {
			logger.error("", e);
			this.rollback();
		}
	}

	private void produceForStock() {
		try {
			mt = this.mozartSpaces.createTransaction(-1);
			this.produceRocket(null);
			this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);
			mt = null;
		} catch (Exception e) {
			logger.error("", e);
			this.rollback();
		}
	}

	private void init() {

		isOrder = true;

		try {
			mcParts = this.mozartSpaces.findContainer(MozartSpaces.PARTS);
			mcOrders = this.mozartSpaces.findContainer(MozartSpaces.ORDERS);
			mcRequested = this.mozartSpaces.findContainer(MozartSpaces.REQUESTED_ROCKETS);

/*			NotificationManager notifManager = this.mozartSpaces.createNotificationManager();

			notifManager.createNotification(mcRequested.getContainer(), this, Operation.WRITE);*/

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void produceRocket(Order order) throws Exception {
		Rocket rocket = new Rocket(this.mozartSpaces.getIDAndIncr(MozartSpaces.ROCKET_COUNTER), null, false);
		List<Employee> employees = new ArrayList<Employee>();
		employees.add(new Employee(this.employeeId));
		rocket.setEmployee(employees);

		this.gatherIgniter(rocket);
		this.gatherWoodenStaff(rocket);

		if (order != null) {
			this.gatherSingleEffectLoad(rocket, order.getEffectLoadColor1());
			this.gatherSingleEffectLoad(rocket, order.getEffectLoadColor2());
			this.gatherSingleEffectLoad(rocket, order.getEffectLoadColor3());
			rocket.setOrderId(order.getId());
		} else {
			this.gatherEffectLoad(rocket);
		}

		this.gatherPropellant(rocket);

		logger.info("Produce rocket.");
		Thread.sleep(this.workRandomTime());

		this.mozartSpaces.write(MozartSpaces.PRODUCED_ROCKETS, rocket);
		logger.info(this.employeeId + " produced a rock (ID = " + rocket.getId() + ").");
	}

	private void gatherPropellant(Rocket rocket) throws Exception {

		logger.info("Get propellant.");

		int propellantAmount = 110 + this.workRandomValue();

		rocket.setFillingQuantity(propellantAmount);
		rocket.setPropellant(new ArrayList<Propellant>());

//		ComparableProperty amount = ComparableProperty.forName("amount");
		AlterQuery query = new AlterQuery();
		query.sortup("getAmount").cnt(1);

		Boolean isPropellantReached = false;
		while (!isPropellantReached) {

			Propellant propellant = (Propellant) this.mozartSpaces.take(mcParts, mt, query).get(0);

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

		logger.info("Get woodenstaff.");

//		MozartSelector woodenStaffSelector = new MozartSelector(LindaCoordinator.newSelector(new WoodenStaff(), 1));
		AlterQuery query = new AlterQuery();
		query.getClass(new WoodenStaff()).cnt(1);

		WoodenStaff resultWoodenStaff = (WoodenStaff) this.mozartSpaces.take(mcParts, mt, query).get(0);

		rocket.setWoodenStaff(resultWoodenStaff);
	}

	private void gatherSingleEffectLoad(Rocket rocket, EffectLoadColor color) throws Exception {

		logger.info("Get effectLoad: " + color);

		AlterQuery query = new AlterQuery();
		query.prop("getColor").equaling(color).cnt(1);

		EffectLoad effectLoad = (EffectLoad) this.mozartSpaces.take(mcParts, mt, query).get(0);

		if (rocket.getEffectiveLoad() == null)
			rocket.setEffectiveLoad(new ArrayList<EffectLoad>());

		rocket.getEffectiveLoad().add(effectLoad);
	}

	private void gatherEffectLoad(Rocket rocket) throws Exception {

		logger.info("Get effectLoad.");

//		MozartSelector effectLoadSelector = new MozartSelector(LindaCoordinator.newSelector(new EffectLoad(), 3));
		AlterQuery query = new AlterQuery();
		query.getClass(new EffectLoad()).cnt(3);

		ArrayList<EffectLoad> resultEffectLoad = this.mozartSpaces.take(mcParts, mt, query);
		rocket.setEffectiveLoad(resultEffectLoad);
	}

	private void gatherIgniter(Rocket rocket) throws Exception {

		logger.info("Get igniter.");

//		MozartSelector igniterSelector = new MozartSelector(LindaCoordinator.newSelector(new Igniter(), 1));
		AlterQuery query = new AlterQuery();
		query.getClass(new Igniter()).cnt(1);

		Igniter resultIgniter = (Igniter) this.mozartSpaces.take(mcParts, mt, query).get(0);
		rocket.setIgniter(resultIgniter);

	}

	private Rocket getNextRequestedRocket() {
		try {
			AlterQuery query = new AlterQuery();
			Rocket result = null;
			query.getClass(new Rocket()).cnt(Selecting.COUNT_MAX);
			mt = this.mozartSpaces.createTransaction(2000);

			if (mozartSpaces.read(mcRequested, mt, query).size() > 0) {
//			MozartSelector requestedSelector = new MozartSelector(LindaCoordinator.newSelector(new Rocket(), 1));
				result = this.mozartSpaces.take(mcRequested, mt);
			}

			this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

			return result;

		} catch (Exception e) {

			return null;
		}
	}

	private Order getOrderById(Integer id) throws Exception {

		AlterQuery query = new AlterQuery();
		query.prop("getId").equaling(id).cnt(1);
		Order order = (Order) (this.mozartSpaces.take(mcOrders, mt, query).get(0));
		this.mozartSpaces.write(mcOrders, mt, order);
		return order;
	}

/*	public void entryOperationFinished(Notification arg0, Operation arg1, List<? extends Serializable> arg2) {

		this.isOrder = true;
	}*/

	private void rollback() {
		try {
			this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_ROLLBACK);
			mt = null;
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
