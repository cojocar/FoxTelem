package telemStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import common.Config;
import common.Log;
import common.DesktopApi;
import common.Spacecraft;
import telemetry.FramePart;

public class TelemetryPipe implements Runnable {

	String pipePath;
	int trackedSatId = -1;
	FileOutputStream out = null;

	/* Named pipe should exist, this code path does not take care of creating
	 * the pipe. Use mkfifo (on linux) to create such a pipe.
	 */
	public TelemetryPipe (int trackedSatId, String pipeName) {
		this.pipePath = getArchPipeNamePath(pipeName);
		this.trackedSatId = trackedSatId;
	}

	private String getArchPipeNamePath(String pipeName) {
		DesktopApi.EnumOS os = DesktopApi.getOs();
		if (os.isWindows()) {
			return "\\\\.\\pipe\\" + pipeName;
		}
		// TODO: not sure what to do on MAC.
		// on Linux, we can use the direct name as a path
		return pipeName;
	}

	@Override
	public void run() {
		try {
			this.out = new FileOutputStream(getArchPipeNamePath(this.pipePath));
		} catch (FileNotFoundException e) {
			Log.println("TelemetryPipeThread failed to open pipe");
			return;
		}

		Log.println("TelemetryPipeThread START");
		Log.println("station lat, lon: " + Config.latitude + " " + Config.longitude);
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String p = getPositionNow(trackedSatId);
			String data = p + "\n";
			try {
				out.write(data.getBytes());
			} catch (IOException e) {
				break;
			}
		}
		Log.println("TelemetryPipeThread END");
	}

	private String getPositionNow(int trackedSatId) {
		// The output format is EASYCOMM II. A full description of this format
		// can be found here: https://www.mustbeart.com/software/easycomm.txt
		//
		// We implement only a subset of commands (the AZ and EL)
		Spacecraft sat = Config.satManager.spacecraftList.get(trackedSatId);
		if (sat.satPos == null) {
			return "ERR";
		}
		double az = FramePart.radToDeg(sat.satPos.getAzimuth());
		double el = FramePart.radToDeg(sat.satPos.getElevation());

		String position = "AZ" + String.format("%2.1f", az) + " EL" + String.format("%2.1f", el);
		StringBuffer ret = new StringBuffer(position);

		Log.println(position);

		return ret.toString();
	}
}
