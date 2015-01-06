package sbcm.order;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import sbc.space.MozartSpaces;
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

		// create shipping space only once
		SingleSpace.getInstance();

	}

	public void doOrder() {

		// create order and write requested rockets

		Order forwardOrder = new Order(this.mozartSpaces.getIDAndIncr(MozartSpaces.ORDER_COUNTER));
		forwardOrder.setEffectLoadColor1(this.order.getEffectLoadColor1());
		forwardOrder.setEffectLoadColor2(this.order.getEffectLoadColor2());
		forwardOrder.setEffectLoadColor3(this.order.getEffectLoadColor3());
		forwardOrder.setProduceQuantity(this.order.getQuantityRockets());
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
		// TODO Auto-generated method stub

	}
}