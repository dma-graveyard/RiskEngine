package dk.sfs.riskengine.persistence.domain;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import dk.sfs.riskengine.persistence.mapper.DBSessionFactory;
import dk.sfs.riskengine.persistence.mapper.RiskIndexesMapper;
import dk.sfs.riskengine.persistence.mapper.VesselMapper;

public class RiskIndexes {
	private Integer id;

	private Integer mmsi;
	private Double collision;
	private Double fireExplosion;
	private Double foundering;
	private Double hullFailure;
	private Double machineryFailure;
	private Double strandedByMachineFailure;
	private Double strandedByNavigationError;
	private Date dateTimeCreated;

	private Vessel staticInfo = new Vessel();
	
	private Long cpaTargetMmsi;
	private Double cpaTime;
	private Double cpaDist;
	private Double sog;
	private Double cog;
	
	private double lat;
	private double lon;
	
	private Double cpaSog;
	private Double cpaCog;
	
	private double cpaLat;
	private double cpaLon;

	
	public static List<RiskIndexes> selectLatest() {
		SqlSession sess = DBSessionFactory.getSession();
		try {
			return sess.getMapper(RiskIndexesMapper.class).selectLatest();
		} finally {
			sess.close();
		}
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMmsi() {
		return mmsi;
	}

	public void setMmsi(Integer mmsi) {
		this.mmsi = mmsi;
	}

	public Double getCollision() {
		return collision;
	}

	public void setCollision(Double collision) {
		this.collision = collision;
	}

	public Double getFireExplosion() {
		return fireExplosion;
	}

	public void setFireExplosion(Double fireExplosion) {
		this.fireExplosion = fireExplosion;
	}

	public Double getFoundering() {
		return foundering;
	}

	public void setFoundering(Double foundering) {
		this.foundering = foundering;
	}

	public Double getHullFailure() {
		return hullFailure;
	}

	public void setHullFailure(Double hullFailure) {
		this.hullFailure = hullFailure;
	}

	public Double getMachineryFailure() {
		return machineryFailure;
	}

	public void setMachineryFailure(Double machineryFailure) {
		this.machineryFailure = machineryFailure;
	}

	public Double getStrandedByMachineFailure() {
		return strandedByMachineFailure;
	}

	public void setStrandedByMachineFailure(Double strandedByMachineFailure) {
		this.strandedByMachineFailure = strandedByMachineFailure;
	}

	public Double getStrandedByNavigationError() {
		return strandedByNavigationError;
	}

	public void setStrandedByNavigationError(Double strandedByNavigationError) {
		this.strandedByNavigationError = strandedByNavigationError;
	}

	

	public Date getDateTimeCreated() {
		return dateTimeCreated;
	}

	public void setDateTimeCreated(Date dateTimeCreated) {
		this.dateTimeCreated = dateTimeCreated;
	}

	
	public void save(){
		SqlSession sess = DBSessionFactory.getSession();
		try{
			sess.getMapper(RiskIndexesMapper.class).insert(this);
		}finally{
			sess.close();
		}
	}

	public String getShipType() {
		return staticInfo.getShipTypeIwrap();
	}

	public void setShipType(String shipType) {
		staticInfo.setShipTypeIwrap(shipType);
	}

	public Double getLength() {
		return staticInfo.getLength();
	}

	public void setLength(Double length) {
		staticInfo.setLength(length);
	}

	public Double getDraught() {
		return staticInfo.getDraught();
	}

	public void setDraught(Double draught) {
		staticInfo.setDraught(draught);
	}

	public Integer getYearOfBuild() {
		return staticInfo.getYearOfBuild();
	}

	public void setYearOfBuild(Integer yearOfBuild) {
		staticInfo.setYearOfBuild(yearOfBuild);
	}

	public String getFlag() {
		return staticInfo.getFlag();
	}

	public void setFlag(String flag) {
		staticInfo.setFlag(flag);
	}

	public void setStaticInfo(Vessel staticInfo) {
		this.staticInfo = staticInfo;
	}

	
	public Long getCpaTargetMmsi() {
		return cpaTargetMmsi;
	}

	public void setCpaTargetMmsi(Long cpaTargetMmsi) {
		this.cpaTargetMmsi = cpaTargetMmsi;
	}

	public Double getCpaTime() {
		return cpaTime;
	}

	public void setCpaTime(Double cpaTime) {
		this.cpaTime = cpaTime;
	}

	public Double getSog() {
		return sog;
	}

	public void setSog(Double sog) {
		this.sog = sog;
	}

	public Double getCog() {
		return cog;
	}

	public void setCog(Double cog) {
		this.cog = cog;
	}

	public Double getCpaDist() {
		return cpaDist;
	}

	public void setCpaDist(Double cpaDist) {
		this.cpaDist = cpaDist;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getNameOfShip() {
		return staticInfo.getNameOfShip();
	}

	public void setNameOfShip(String nameOfShip) {
		staticInfo.setNameOfShip(nameOfShip);
	}

	public Double getCpaSog() {
		return cpaSog;
	}

	public void setCpaSog(Double cpaSog) {
		this.cpaSog = cpaSog;
	}

	public Double getCpaCog() {
		return cpaCog;
	}

	public void setCpaCog(Double cpaCog) {
		this.cpaCog = cpaCog;
	}

	public double getCpaLat() {
		return cpaLat;
	}

	public void setCpaLat(double cpaLat) {
		this.cpaLat = cpaLat;
	}

	public double getCpaLon() {
		return cpaLon;
	}

	public void setCpaLon(double cpaLon) {
		this.cpaLon = cpaLon;
	}
	
	
}