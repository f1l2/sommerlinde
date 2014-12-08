package sbcm.supplier;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.mozartspaces.capi3.LindaCoordinator;
import org.mozartspaces.capi3.LindaCoordinator.LindaSelector;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbcm.factory.FactoryCore;
import sbcm.factory.model.EffectiveLoad;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.Rocket;
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

	public List<Igniter> getlIgniter() {
		return lIgniter;
	}

	public static void main(String[] args) {

		new Report();

		System.exit(0);
	}

	public Report() {

		FactoryCore.initSpace(Boolean.FALSE);

		// first receive producer id
		this.generateReport();
	}

	public void generateReport() {

		logger.info("-------- REPORT ---------- ");

		LindaSelector igniterSelector = LindaCoordinator.newSelector(new Igniter(), LindaSelector.COUNT_MAX);
		LindaSelector propellantSelector = LindaCoordinator.newSelector(new Propellant(), LindaSelector.COUNT_MAX);
		LindaSelector woodenStaffSelector = LindaCoordinator.newSelector(new WoodenStaff(), LindaSelector.COUNT_MAX);
		LindaSelector effectiveLoadSelector = LindaCoordinator.newSelector(new EffectiveLoad(), LindaSelector.COUNT_MAX);
		LindaSelector producedRocket = LindaCoordinator.newSelector(new Rocket(), LindaSelector.COUNT_MAX);

		try {
			ContainerReference container = FactoryCore.getOrCreateNamedContainer(FactoryCore.PARTS);

			this.lIgniter = FactoryCore.CAPI.read(container, igniterSelector, new Long(0), null);
			logger.info("# Igniter: " + lIgniter.size());

			this.lPropellant = FactoryCore.CAPI.read(container, propellantSelector, new Long(0), null);
			logger.info("# Propellant: " + lPropellant.size());
			for (Propellant propellant : lPropellant) {
				logger.info("- ID:" + propellant.getId() + "; Amount: " + propellant.getAmount());
			}

			this.lWoodenStaff = FactoryCore.CAPI.read(container, woodenStaffSelector, new Long(0), null);
			logger.info("# WoodenStaff: " + lWoodenStaff.size());

			this.lEffectLoad = FactoryCore.CAPI.read(container, effectiveLoadSelector, new Long(0), null);
			logger.info("# EffectiveLoad: " + lEffectLoad.size());

			container = FactoryCore.getOrCreateNamedContainer(FactoryCore.PRODUCED_ROCKETS);
			this.producedRockets = FactoryCore.CAPI.read(container, producedRocket, new Long(0), null);
			logger.info("# Produced rockets: " + this.producedRockets.size());

			container = FactoryCore.getOrCreateNamedContainer(FactoryCore.GOOD_ROCKETS);
			this.goodRockets = FactoryCore.CAPI.read(container, producedRocket, new Long(0), null);
			logger.info("# Good rockets: " + this.goodRockets.size());

			container = FactoryCore.getOrCreateNamedContainer(FactoryCore.DEFECT_ROCKETS);
			this.defectRockets = FactoryCore.CAPI.read(container, producedRocket, new Long(0), null);
			logger.info("# Defect rockets: " + this.defectRockets.size());

		} catch (MzsCoreException e) {
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

	public List<EffectiveLoad> getlEffectLoad() {
		return lEffectLoad;
	}

	public void setlEffectLoad(List<EffectiveLoad> lEffectLoad) {
		this.lEffectLoad = lEffectLoad;
	}

	private List<EffectiveLoad> lEffectLoad;

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

}
