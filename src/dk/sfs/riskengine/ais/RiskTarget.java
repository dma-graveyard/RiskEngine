package dk.sfs.riskengine.ais;

import org.apache.ibatis.session.SqlSession;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage18;
import dk.frv.ais.message.AisPosition;
import dk.sfs.riskengine.persistence.domain.Vessel;
import dk.sfs.riskengine.persistence.mapper.DBSessionFactory;
import dk.sfs.riskengine.persistence.mapper.VesselMapper;

public class RiskTarget {

	private static final long CAL_PERIOD = 5*60l*1000l; // 1 min

	private long mmsi;
	private Vessel vessel;
	private AisPosition pos;
	private int cog;
	private int sog;
	private long lastUpdated = 0;
	
	
	public RiskTarget(AisMessage18 msg) {
		super();
		mmsi = msg.getUserId();
		pos=msg.getPos();
		SqlSession sess= DBSessionFactory.getSession();
		try{
		sess.getMapper(VesselMapper.class).selectByMmsi(mmsi);
		}
		finally{
			sess.close();
		}
		cog=msg.getCog();
		sog=msg.getSog();
	}

	public void setPos(AisPosition pos) {
		this.pos = pos;
	}

	public String getShipTypeIwrap() {
		return vessel.getShipTypeIwrap();
	}

	public Double getLength() {
		return vessel.getLength();
	}

	public Double getDraught() {
		return vessel.getDraught();
	}

	public Integer getYearOfBuild() {
		return vessel.getYearOfBuild();
	}

	public long getLongitude() {
		return pos.getLongitude();
	}

	public long getLatitude() {
		return pos.getLatitude();
	}

	public GeoLocation getGeoLocation() {
		return pos.getGeoLocation();
	}

	public String getFlag() {
		return vessel.getFlag();
	}

	public int getSog() {
		return sog;
	}

	public void setSog(int sog) {
		this.sog = sog;
	}

	public GeoLocation getPos() {
		return pos.getGeoLocation();
	}

	public int getCog() {
		return cog;
	}

	public void setCog(int cog) {
		this.cog = cog;
	}
	
	public boolean timeToUpdate(){
		return System.currentTimeMillis() - lastUpdated > CAL_PERIOD;
	}
		
	
	
	
	
}
