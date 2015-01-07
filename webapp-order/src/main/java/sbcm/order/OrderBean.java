package sbcm.order;

import java.io.IOException;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.Matchmaker;
import org.mozartspaces.capi3.Matchmakers;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.MzsConstants.Selecting;

import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.EffectLoadColor;
import sbcm.factory.model.Order;
import sbcm.factory.model.OrderStatus;
import sbcm.factory.model.Rocket;
import sbcm.space.role.Role;
import sbcm.utility.SingleSpace;

@ManagedBean(name = "orderBean")
@ViewScoped
public class OrderBean extends Role {

	private Order order;

	public OrderBean() {
		super();
		// initialize
		this.order = new Order();
		this.order.setQuantityRockets(0);
		this.order.setShippingAddress(SingleSpace.URI);

		this.employeeId = 5;

		this.fetchPendingOrders();
	}

	public void doOrder() {

		// create order and write requested rockets

		Order forwardOrder = new Order(this.mozartSpaces.getIDAndIncr(MozartSpaces.ORDER_COUNTER));
		forwardOrder.setEffectLoadColor1(this.order.getEffectLoadColor1());
		forwardOrder.setEffectLoadColor2(this.order.getEffectLoadColor2());
		forwardOrder.setEffectLoadColor3(this.order.getEffectLoadColor3());
		forwardOrder.setQuantityRockets(this.order.getQuantityRockets());
		forwardOrder.setShippingAddress(this.order.getShippingAddress());
		forwardOrder.setOrdererId(this.employeeId);
		forwardOrder.setStatus(OrderStatus.SCHEDULED);

		try {
			this.mozartSpaces.write(MozartSpaces.ORDERS, forwardOrder);

			for (int i = 0; i < forwardOrder.getQuantityRockets(); i++) {
				Rocket rocket = new Rocket();
				rocket.setOrderId(forwardOrder.getId());
				this.mozartSpaces.write(MozartSpaces.REQUESTED_ROCKETS, rocket);
			}

		} catch (Exception e) {
			logger.error("", e);
		}

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

		try {
			ec.redirect(ec.getRequestContextPath() + "/order/stock.xhtml");
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	private void fetchPendingOrders() {

		MozartTransaction mt = null;

		try {

			// this.debugDataDeliver();

			logger.info("Fetch pending order.");

			MozartSpaces space = SingleSpace.getInstance().getShippingSpace();

			mt = (MozartTransaction) this.mozartSpaces.createTransaction(1000);

			MozartContainer mcOrders = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.ORDERS);
			MozartContainer mcRockets = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_ORDER);

			Matchmaker status = ComparableProperty.forName("status").equalTo(OrderStatus.NOT_DELIVERED);
			Matchmaker ordererId = ComparableProperty.forName("ordererId").equalTo(this.employeeId);
			Query query = new Query().filter(Matchmakers.and(status, ordererId));

			ArrayList<Order> orders = this.mozartSpaces.take(mcOrders, mt, new MozartSelector(QueryCoordinator.newSelector(query)), 1000);

			logger.info("Found pending orders.");

			for (Order order : orders) {

				logger.info("Order: " + order.getId());

				Rocket templRocket = new Rocket();
				templRocket.setOrderId(order.getId());

				ArrayList<Rocket> rockets = this.mozartSpaces.take(mcRockets, mt,
						new MozartSelector(LindaCoordinator.newSelector(templRocket, Selecting.COUNT_ALL)));

				for (Rocket rocket : rockets) {
					logger.info("- Fetch rocket: " + rocket.getId());
					space.write(MozartSpaces.STOCK, rocket);
				}

				order.setStatus(OrderStatus.DELIVERED);

				this.mozartSpaces.write(MozartSpaces.ORDERS, order);

			}
			this.mozartSpaces.endTransaction(mt, TransactionEndType.TET_COMMIT);

			logger.info("Fetching over.");

		} catch (Exception e) {

			logger.info("Nothing found.");
		}
	}

	public EffectLoadColor[] getColor() {
		return EffectLoadColor.values();
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	protected void doAction() {
	}
}