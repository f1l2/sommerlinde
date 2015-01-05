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
import sbcm.space.role.Role;

@ManagedBean(name = "orderBean")
@ViewScoped
public class OrderBean extends Role {

	private Order order;

	public OrderBean() {
		super();
		this.order = new Order();
		this.order.setQuantityRockets(0);
	}

	public void doOrder() {

		Order saveOrder = new Order(this.mozartSpaces.getIDAndIncr(MozartSpaces.ORDER_COUNTER));
		saveOrder.setEffectLoadColor1(this.order.getEffectLoadColor1());
		saveOrder.setEffectLoadColor2(this.order.getEffectLoadColor2());
		saveOrder.setEffectLoadColor3(this.order.getEffectLoadColor3());
		saveOrder.setQuantityRockets(this.order.getQuantityRockets());
		saveOrder.setShippingAddress(this.order.getShippingAddress());
		saveOrder.setStatus(OrderStatus.IN_PROCESS);

		try {
			this.mozartSpaces.write(MozartSpaces.ORDERS, saveOrder);
		} catch (Exception e) {
			logger.error("", e);
		}

		try {
			Thread.sleep(workRandomTime());
		} catch (InterruptedException e) {
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