package sbcm.factory.model;

import java.io.Serializable;

import org.mozartspaces.capi3.Queryable;

@Queryable(autoindex = true)
public enum QualityCategory implements Serializable {

	A, B, DEFEKT, UNKNOWN
	
}
