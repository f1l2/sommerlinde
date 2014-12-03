package at.tuwien.sbcm.qsupervisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.sbcm.factory.FactoryCore;

public class QSupervisor {

	private static final Logger logger = LoggerFactory.getLogger(QSupervisor.class);

	private int employeeId;

	public static void main(String[] args) {
		new QSupervisor();
	}

	public QSupervisor() {

		FactoryCore.initSpace(Boolean.FALSE);

		// first receive producer id
		this.employeeId = FactoryCore.getIDAndIncr(FactoryCore.PRODUCER_COUNTER);

		this.check();
	}

	public void check() {

	}
}