package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

@Queryable(autoindex = true)
public enum OrderStatus implements Serializable {

	SCHEDULED, PROCESSED, NOT_DELIVERED, DELIVERED

}
