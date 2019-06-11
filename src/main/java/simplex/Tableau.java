package simplex;

import java.util.LinkedList;
import java.util.List;

public class Tableau {

	private double[] zFunction;
	
	private List< double[] > restrictions;
	
	public Tableau() {}

	public double[] getZFunction() {
		return zFunction;
	}

	public void setZFunction( double[] zFunction ) {
		this.zFunction = zFunction;
	}

	public List< double[] > getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List< double[] > restrictions) {
		this.restrictions = restrictions;
	}
	
	public void addRestriction( double[] restriction ) {
		if( this.restrictions == null ) {
			this.restrictions = new LinkedList< double[] >();
		}
		this.restrictions.add( restriction );
	}
	
}
