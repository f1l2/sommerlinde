package sbcm.supplier;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.LindaCoordinator.LindaSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbc.space.MozartContainer;
import sbc.space.MozartSpaces;
import sbcm.factory.model.EffectLoad;
import sbcm.factory.model.Igniter;
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
	private List<Igniter> lIgniter;
	private List<Propellant> lPropellant;
	private List<WoodenStaff> lWoodenStaff;
	private List<RocketPackage> packages;

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

		LindaSelector igniterSelector = LindaCoordinator.newSelector(new Igniter(), LindaSelector.COUNT_MAX);
		LindaSelector propellantSelector = LindaCoordinator.newSelector(new Propellant(), LindaSelector.COUNT_MAX);
		LindaSelector woodenStaffSelector = LindaCoordinator.newSelector(new WoodenStaff(), LindaSelector.COUNT_MAX);
		LindaSelector effectLoadSelector = LindaCoordinator.newSelector(new EffectLoad(), LindaSelector.COUNT_MAX);
		LindaSelector rocketSelector = LindaCoordinator.newSelector(new Rocket(), LindaSelector.COUNT_MAX);
		LindaSelector packageSelector = LindaCoordinator.newSelector(new RocketPackage(), LindaSelector.COUNT_MAX);

		try {

			MozartContainer mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PARTS);

			this.lIgniter = this.mozartSpaces.read(mc, null, igniterSelector, 1000);
			logger.info("# Igniter: " + lIgniter.size());

			this.lPropellant = this.mozartSpaces.read(mc, null, propellantSelector, 1000);
			logger.info("# Propellant: " + lPropellant.size());
			for (Propellant propellant : lPropellant) {
				logger.info("- ID:" + propellant.getId() + "; Amount: " + propellant.getAmount());
			}

			this.lWoodenStaff = this.mozartSpaces.read(mc, null, woodenStaffSelector, 1000);
			logger.info("# WoodenStaff: " + lWoodenStaff.size());

			this.lEffectLoad = this.mozartSpaces.read(mc, null, effectLoadSelector, 1000);
			logger.info("# EffectiveLoad: " + lEffectLoad.size());

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.PRODUCED_ROCKETS);
			this.producedRockets = this.mozartSpaces.read(mc, null, rocketSelector, 1000);
			logger.info("# Produced rockets: " + this.producedRockets.size());

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.GOOD_ROCKETS);
			this.goodRockets = this.mozartSpaces.read(mc, null, rocketSelector, 1000);
			logger.info("# Good rockets: " + this.goodRockets.size());

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.DEFECT_ROCKETS);
			this.defectRockets = this.mozartSpaces.read(mc, null, rocketSelector, 1000);
			logger.info("# Defect rockets: " + this.defectRockets.size());

			mc = (MozartContainer) this.mozartSpaces.findContainer(MozartSpaces.ROCKET_PACKAGES);
			this.packages = this.mozartSpaces.read(mc, null, packageSelector, 1000);
			logger.info("# Packages: " + this.packages.size());

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

	private List<EffectLoad> lEffectLoad;

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

}
