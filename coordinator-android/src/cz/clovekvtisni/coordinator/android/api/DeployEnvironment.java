package cz.clovekvtisni.coordinator.android.api;

import android.util.Log;

/**
 * FIXME: spravne hodnoty podle reality
 * 
 * Popis prostredi Metis3 - testovani/produkcni. Vsechny mozne konstanty buildu dame sem.
 * 
 * @author tomucha
 */
public class DeployEnvironment {

	private static final String TAG = "Environment";
	private String apiHost = null;
	private int apiPort = 0;
	private int logLevel = 0;
	private String apiVersion = "v1";

	private DeployEnvironment() {
	}

	public static DeployEnvironment getProductionEnvironment() {
		DeployEnvironment e = new DeployEnvironment();
		e.apiHost = "???";
		e.apiPort = 44443;
		e.logLevel = Log.INFO;
		return e;
	}

	public static DeployEnvironment getTestingEnvironment() {
		DeployEnvironment e = new DeployEnvironment();
		e.apiHost = "coordinator-test.appspot.com";
		e.apiPort = 443;
		e.logLevel = Log.DEBUG;
		return e;
	}

	public String getApiHost() {
		return apiHost;
	}

	public int getApiPort() {
		return apiPort;
	}

	public int getLogLevel() {
		return logLevel;
	}

	public String getApiVersion() {
		return apiVersion;
	}

}
