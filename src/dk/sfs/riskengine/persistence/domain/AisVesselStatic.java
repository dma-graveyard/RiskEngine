package dk.sfs.riskengine.persistence.domain;

import org.apache.ibatis.session.SqlSession;

import dk.sfs.riskengine.persistence.mapper.AisVesselStaticMapper;
import dk.sfs.riskengine.persistence.mapper.DBSessionFactory;


public class AisVesselStatic {
  
    private Integer mmsi;
    private String name;
    private Integer shipType;
    private Integer cargo;
    private Integer dimBow;
    private Integer dimStern;
    private Integer dimPort;
    private Integer dimStarboard;

    private Long imo;
    private Integer draught;
    
    public static AisVesselStatic findByMmsi(Long mmsi) {

		SqlSession sess = DBSessionFactory.getSession();

		try {
			AisVesselStaticMapper mapper = sess.getMapper(AisVesselStaticMapper.class);
			
			return mapper.selectByPrimaryKey(mmsi);
		} finally {
			sess.close();
		}

	}
    
    
	public Integer getMmsi() {
		return mmsi;
	}
	public void setMmsi(Integer mmsi) {
		this.mmsi = mmsi;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getShipType() {
		return shipType;
	}
	public void setShipType(Integer shipType) {
		this.shipType = shipType;
	}
	public Integer getCargo() {
		return cargo;
	}
	public void setCargo(Integer cargo) {
		this.cargo = cargo;
	}
	public Integer getDimBow() {
		return dimBow;
	}
	public void setDimBow(Integer dimBow) {
		this.dimBow = dimBow;
	}
	public Integer getDimStern() {
		return dimStern;
	}
	public void setDimStern(Integer dimStern) {
		this.dimStern = dimStern;
	}
	public Long getImo() {
		return imo;
	}
	public void setImo(Long imo) {
		this.imo = imo;
	}
	public Integer getDraught() {
		return draught;
	}
	public void setDraught(Integer draught) {
		this.draught = draught;
	}


	public Integer getDimPort() {
		return dimPort;
	}


	public void setDimPort(Integer dimPort) {
		this.dimPort = dimPort;
	}


	public Integer getDimStarboard() {
		return dimStarboard;
	}


	public void setDimStarboard(Integer dimStarboard) {
		this.dimStarboard = dimStarboard;
	}

    
    
}