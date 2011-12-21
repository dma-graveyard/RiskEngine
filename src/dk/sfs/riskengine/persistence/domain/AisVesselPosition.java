package dk.sfs.riskengine.persistence.domain;

import java.util.Date;

public class AisVesselPosition {

	private Integer mmsi;
	private Double lat;
	private Double lon;
	private Byte posAcc;
	private Double sog;
	private Double cog;
	private Double heading;
	private Byte raim;
	private Byte utcSec;
	private Date sourceTimestamp;
	private Date received;
	private Date created;

	public Integer getMmsi() {
		return mmsi;
	}

	public void setMmsi(Integer mmsi) {
		this.mmsi = mmsi;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Byte getPosAcc() {
		return posAcc;
	}

	public void setPosAcc(Byte posAcc) {
		this.posAcc = posAcc;
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

	public Double getHeading() {
		return heading;
	}

	public void setHeading(Double heading) {
		this.heading = heading;
	}

	public Byte getRaim() {
		return raim;
	}

	public void setRaim(Byte raim) {
		this.raim = raim;
	}

	public Byte getUtcSec() {
		return utcSec;
	}

	public void setUtcSec(Byte utcSec) {
		this.utcSec = utcSec;
	}

	public Date getSourceTimestamp() {
		return sourceTimestamp;
	}

	public void setSourceTimestamp(Date sourceTimestamp) {
		this.sourceTimestamp = sourceTimestamp;
	}

	public Date getReceived() {
		return received;
	}

	public void setReceived(Date received) {
		this.received = received;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}