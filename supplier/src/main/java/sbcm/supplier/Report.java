package sbcm.supplier;

import java.util.ArrayList;

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

public class Report {

	private static final Logger logger = LoggerFactory.getLogger(Report.class);

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

			ArrayList<Igniter> listI = FactoryCore.CAPI.read(container, igniterSelector, new Long(0), null);
			logger.info("# Igniter: " + listI.size());

			ArrayList<Propellant> plist = FactoryCore.CAPI.read(container, propellantSelector, new Long(0), null);
			logger.info("# Propellant: " + plist.size());
			for (Propellant propellant : plist) {
				logger.info("- ID:" + propellant.getId() + "; Amount: " + propellant.getAmount());
			}

			ArrayList<WoodenStaff> wlist = FactoryCore.CAPI.read(container, woodenStaffSelector, new Long(0), null);
			logger.info("# WoodenStaff: " + wlist.size());

			ArrayList<EffectiveLoad> elist = FactoryCore.CAPI.read(container, effectiveLoadSelector, new Long(0), null);
			logger.info("# EffectiveLoad: " + elist.size());

			container = FactoryCore.getOrCreateNamedContainer(FactoryCore.PRODUCED_ROCKETS);
			ArrayList<Rocket> prlist = FactoryCore.CAPI.read(container, producedRocket, new Long(0), null);
			logger.info("# Produced rockets: " + prlist.size());

		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("-------- END REPORT ---------- ");
	}

}
