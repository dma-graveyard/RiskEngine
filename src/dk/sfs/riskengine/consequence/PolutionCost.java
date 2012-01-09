package dk.sfs.riskengine.consequence;

public class PolutionCost {

	//This formula does not distinques between different oil types
	//spillsize is in tonnes
	//Potential for improving on this. Lots of literature out there. 
	//How much reaches the shore and how much can be picked up at sea?
	public static double totalEstimate(double spillsize) {
		if (spillsize<0.0) return 0.0;
		double totalCost=51432* Math.pow(spillsize,0.728); //An empirical analysis of IOPCF oil spill cost data for US
		totalCost*=0.5; //Seems a bit high so we divide by 2
		totalCost*=1.0e-6;	//To convert to million US$
		return totalCost;
	}
}
