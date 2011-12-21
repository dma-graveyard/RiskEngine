package dk.sfs.riskengine.metoc;

import org.apache.commons.lang.builder.EqualsBuilder;

import dk.sfs.riskengine.geometry.Point2d;
import dk.sfs.riskengine.persistence.domain.DepthPoint;

public class MetocKey {

	
	private Double lat;
	private Double lon;
	private long time;
	
	public MetocKey(Double lat, Double lon, long time) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.time = time;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MetocKey){
			MetocKey tmp = (MetocKey) obj;
			Point2d me = new Point2d();
			me.setLatLon(lon, lat);
			// Metoc within 500m and 10 min are equals
			return me.distanceLatLon(tmp.lat, tmp.lon)<250 && Math.abs(time-tmp.time)<1000l*60l*10l ;  
		}
			
		
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	
	
	
	
}
