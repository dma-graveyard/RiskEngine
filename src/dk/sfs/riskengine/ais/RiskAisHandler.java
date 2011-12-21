package dk.sfs.riskengine.ais;

import java.util.Date;

import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.proprietary.GatehouseFactory;
import dk.frv.ais.reader.RoundRobinAisTcpReader;
import dk.frv.ais.utils.filter.FilterSettings;
import dk.frv.ais.utils.filter.MessageHandler;
import dk.sfs.riskengine.geometry.CPA;

public class RiskAisHandler {

	public static void main(String[] args) {

		RoundRobinAisTcpReader aisReader = new RoundRobinAisTcpReader();
		aisReader.setCommaseparatedHostPort("ais43.sealan.dk:4712");
		// aisReader.setCommaseparatedHostPort("192.168.10.250:4001");

		aisReader.setTimeout(5);

		IAisHandler messageHandler = new RiskMessageHandler(54.75, 56.0, 10.65, 11.25);
		CPA.setSphereCentralPoint(11.0, 55.5);
		IAisHandler handler = (IAisHandler) messageHandler;

		// Register handler
		aisReader.registerHandler(handler);

		// Register proprietary handler (optional)
		// aisReader.addProprietaryFactory(new GatehouseFactory());

		// Start reader thread
		Date start = new Date();
		aisReader.start();

		while (true) {

		}
	}

}
