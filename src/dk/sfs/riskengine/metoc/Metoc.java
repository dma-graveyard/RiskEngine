package dk.sfs.riskengine.metoc;

import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.ShoreServiceResponse;
import dk.frv.enav.common.xml.Waypoint.Heading;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequest;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequestWp;
import dk.frv.enav.common.xml.metoc.response.MetocForecastResponse;

public class Metoc {

	private static final Logger log = Logger.getLogger(Metoc.class);
	private double windDirection;
	private double windSpeed;
	private double currentDirection;
	private double currentSpeed;
	private double visibility;

	public Metoc() {
		super();
	}

	public static Metoc getForPosition(GeoLocation pos) throws Exception {

		MetocForecastRequestWp reqWp = new MetocForecastRequestWp();
		reqWp.setEta(new Date());
		reqWp.setHeading(Heading.RL.name());
		reqWp.setLat(pos.getLatitude());
		reqWp.setLon(pos.getLongitude());
		MetocForecastRequest req = new MetocForecastRequest();
		req.getWaypoints().add(reqWp);

		// Make request
		MetocForecastResponse res = (MetocForecastResponse) makeRequest("/api/xml/routeMetoc",
				"dk.frv.enav.common.xml.metoc.request", "dk.frv.enav.common.xml.metoc.response", req);

		Iterator<MetocForecastPoint> iter = res.getMetocForecast().getForecasts().iterator();
		MetocForecastPoint point = null;
		
		if(iter.hasNext()){
			point = iter.next();
			Metoc metoc = new Metoc();
			metoc.setCurrentDirection(point.getCurrentDirection());
			
		}
		
		
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

	public void setWindDirection(double windDirection) {
		this.windDirection = windDirection;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public double getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(double currentDirection) {
		this.currentDirection = currentDirection;
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(double currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public double getVisibility() {
		return visibility;
	}

	public void setVisibility(double visibility) {
		this.visibility = visibility;
	}

	private static ShoreServiceResponse makeRequest(String uri, String reqContextPath, String resContextPath, Object request)
			throws Exception {
		// Create HTTP request
		ShoreHttp shoreHttp = new ShoreHttp(uri);
		// Init HTTP
		shoreHttp.init();
		// Set content
		try {
			shoreHttp.setXmlMarshalContent(reqContextPath, request);
		} catch (Exception e) {
			log.error("Failed to make XML request: " + e.getMessage(),e);
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
