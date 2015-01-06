package sbcm.producer;

import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;

import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.EffectLoadColor;
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
public class Producer extends Role {

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

		this.init();

		do {
			Order nextOpenOrder = this.getNextOpenOrder();

			if (null != nextOpenOrder) {
				logger.info("--- Produce for order ...");
				this.produceForOrder(nextOpenOrder);
			} else {
				logger.info("--- Produce for stock ...");
				this.produceForStock();
			}

		} while (true);
	}

	private void produceForOrder(Order order) {
		try {
			Order updatedOrder = null;

			for (int i = 0; i < order.getProduceQuantity(); i++) {
				mt = (MozartTransaction) this.mozartSpaces.createTransaction(TransactionTimeout.INFINITE);
				this.produceRocket(order);

				updatedOrder = this.getOrderById(order.getId());
				updatedOrder.setStatus(OrderStatus.IN_PROCESS);
				updatedOrder.setProduceQuantity(updatedOrder.getProduceQuantity() - 1);

				logger.info("Write order " + updatedOrder.toString());

				this.mozartSpaces.write(MozartSpaces.ORDERS, updatedOrder);

				this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());

			try {
				this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_ROLLBACK);
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
	}

	private void produceForStock() {
		try {
			mt = (MozartTransaction) this.mozartSpaces.createTransaction(TransactionTimeout.INFINITE);
			this.produceRocket(null);
			this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void init() {
		try {
			mcParts = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PARTS);
			mcOrders = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.ORDERS);
		} catch (Exception e) {
			logger.error("", e);
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

		logger.info("Get woodenstaff.");

		MozartSelector woodenStaffSelector = new MozartSelector(LindaCoordinator.newSelector(new WoodenStaff(), 1));
		ArrayList<WoodenStaff> resultWoodenStaff = this.mozartSpaces.take(mcParts, mt, woodenStaffSelector);

		rocket.setWoodenStaff(resultWoodenStaff.get(0));
	}

	private void gatherSingleEffectLoad(Rocket rocket, EffectLoadColor color) throws Exception {

		logger.info("Get effectLoad: " + color);

		EffectLoad el = new EffectLoad();
		el.setColor(color);
		MozartSelector effectLoadSelector = new MozartSelector(LindaCoordinator.newSelector(el, 1));

		EffectLoad effectLoad = (EffectLoad) this.mozartSpaces.take(mcParts, mt, effectLoadSelector).get(0);

		if (rocket.getEffectiveLoad() == null)
			rocket.setEffectiveLoad(new ArrayList<EffectLoad>());

		rocket.getEffectiveLoad().add(effectLoad);
	}

	private void gatherEffectLoad(Rocket rocket) throws Exception {

		logger.info("Get effectLoad.");

		MozartSelector effectLoadSelector = new MozartSelector(LindaCoordinator.newSelector(new EffectLoad(), 3));

		ArrayList<EffectLoad> resultEffectLoad = this.mozartSpaces.take(mcParts, mt, effectLoadSelector);
		rocket.setEffectiveLoad(resultEffectLoad);
	}

	private void gatherIgniter(Rocket rocket) throws Exception {

		logger.info("Get igniter.");

		MozartSelector igniterSelector = new MozartSelector(LindaCoordinator.newSelector(new Igniter(), 1));

		ArrayList<Igniter> resultIgniter = this.mozartSpaces.take(mcParts, mt, igniterSelector);
		rocket.setIgniter(resultIgniter.get(0));

	}

	private Order getNextOpenOrder() {

		try {
			mt = (MozartTransaction) this.mozartSpaces.createTransaction(1000);
			Order template = new Order();
			template.setStatus(OrderStatus.SCHEDULED);

			Query query = new Query().filter(ComparableProperty.forName("produceQuantity").greaterThan(0)).cnt(1);

			Order order = (Order) this.mozartSpaces.take(mcOrders, mt, new MozartSelector(QueryCoordinator.newSelector(query))).get(0);

			logger.info("Take order " + order.toString());

			order.setStatus(OrderStatus.IN_PROCESS);

			logger.info("Write order " + order.toString());

			this.mozartSpaces.write(MozartSpaces.ORDERS, order);

			this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

			return order;

		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	private Order getOrderById(Integer id) throws Exception {

		Query query = new Query().filter(ComparableProperty.forName("id").equalTo(id)).cnt(1);
		Order order = (Order) this.mozartSpaces.take(mcOrders, mt, new MozartSelector(QueryCoordinator.newSelector(query))).get(0);

		logger.info("Take order " + order.toString());

		return order;
	}
}
