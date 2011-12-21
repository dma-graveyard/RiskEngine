package dk.sfs.riskengine.persistence.domain;


public class VesselStaticInfo {
	
	private double draught;
	private double length;
	private int yearOfBuild;
	private long imo;
	private String shiptype;
	private String flag;
	
	public double getDraught() {
		return draught;
	}
	public void setDraught(double draught) {
		
		this.draught = draught;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public int getYearOfBuild() {
		return yearOfBuild;
	}
	public void setYearOfBuild(int yearBuild) {
		this.yearOfBuild = yearBuild;
	}
	public long getImo() {
		return imo;
	}
	public void setImo(long imo) {
		this.imo = imo;
	}
	public String getShiptype() {
		return shiptype;
	}
	public void setShiptype(String shiptype) {
		this.shiptype = shiptype;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
}
