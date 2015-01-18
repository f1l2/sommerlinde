package sbcm.deliverer;

import java.util.ArrayList;
import java.util.List;

/*import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;*/

import org.mozartspaces.core.MzsConstants.Selecting;

import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.Order;
import sbcm.factory.model.OrderStatus;
import sbcm.factory.model.Rocket;
import sbcm.space.role.Role;
import sbc.space.*;

public class Deliverer extends Role {

	private SpaceTech shippingSpaces = null;
	private SpaceTransaction mt = null;
	private Container mcOrders = null, mcRockets;

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
//			Query query = null;
			AlterQuery query = new AlterQuery();

			// wait for finished orders
			try {

				// waiting for processed orders
				mt = this.mozartSpaces.createTransaction(-1);

//				query = new Query().filter(ComparableProperty.forName("status").equalTo(OrderStatus.PROCESSED)).cnt(1);
				query.prop("getStatus").equaling(OrderStatus.PROCESSED).cnt(1);
				finishedOrder = (Order) this.mozartSpaces.take(mcOrders, mt, query)
						.get(0);
				this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

				// deliver processed orders

				logger.info("Found finished order. Id: " + finishedOrder.getId());

				try {
					// Test if shipping spaces is available

					this.shippingSpaces = new AlterSpaceClient(finishedOrder.getShippingAddress());
					SpaceTransaction trans = this.shippingSpaces.createTransaction();
					this.mozartSpaces.endTransaction(trans, TransactionEndType.TET_ROLLBACK);

				} catch (Exception e) {
					this.shippingSpaces = null;
				}

				if (this.shippingSpaces != null) {

					AlterQuery q = new AlterQuery();
					mt = this.mozartSpaces.createTransaction(5000);

					Rocket templRocket = new Rocket();
					templRocket.setOrderId(finishedOrder.getId());
					q.getClass(templRocket).cnt(Selecting.COUNT_ALL);
					rockets = this.mozartSpaces.take(mcRockets, mt, q);

					Thread.sleep(this.workRandomTime());

					this.shippingSpaces = new AlterSpaceClient(finishedOrder.getShippingAddress());

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
			mcOrders = this.mozartSpaces.findContainer(MozartSpaces.ORDERS);
			mcRockets = this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_ORDER);

		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
