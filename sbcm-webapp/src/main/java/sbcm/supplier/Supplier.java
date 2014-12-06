package sbcm.supplier;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbcm.factory.FactoryCore;
import sbcm.factory.model.EffectiveLoad;
import sbcm.factory.model.Igniter;
import sbcm.factory.model.Propellant;
import sbcm.factory.model.WoodenStaff;

@ManagedBean(name = "supplier")
@ViewScoped
public class Supplier {

	private static final Logger logger = LoggerFactory.getLogger(Supplier.class);

	private int producerId;

	private int propDefect = 10;

	private String name;
	private Integer woodenstaff;
	private Integer igniter;
	private Integer propellant;
	private Integer effectLoad;

	public int getProducerId() {
		return producerId;
	}

	public void setProducerId(int producerId) {
		this.producerId = producerId;
	}

	public int getPropDefect() {
		return propDefect;
	}

	public void setPropDefect(int propDefect) {
		this.propDefect = propDefect;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getWoodenstaff() {
		return woodenstaff;
	}

	public void setWoodenstaff(Integer woodenstaff) {
		this.woodenstaff = woodenstaff;
	}

	public Integer getIgniter() {
		return igniter;
	}

	public void setIgniter(Integer igniter) {
		this.igniter = igniter;
	}

	public Integer getPropellant() {
		return propellant;
	}

	public void setPropellant(Integer propellant) {
		this.propellant = propellant;
	}

	public Integer getEffectLoad() {
		return effectLoad;
	}

	public void setEffectLoad(Integer effectLoad) {
		this.effectLoad = effectLoad;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void main(String[] args) {

		new Supplier();

		System.exit(0);
	}

	public Supplier() {

	}

	public void deliver() {

		FactoryCore.initSpace(Boolean.FALSE);

		// first receive producer id
		this.producerId = FactoryCore.getIDAndIncr(FactoryCore.PRODUCER_COUNTER);

		logger.info(this.producerId + " started.");

		for (int i = 1; i <= this.igniter; i++) {

			Igniter ig = new Igniter();
			ig.setId(FactoryCore.getIDAndIncr(FactoryCore.PART_COUNTER));

			try {
				FactoryCore.write(FactoryCore.PARTS, new Entry(ig));
			} catch (MzsCoreException e) {
				e.printStackTrace();
			}

		}

		for (int i = 1; i <= this.propellant; i++) {

			Propellant p = new Propellant();
			p.setId(FactoryCore.getIDAndIncr(FactoryCore.PART_COUNTER));
			p.setAmount(500);

			try {
				FactoryCore.write(FactoryCore.PARTS, new Entry(p));
			} catch (MzsCoreException e) {
				e.printStackTrace();
			}

		}

		for (int i = 1; i <= this.woodenstaff; i++) {

			WoodenStaff w = new WoodenStaff();
			w.setId(FactoryCore.getIDAndIncr(FactoryCore.PART_COUNTER));

			try {
				FactoryCore.write(FactoryCore.PARTS, new Entry(w));
			} catch (MzsCoreException e) {
				e.printStackTrace();
			}
		}

		for (int i = 1; i <= this.effectLoad; i++) {

			EffectiveLoad effectLoad = new EffectiveLoad();
			effectLoad.setId(FactoryCore.getIDAndIncr(FactoryCore.PART_COUNTER));
			effectLoad.setIsDefect(FactoryCore.isDefectRandom(this.propDefect));

			try {
				FactoryCore.write(FactoryCore.PARTS, new Entry(effectLoad));
			} catch (MzsCoreException e) {
				e.printStackTrace();
			}
		}

	}
}
