package com.totvslabs.mdm.client;

import java.util.Arrays;

import com.totvslabs.mdm.client.ui.PanelGeneral;
import com.totvslabs.mdm.client.util.FileConsume;
import com.totvslabs.mdm.client.util.JDBCConnectionConsume;
import com.totvslabs.mdm.client.util.ThreadProcessBatch;
import com.totvslabs.mdm.client.util.ThreadProcessBatchExport;

public class SendDataClient {
	public static void main(String[] args) {
		if(Arrays.asList(args).contains("--no-cache")) {
			FileConsume.IGNORE_LOCAL_CACHE = Boolean.TRUE;
			JDBCConnectionConsume.IGNORE_LOCAL_CACHE = Boolean.TRUE;
		}

		if(args != null && args.length > 0 && args[0].equals("service")) {
			Thread thread = new Thread(new ThreadProcessBatch());
			thread.start();
		}
		else if(args != null && args.length > 0 && args[0].equals("serviceExport")) {
			Thread thread = new Thread(new ThreadProcessBatchExport(args[1]));
			thread.start();
		}
		else {
			new PanelGeneral();
		}
	}
}
