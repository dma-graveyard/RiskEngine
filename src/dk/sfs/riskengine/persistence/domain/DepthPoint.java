package dk.sfs.riskengine.persistence.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import dk.frv.ais.geo.GeoLocation;
import dk.sfs.riskengine.geometry.Geofunctions;
import dk.sfs.riskengine.persistence.mapper.DBSessionFactory;
import dk.sfs.riskengine.persistence.mapper.DepthPointMapper;

public class DepthPoint {
	private Integer id;
	private Double lat;
	private Double lon;
	private Integer n;
	private Integer m;
	private Double depth;

	public static DepthPoint findClosestDeepPoint(GeoLocation pos) {

		SqlSession sess = DBSessionFactory.getSession();

		try {
			DepthPointMapper mapper = sess.getMapper(DepthPointMapper.class);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("lat", pos.getLatitude());
			map.put("lon", pos.getLongitude());
			return mapper.findClosestDeepPoint(map);
		} finally {
			sess.close();
		}

	}

	public static DepthPoint findGroundingPoint(GeoLocation pos, double heading, double draught) {

		SqlSession sess = DBSessionFactory.getSession();
		try {

			DepthPointMapper mapper = sess.getMapper(DepthPointMapper.class);
			/*
			 * Find closest deep point.
			 */
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("lat", pos.getLatitude());
			map.put("lon", pos.getLongitude());
			DepthPoint p = mapper.findClosestDeepPoint(map);

			/*
			 * Find first grounding point in direction
			 */

			double angle = Geofunctions.d2r(Geofunctions.compass2cartesian(heading));

			Long ratioM = Math.round(Math.sin(angle) * 10l);
			Long ratioN = Math.round(Math.cos(angle) * 10l);

			map.put("m", p.getM());
			map.put("n", p.getN());
			map.put("ratioM", ratioM);
			map.put("ratioN", ratioN);
			map.put("depth", -draught);

			DepthPoint ground = mapper.findGroundingPointIndices(map);
			return mapper.selectByIndices(ground);
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

	public Integer getN() {
		return n;
	}

	public void setN(Integer n) {
		this.n = n;
	}

	public Integer getM() {
		return m;
	}

	public void setM(Integer m) {
		this.m = m;
	}

	public Double getDepth() {
		return depth;
	}

	public void setDepth(Double depth) {
		this.depth = depth;
	}

}