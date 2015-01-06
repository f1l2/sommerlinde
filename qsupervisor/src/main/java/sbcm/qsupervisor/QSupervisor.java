package sbcm.qsupervisor;

import java.util.ArrayList;

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
import sbcm.factory.model.Employee;
import sbcm.factory.model.Order;
import sbcm.factory.model.OrderStatus;
import sbcm.factory.model.QualityCategory;
import sbcm.factory.model.Rocket;
import sbcm.space.role.Role;

public class QSupervisor extends Role {

	private MozartTransaction mt = null;

	private MozartContainer mcRockets = null, mcOrders = null;

	public static void main(String[] args) {
		new QSupervisor();
	}

	public QSupervisor() {
		super();
	}

	@Override
	protected void doAction() {

		do {

			try {
				Rocket rocketTemplate = new Rocket();
				MozartSelector rocketSelector = new MozartSelector(LindaCoordinator.newSelector(rocketTemplate, 1));

				mt = (MozartTransaction) this.mozartSpaces.createTransaction(TransactionTimeout.INFINITE);
				mcRockets = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PRODUCED_ROCKETS);
				mcOrders = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.ORDERS);

				ArrayList<Rocket> result = this.mozartSpaces.take(mcRockets, mt, rocketSelector);

				logger.info("Check rocket (Id = " + result.get(0).getId() + ").");
				Thread.sleep(this.workRandomTime());

				Rocket rocket = result.get(0);

				/**
				 * Propellant amount has to be more than 120.
				 * 
				 * Only one defect effect load is allowed.
				 */

				int cntDefectEffectiveLoad = 0;
				for (EffectLoad effectiveLoad : rocket.getEffectiveLoad()) {
					if (effectiveLoad.getIsDefect())
						cntDefectEffectiveLoad++;
				}

				if ((rocket.getFillingQuantity() >= 130) && (cntDefectEffectiveLoad == 0)) {
					logger.info("Quality category A.");
					rocket.setQualityCategory(QualityCategory.A);
				} else if ((rocket.getFillingQuantity() >= 120) && (cntDefectEffectiveLoad <= 1)) {
					logger.info("Quality category B.");
					rocket.setQualityCategory(QualityCategory.B);
				} else {
					logger.info("Defect.");
					rocket.setQualityCategory(QualityCategory.DEFEKT);
				}

				rocket.getEmployee().add(new Employee(this.employeeId));

				if (rocket.getQualityCategory().equals(QualityCategory.DEFEKT)) {
					this.incrProduceQuantity(rocket);
					rocket.setOrderId(null);
					this.mozartSpaces.write(MozartSpaces.DEFECT_ROCKETS, rocket);

				} else if (rocket.getQualityCategory().equals(QualityCategory.B)) {
					this.incrProduceQuantity(rocket);
					rocket.setOrderId(null);
					this.mozartSpaces.write(MozartSpaces.GOOD_ROCKETS_B, rocket);

				} else if (rocket.getQualityCategory().equals(QualityCategory.A)) {
					this.incrProducedQuantity(rocket);
					if (rocket.getOrderId() != null) {
						this.mozartSpaces.write(MozartSpaces.GOOD_ROCKETS_ORDER, rocket);
					} else {
						this.mozartSpaces.write(MozartSpaces.GOOD_ROCKETS_A, rocket);
					}
				}
				logger.info("Rocket checked: (Id = " + result.get(0).getId() + "; QualityCategory = " + rocket.getQualityCategory() + ").");

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

	private void incrProducedQuantity(Rocket rocket) throws Exception {
		if (null != rocket.getOrderId()) {

			Order order = this.getOrderById(rocket.getOrderId());

			if (order.getProducedQuantity() == null) {
				order.setProducedQuantity(1);
			} else {
				order.setProducedQuantity(order.getProducedQuantity() + 1);
			}

			if (order.getQuantityRockets() <= order.getProducedQuantity()) {
				order.setStatus(OrderStatus.PROCESSED);
			}

			logger.info("Write order " + order.toString());

			this.mozartSpaces.write(MozartSpaces.ORDERS, order);
		}
	}

	private void incrProduceQuantity(Rocket rocket) throws Exception {
		if (null != rocket.getOrderId()) {

			Order order = this.getOrderById(rocket.getOrderId());
			if (order.getProduceQuantity() == null) {
				order.setProduceQuantity(1);
			} else {
				order.setProduceQuantity(order.getProduceQuantity() + 1);
			}

			logger.info("Write order " + order.toString());

			this.mozartSpaces.write(MozartSpaces.ORDERS, order);
		}
	}

	private Order getOrderById(Integer id) throws Exception {

		Query query = new Query().filter(ComparableProperty.forName("id").equalTo(id)).cnt(1);
		Order order = (Order) this.mozartSpaces.take(mcOrders, mt, new MozartSelector(QueryCoordinator.newSelector(query))).get(0);

		logger.info("Take order " + order.toString());

		return order;
	}
}
