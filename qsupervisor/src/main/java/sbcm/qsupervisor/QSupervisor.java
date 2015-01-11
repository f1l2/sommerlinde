package sbcm.qsupervisor;

import java.util.ArrayList;

import sbc.space.AlterQuery;
import sbc.space.Container;
import sbc.space.SpaceTransaction;
import sbc.space.*;

/*import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.MzsConstants.Selecting;
import org.mozartspaces.core.MzsConstants.TransactionTimeout;*/

/*import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbc.space.MozartTransaction;*/
import sbc.space.SpaceTech.TransactionEndType;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.Employee;
import sbcm.factory.model.Order;
import sbcm.factory.model.OrderStatus;
import sbcm.factory.model.QualityCategory;
import sbcm.factory.model.Rocket;
import sbcm.space.role.Role;

public class QSupervisor extends Role {

	private SpaceTransaction mt = null;

	private Container mcRockets = null, mcOrders = null, mcOrderRockets = null;

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
//				MozartSelector rocketSelector = new MozartSelector(LindaCoordinator.newSelector(rocketTemplate, 1));
				AlterQuery query = new AlterQuery();
				query.getClass(new Rocket()).cnt(1);

				mt = this.mozartSpaces.createTransaction(-1);
				mcRockets = this.mozartSpaces.findContainer(MozartSpaces.PRODUCED_ROCKETS);
				mcOrders = this.mozartSpaces.findContainer(MozartSpaces.ORDERS);
				mcOrderRockets = this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_ORDER);

				ArrayList<Rocket> result = this.mozartSpaces.take(mcRockets, mt, query);

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
					logger.info("Defect (" + rocket.getFillingQuantity() + "/" + cntDefectEffectiveLoad + ")");
					rocket.setQualityCategory(QualityCategory.DEFEKT);
				}

				rocket.getEmployee().add(new Employee(this.employeeId));

				if (rocket.getQualityCategory().equals(QualityCategory.DEFEKT)) {

					this.createSpareRequest(rocket);

					this.mozartSpaces.write(MozartSpaces.DEFECT_ROCKETS, rocket);

				} else if (rocket.getQualityCategory().equals(QualityCategory.B)) {

					this.createSpareRequest(rocket);

					this.mozartSpaces.write(MozartSpaces.GOOD_ROCKETS_B, rocket);

				} else if (rocket.getQualityCategory().equals(QualityCategory.A)) {

					if (rocket.getOrderId() != null) {

						this.mozartSpaces.write(MozartSpaces.GOOD_ROCKETS_ORDER, rocket);

						this.checkIfOrderIsFinished(rocket);

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

	private void checkIfOrderIsFinished(Rocket rocket) throws Exception {

/*		Order order = this.readOrderById(rocket.getOrderId());

		Rocket templRocket = new Rocket();
		templRocket.setOrderId(order.getId());
		ArrayList<Rocket> rockets = this.mozartSpaces.read(mcOrderRockets, mt,
				new MozartSelector(LindaCoordinator.newSelector(templRocket, Selecting.COUNT_ALL)));

		if (rockets.size() == order.getQuantityRockets()) {
			Order takeOrder = this.takeOrderById(rocket.getOrderId());
			takeOrder.setStatus(OrderStatus.PROCESSED);
			logger.info("Write order " + takeOrder.toString());

			this.mozartSpaces.write(MozartSpaces.ORDERS, takeOrder);
		}*/
		System.err.println("TODO: Checking if order is finished");
	}

	private void createSpareRequest(Rocket rocket) throws Exception {
		if (rocket.getOrderId() != null) {
			Order order = this.readOrderById(rocket.getOrderId());

			Rocket newRocket = new Rocket();
			newRocket.setOrderId(order.getId());
			this.mozartSpaces.write(MozartSpaces.REQUESTED_ROCKETS, newRocket);

			rocket.setOrderId(null);
		}
	}

	private Order readOrderById(Integer id) throws Exception {

//		Query query = new Query().filter(ComparableProperty.forName("id").equalTo(id)).cnt(1);
		AlterQuery query = new AlterQuery();
		query.prop("getId").equaling(id);
		Order order = (Order) this.mozartSpaces.read(mcOrders, mt, query).get(0);

		logger.info("Read order " + order.toString());

		return order;
	}

	private Order takeOrderById(Integer id) throws Exception {

//		Query query = new Query().filter(ComparableProperty.forName("id").equalTo(id)).cnt(1);
		AlterQuery query = new AlterQuery();
		query.prop("getId").equaling(id);
		Order order = (Order) this.mozartSpaces.take(mcOrders, mt, query).get(0);

		logger.info("Take order " + order.toString());

		return order;
	}
}
