package sbcm.supplier;

import java.util.ArrayList;
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
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.EffectLoadColor;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Order;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.Rocket;
import sbcm.factory.model.RocketPackage;
import sbcm.factory.model.WoodenStaff;

@ManagedBean
@ViewScoped
public class Report {

	private static final Logger logger = LoggerFactory.getLogger(Report.class);

	private List<Rocket> producedRockets;
	private List<Rocket> goodRockets;
	private List<Rocket> defectRockets;
	private List<Rocket> rqRockets;
	private List<Igniter> lIgniter;
	private List<Propellant> lPropellant;
	private List<WoodenStaff> lWoodenStaff;
	private List<RocketPackage> packages;
	private List<Order> orders;
	private List<EffectLoad> lEffectLoad;

	private int eLRed = 0, eLGreen = 0, eLBlue = 0;

	private MozartSpaces mozartSpaces;

	public List<Igniter> getlIgniter() {
		return lIgniter;
	}

	public static void main(String[] args) {

		new Report();

		System.exit(0);
	}

	public Report() {

		this.mozartSpaces = new MozartSpaces(false);

		this.generateReport();
	}

	public void generateReport() {

		logger.info("-------- REPORT ---------- ");

		MozartSelector igniterSelector = new MozartSelector(LindaCoordinator.newSelector(new Igniter(), LindaSelector.COUNT_MAX));
		MozartSelector propellantSelector = new MozartSelector(LindaCoordinator.newSelector(new Propellant(), LindaSelector.COUNT_MAX));
		MozartSelector woodenStaffSelector = new MozartSelector(LindaCoordinator.newSelector(new WoodenStaff(), LindaSelector.COUNT_MAX));
		MozartSelector effectLoadSelector = new MozartSelector(LindaCoordinator.newSelector(new EffectLoad(), LindaSelector.COUNT_MAX));
		MozartSelector rocketSelector = new MozartSelector(LindaCoordinator.newSelector(new Rocket(), LindaSelector.COUNT_MAX));
		MozartSelector packageSelector = new MozartSelector(LindaCoordinator.newSelector(new RocketPackage(), LindaSelector.COUNT_MAX));
		MozartSelector orderSelector = new MozartSelector(LindaCoordinator.newSelector(new Order(), LindaSelector.COUNT_MAX));

		try {

			MozartContainer mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PARTS);

			this.lIgniter = this.mozartSpaces.read(mc, null, igniterSelector);
			logger.info("# Igniter: " + lIgniter.size());

			this.lPropellant = this.mozartSpaces.read(mc, null, propellantSelector);
			logger.info("# Propellant: " + lPropellant.size());
			for (Propellant propellant : lPropellant) {
				logger.info("- ID:" + propellant.getId() + "; Amount: " + propellant.getAmount());
			}

			this.lWoodenStaff = this.mozartSpaces.read(mc, null, woodenStaffSelector);
			logger.info("# WoodenStaff: " + lWoodenStaff.size());

			this.lEffectLoad = this.mozartSpaces.read(mc, null, effectLoadSelector);
			logger.info("# EffectLoad: " + lEffectLoad.size());

			for (EffectLoad el : lEffectLoad) {
				if (EffectLoadColor.RED.equals(el.getColor())) {
					this.seteLRed(this.geteLRed() + 1);
				} else if (EffectLoadColor.GREEN.equals(el.getColor())) {
					this.seteLGreen(this.geteLGreen() + 1);
				} else if (EffectLoadColor.BLUE.equals(el.getColor())) {
					this.seteLBlue(this.geteLBlue() + 1);
				}
			}

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PRODUCED_ROCKETS);
			this.producedRockets = this.mozartSpaces.read(mc, null, rocketSelector);
			logger.info("# Produced rockets: " + this.producedRockets.size());

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_A);
			this.goodRockets = this.mozartSpaces.read(mc, null, rocketSelector);

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_B);
			ArrayList<Rocket> tempResult = this.mozartSpaces.read(mc, null, rocketSelector);
			this.goodRockets.addAll(tempResult);

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS_ORDER);
			tempResult = this.mozartSpaces.read(mc, null, rocketSelector);
			this.goodRockets.addAll(tempResult);
			logger.info("# Good rockets: " + this.goodRockets.size());

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.DEFECT_ROCKETS);
			this.defectRockets = this.mozartSpaces.read(mc, null, rocketSelector);
			logger.info("# Defect rockets: " + this.defectRockets.size());

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.ROCKET_PACKAGES);
			this.packages = this.mozartSpaces.read(mc, null, packageSelector);
			logger.info("# Packages: " + this.packages.size());

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.ORDERS);
			this.orders = this.mozartSpaces.read(mc, null, orderSelector);
			logger.info("# Orders: " + this.orders.size());

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.REQUESTED_ROCKETS);
			this.rqRockets = this.mozartSpaces.read(mc, null, rocketSelector);
			logger.info("# Requested rockets: " + this.rqRockets.size());

		} catch (Exception e) {
			logger.error("", e);
		}

		logger.info("-------- END REPORT ---------- ");
	}

	public void setlIgniter(List<Igniter> lIgniter) {
		this.lIgniter = lIgniter;
	}

	public List<Propellant> getlPropellant() {
		return lPropellant;
	}

	public void setlPropellant(List<Propellant> lPropellant) {
		this.lPropellant = lPropellant;
	}

	public List<WoodenStaff> getlWoodenStaff() {
		return lWoodenStaff;
	}

	public void setlWoodenStaff(List<WoodenStaff> lWoodenStaff) {
		this.lWoodenStaff = lWoodenStaff;
	}

	public List<EffectLoad> getlEffectLoad() {
		return lEffectLoad;
	}

	public void setlEffectLoad(List<EffectLoad> lEffectLoad) {
		this.lEffectLoad = lEffectLoad;
	}

	public List<Rocket> getDefectRockets() {
		return defectRockets;
	}

	public void setDefectRockets(List<Rocket> defectRockets) {
		this.defectRockets = defectRockets;
	}

	public List<Rocket> getGoodRockets() {
		return goodRockets;
	}

	public void setGoodRockets(List<Rocket> goodRockets) {
		this.goodRockets = goodRockets;
	}

	public List<Rocket> getProducedRockets() {
		return producedRockets;
	}

	public void setProducedRockets(List<Rocket> producedRockets) {
		this.producedRockets = producedRockets;
	}

	public List<RocketPackage> getPackages() {
		return packages;
	}

	public void setPackages(List<RocketPackage> packages) {
		this.packages = packages;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public int geteLRed() {
		return eLRed;
	}

	public void seteLRed(int eLRed) {
		this.eLRed = eLRed;
	}

	public int geteLGreen() {
		return eLGreen;
	}

	public void seteLGreen(int eLGreen) {
		this.eLGreen = eLGreen;
	}

	public int geteLBlue() {
		return eLBlue;
	}

	public void seteLBlue(int eLBlue) {
		this.eLBlue = eLBlue;
	}

}
