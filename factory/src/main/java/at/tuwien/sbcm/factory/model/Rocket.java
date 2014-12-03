package at.tuwien.sbcm.factory.model;

import java.io.Serializable;

public class Rocket implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer producerId;

	private Boolean isDefect;

	private Boolean readyForPickUP;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProducerId() {
		return producerId;
	}

	public void setProducerId(Integer producerId) {
		this.producerId = producerId;
	}

	public Boolean getReadyForPickUP() {
		return readyForPickUP;
	}

	public void setReadyForPickUP(Boolean readyForPickUP) {
		this.readyForPickUP = readyForPickUP;
	}

	public Boolean getIsDefect() {
		return isDefect;
	}

	public void setIsDefect(Boolean isDefect) {
		this.isDefect = isDefect;
	}

}
