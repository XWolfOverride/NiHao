package nihao.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLogProvider extends LogProvider {
	static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

	@Override
	public void writeLog(long timestamp, LogLevel level, String s) {
		System.out.print('[');
		System.out.print(sdf.format(new Date(timestamp)));
		System.out.print("] ");
		System.out.print(level.name());
		System.out.print(": ");
		System.out.println(s);
	}

}
