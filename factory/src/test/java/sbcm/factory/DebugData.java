package sbcm.factory;

import sbc.space.MozartSpaces;
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
 * Unit test for simple App.
 */
public class DebugData extends Role {

	@SuppressWarnings("unused")
	private void debugDataProducer() {

		try {

			Order order = new Order(1);
			order.setEffectLoadColor1(EffectLoadColor.RED);
			order.setEffectLoadColor2(EffectLoadColor.BLUE);
			order.setEffectLoadColor3(EffectLoadColor.GREEN);
			order.setQuantityRockets(3);

			this.mozartSpaces.write(MozartSpaces.ORDERS, order);

			for (int i = 0; i < order.getQuantityRockets(); i++) {
				Rocket rocket = new Rocket();
				rocket.setOrderId(order.getId());
				this.mozartSpaces.write(MozartSpaces.REQUESTED_ROCKETS, rocket);
			}

			for (int i = 1; i <= 1; i++) {

				Igniter ig = new Igniter(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				ig.setSupplier(new Employee(employeeId));
				mozartSpaces.write(MozartSpaces.PARTS, ig);

			}

			for (int i = 1; i <= 1; i++) {

				Propellant p = new Propellant(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				p.setAmount(500);
				p.setSupplier(new Employee(employeeId));

				mozartSpaces.write(MozartSpaces.PARTS, p);

			}

			for (int i = 1; i <= 1; i++) {

				WoodenStaff w = new WoodenStaff(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				w.setSupplier(new Employee(employeeId));

				mozartSpaces.write(MozartSpaces.PARTS, w);

			}

			for (int i = 1; i <= 1; i++) {

				EffectLoad el = new EffectLoad(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				el.setSupplier(new Employee(employeeId));
				el.setColor(EffectLoadColor.RED);
				el.setIsDefect(Boolean.FALSE);

				mozartSpaces.write(MozartSpaces.PARTS, el);
			}

			for (int i = 1; i <= 1; i++) {

				EffectLoad el = new EffectLoad(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				el.setSupplier(new Employee(employeeId));
				el.setColor(EffectLoadColor.BLUE);
				el.setIsDefect(Boolean.FALSE);

				mozartSpaces.write(MozartSpaces.PARTS, el);

			}

			for (int i = 1; i <= 1; i++) {

				EffectLoad el = new EffectLoad(mozartSpaces.getIDAndIncr(MozartSpaces.PART_COUNTER));
				el.setSupplier(new Employee(employeeId));
				el.setColor(EffectLoadColor.GREEN);

				el.setIsDefect(Boolean.FALSE);

				mozartSpaces.write(MozartSpaces.PARTS, el);
			}

		} catch (Exception e) {

		}

	}

	@Override
	protected void doAction() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	private void debugDataDeliver() throws Exception {
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
