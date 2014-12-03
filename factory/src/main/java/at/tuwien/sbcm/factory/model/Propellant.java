package at.tuwien.sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

@Queryable(autoindex = true)
public class Propellant implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
