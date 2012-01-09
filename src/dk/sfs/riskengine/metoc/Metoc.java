package dk.sfs.riskengine.metoc;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.ShoreServiceResponse;
import dk.frv.enav.common.xml.Waypoint.Heading;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequest;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequestWp;
import dk.frv.enav.common.xml.metoc.response.MetocForecastResponse;
import dk.frv.enav.common.xml.metoc.single.request.SinglePointMetocRequest;
import dk.frv.enav.common.xml.metoc.single.response.SinglePointMetocResponse;

public class Metoc {

	private static final Logger log = Logger.getLogger(Metoc.class);
	private double windDirection;
	private double windSpeed;
	private double currentDirection;
	private double currentSpeed;
	private double visibility;
	private double waweHeight;
	private static final Map<MetocKey, Metoc> metocMap = new HashMap<MetocKey, Metoc>();
	private static final Object mutex = new Object();

	public Metoc() {
		super();
	}

	public static void main(String[] args) throws Exception {

		getMetocForPosition(new GeoLocation(55.870717, 12.70195));
	}

	/**
	 * Get the metoc information for the position.
	 * 
	 * @param pos
	 * @return
	 * @throws Exception
	 */
	public static Metoc getMetocForPosition(GeoLocation pos) {
		/*
		 * check in cache
		 */
		MetocKey key = new MetocKey(pos.getLatitude(), pos.getLongitude(), System.currentTimeMillis());
		Metoc metoc = metocMap.get(key);
		if (metoc != null) {
			return metoc;
		}
		/*
		 * Not in cache, initiate service request
		 */
		synchronized (mutex) {
			metoc = metocMap.get(key);
			if (metoc != null) {
				// inserted in cache by another thread
				return metoc;
			}

			SinglePointMetocResponse res;
			try {
				res = (SinglePointMetocResponse) makeRequest("/api/xml/singlePointMetoc",
						"dk.frv.enav.common.xml.metoc.single.request", "dk.frv.enav.common.xml.metoc.single.response",
						new SinglePointMetocRequest(pos));
				MetocForecastPoint point = res.getMetocPoint();

				if (point != null) {
					metoc = new Metoc();
					if (point.getCurrentDirection() != null) {
						metoc.currentDirection = point.getCurrentDirection().getForecast();
					}
					if (point.getCurrentSpeed() != null) {
						metoc.currentSpeed = point.getCurrentSpeed().getForecast();
					}
					if (point.getMeanWaveHeight() != null) {
						metoc.waweHeight = point.getMeanWaveHeight().getForecast();
					}
					metoc.windDirection = point.getWindDirection().getForecast();
					metoc.windSpeed = point.getWindSpeed().getForecast();
					
					metocMap.put(key, metoc);
					return metoc;

				} else {
					log.warn("Ingen metoc data for lat :" + pos.getLatitude() + " lon: " + pos.getLongitude());
				}
			} catch (Exception e) {
				log.error(
						"Problem requesting single point metoc for lat :" + pos.getLatitude() + " lon: "
								+ pos.getLongitude(), e);
			}
		}
		return null;

	}

	public Metoc(double windDirection, double windSpeed, double currentDirection, double currentSpeed) {
		super();
		this.windDirection = windDirection;
		this.windSpeed = windSpeed;
		this.currentDirection = currentDirection;
		this.currentSpeed = currentSpeed;
	}

	public double getWindDirection() {
		return windDirection;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public double getCurrentDirection() {
		return currentDirection;
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	public double getVisibility() {
		return visibility;
	}

	public double getWaweHeight() {
		return waweHeight;
	}

	private static ShoreServiceResponse makeRequest(String uri, String reqContextPath, String resContextPath,
			Object request) throws Exception {
		// Create HTTP request
		ShoreHttp shoreHttp = new ShoreHttp(uri);
		// Init HTTP
		shoreHttp.init();
		// Set content
		try {
			shoreHttp.setXmlMarshalContent(reqContextPath, request);
		} catch (Exception e) {
			log.error("Failed to make XML request: " + e.getMessage(), e);
			throw new Exception("Internal error", e);
		}

		// Make request
		try {
			shoreHttp.makeRequest();
		} catch (Exception e) {
			throw e;
		}

		ShoreServiceResponse res;
		try {
			Object resObj = shoreHttp.getXmlUnmarshalledContent(resContextPath);
			res = (ShoreServiceResponse) resObj;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to unmarshal XML response: " + e.getMessage());
			throw new Exception("Invalid response", e);
		}

		// Report if an error response
		if (res.getErrorCode() != 0) {
			throw new Exception("Service Error : " + res.getErrorMessage());
		}

		return res;
	}

}
