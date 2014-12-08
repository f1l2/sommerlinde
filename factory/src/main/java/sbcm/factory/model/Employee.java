package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

@Queryable(autoindex = true)
public class Employee implements Serializable {

	private static final long serialVersionUID = 2249317144484146788L;

	private Integer id;

	public Employee() {

	}

	public Employee(Integer id) {
		this.setId(id);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
