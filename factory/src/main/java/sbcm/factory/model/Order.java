package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

import sbc.space.SpaceEntry;

@Queryable(autoindex = true)
public class Order extends SpaceEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer requestedQuantity;

	private String shippingAddress;

	private EffectLoadColor effectLoadColor1;

	private EffectLoadColor effectLoadColor2;

	private EffectLoadColor effectLoadColor3;

	private OrderStatus status;

	private Integer ordererId;

	public Order() {
		super();
	}

	public Order(int id) {
		super(id, 0, false, 0);
	}

	public Integer getQuantityRockets() {
		return requestedQuantity;
	}

	public void setQuantityRockets(Integer quantityRockets) {
		this.requestedQuantity = quantityRockets;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public EffectLoadColor getEffectLoadColor1() {
		return effectLoadColor1;
	}

	public void setEffectLoadColor1(EffectLoadColor effectLoadColor1) {
		this.effectLoadColor1 = effectLoadColor1;
	}

	public EffectLoadColor getEffectLoadColor2() {
		return effectLoadColor2;
	}

	public void setEffectLoadColor2(EffectLoadColor effectLoadColor2) {
		this.effectLoadColor2 = effectLoadColor2;
	}

	public EffectLoadColor getEffectLoadColor3() {
		return effectLoadColor3;
	}

	public void setEffectLoadColor3(EffectLoadColor effectLoadColor3) {
		this.effectLoadColor3 = effectLoadColor3;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Integer getOrdererId() {
		return ordererId;
	}

	public void setOrdererId(Integer ordererId) {
		this.ordererId = ordererId;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Id: ");
		sb.append(this.getId());
		sb.append("; quantityRockets: ");
		sb.append(this.getQuantityRockets());
		sb.append("; status: ");
		sb.append(this.getStatus());

		return sb.toString();
	}
}
