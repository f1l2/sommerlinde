package sbcm.order;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.LindaCoordinator.LindaSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbc.space.MozartContainer;
import sbc.space.MozartSelector;
import sbc.space.MozartSpaces;
import sbcm.factory.model.Rocket;
import sbcm.utility.SingleSpace;

@ManagedBean(name = "stock")
@ViewScoped
public class StockBean {

	private static final Logger logger = LoggerFactory.getLogger(StockBean.class);

	private List<Rocket> deliveredRockets;

	private MozartSpaces shippingSpaces;

	public StockBean() {

		this.shippingSpaces = SingleSpace.getInstance().getShippingSpace();

		this.generateStockReport();
	}

	public void generateStockReport() {

		logger.info("-------- STOCK ---------- ");

		MozartSelector ms = new MozartSelector(LindaCoordinator.newSelector(new Rocket(), LindaSelector.COUNT_MAX));

		try {

			MozartContainer mc = (MozartContainer) this.shippingSpaces.findContainer(MozartSpaces.STOCK);

			this.deliveredRockets = this.shippingSpaces.read(mc, null, ms);
			logger.info("# Rockets: " + this.deliveredRockets.size());

		} catch (Exception e) {
			logger.error("", e);
		}

		logger.info("-------- END STOCK ---------- ");
	}

	public List<Rocket> getDeliveredRockets() {
		return deliveredRockets;
	}

	public void setDeliveredRockets(List<Rocket> deliveredRockets) {
		this.deliveredRockets = deliveredRockets;
	}
}
