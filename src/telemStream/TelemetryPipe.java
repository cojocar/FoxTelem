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
	public TelemetryPipe (int trackedSatId, String pipeName) {
		this.pipePath = getArchPipeNamePath(pipeName);
		this.trackedSatId = trackedSatId;
	}

	private String getArchPipeNamePath(String pipeName) {
		DesktopApi.EnumOS os = DesktopApi.getOs();
		if (os.isWindows()) {
			return "\\\\.\\pipe\\" + pipeName;
		}
		// TODO: not sure what to do on MAC
		// on Linux, we can use the direct name
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
		Spacecraft sat = Config.satManager.spacecraftList.get(trackedSatId);
		if (sat.satPos == null) {
			return "";
		}
		double az = FramePart.radToDeg(sat.satPos.getAzimuth());
		double el = FramePart.radToDeg(sat.satPos.getElevation());

		String position = "AZ" + String.format("%2.1f", az) + " EL" + String.format("%2.1f", el);
		StringBuffer ret = new StringBuffer(position);
		if (Config.foxTelemCalcsDoppler) {
			double freq = sat.satPos.getDopplerFrequency(sat.user_telemetryDownlinkFreqkHz);
			String sign="";
			if (freq > 0) sign = "+";
			position = position + " |" + sign+String.format("%2.3f", freq) + "kHz";
		}
		Log.println(position);
		return ret.toString();
	}
}
