package sbcm.deliverer;

import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.MzsConstants.Selecting;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;

import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.Order;
import sbcm.factory.model.OrderStatus;
import sbcm.factory.model.Rocket;
import sbcm.space.role.Role;

public class Deliverer extends Role {

	private MozartSpaces shippingSpaces = null;
	private MozartTransaction mt = null;
	private MozartContainer mcOrders = null, mcRockets;

	public static void main(String[] args) {
		new Deliverer();

		System.exit(0);
	}

	public Deliverer() {
		super();
	}

	@Override
	protected void doAction() {

		this.init();

		do {

			List<Rocket> rockets = new ArrayList<Rocket>();
			Order finishedOrder = new Order();
			Query query = null;

			// wait for finished orders
			try {

				// waiting for processed orders
				mt = (MozartTransaction) this.mozartSpaces.createTransaction(TransactionTimeout.INFINITE);

				query = new Query().filter(ComparableProperty.forName("status").equalTo(OrderStatus.PROCESSED)).cnt(1);
				finishedOrder = (Order) this.mozartSpaces.take(mcOrders, mt, new MozartSelector(QueryCoordinator.newSelector(query)))
						.get(0);
				this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

				// deliver processed orders

				logger.info("Found finished order. Id: " + finishedOrder.getId());

				try {
					// Test if shipping spaces is available

					this.shippingSpaces = new MozartSpaces(false, finishedOrder.getShippingAddress());
					MozartTransaction trans = (MozartTransaction) this.shippingSpaces.createTransaction();
					this.mozartSpaces.endTransaction(trans, TransactionEndType.TET_ROLLBACK);

				} catch (Exception e) {
					this.shippingSpaces = null;
				}

				if (this.shippingSpaces != null) {

					mt = (MozartTransaction) this.mozartSpaces.createTransaction(5000);

					Rocket templRocket = new Rocket();
					templRocket.setOrderId(finishedOrder.getId());
					rockets = this.mozartSpaces.take(mcRockets, mt,
							new MozartSelector(LindaCoordinator.newSelector(templRocket, Selecting.COUNT_ALL)));

					Thread.sleep(this.workRandomTime());

					this.shippingSpaces = new MozartSpaces(false, finishedOrder.getShippingAddress());

					for (Rocket rocket : rockets) {

						logger.info("- Deliver rocket: " + rocket.getId());

						this.shippingSpaces.write(MozartSpaces.STOCK, rocket);
					}

					// update status of order
					finishedOrder.setStatus(OrderStatus.DELIVERED);
					this.mozartSpaces.write(MozartSpaces.ORDERS, finishedOrder);

					this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

				} else {

					// update status of order
					finishedOrder.setStatus(OrderStatus.NOT_DELIVERED);
					this.mozartSpaces.write(MozartSpaces.ORDERS, finishedOrder);
				}

			} catch (Exception e) {
				logger.error("", e);
			}
		} while (true);
	}

	private void init() {
		try {
			mcOrders = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.ORDERS);
			mcRockets = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_ORDER);

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void debugData() throws Exception {
		logger.info("Write order");

		Order order = new Order(2);
		order.setStatus(OrderStatus.PROCESSED);
		order.setShippingAddress("xvsm://localhost:9874/");
		this.mozartSpaces.write(MozartSpaces.ORDERS, order);

		Rocket rocket1 = new Rocket(1, 1, null);
		rocket1.setOrderId(order.getId());
		this.mozartSpaces.write(MozartSpaces.GOOD_ROCKETS_ORDER, rocket1);

		Rocket rocket2 = new Rocket(2, 1, null);
		rocket2.setOrderId(order.getId());
		this.mozartSpaces.write(MozartSpaces.GOOD_ROCKETS_ORDER, rocket2);

		logger.info("wait");

		this.workRandomTime();

		logger.info("wait is over");

	}
}
