package nekowei.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	
	public static void info(Object s) {
		System.out.println(" [" + sdf.format(new Date(System.currentTimeMillis())) + "] " + s.toString());
	}

}
