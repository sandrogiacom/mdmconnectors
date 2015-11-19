package com.totvslabs.mdm.client;

import com.totvslabs.mdm.client.ui.PanelGeneral;
import com.totvslabs.mdm.client.util.ThreadProcessBatch;

public class SendDataClient {
	public static void main(String[] args) {
		if(args != null && args.length > 0 && args[0].equals("service")) {
			Thread thread = new Thread(new ThreadProcessBatch());
			thread.start();
		}
		else {
			new PanelGeneral();
		}
	}
}
