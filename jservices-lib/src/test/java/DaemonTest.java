import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import org.apache.log4j.BasicConfigurator;

import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceURL;

public class DaemonTest {

	public static void main(String[] args) {
		try {
			long ID = System.currentTimeMillis();
			BasicConfigurator.configure();
			while (true) {
				ServiceLocationManager.getLocator(Locale.ENGLISH).findServiceTypes("service:Hello", new ArrayList());
				Thread.sleep(1000L);
				System.out.println(new Date() + " Alive...");
				ServiceLocationManager.getAdvertiser(new Locale("en")).register(new ServiceURL("service:test:IANA:/http://a.b.s:1234/path/" + ID, 10), new Hashtable());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
