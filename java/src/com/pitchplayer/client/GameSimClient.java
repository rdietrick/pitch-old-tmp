package com.pitchplayer.client;

import java.io.IOException;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class GameSimClient {

	public static void main(String args[]) {
		if (args.length < 2) {
			System.out
					.println("usage: java com.pitchplayer.client.GameSimClient remote_server remote_port");
			System.exit(-1);
		}

		try {
			XmlRpc.setDriver("org.apache.xerces.parsers.SAXParser");
			XmlRpcClient client = new XmlRpcClient("http://" + args[0] + ":"
					+ args[1]);
			Vector params = new Vector();
			if (client == null) {
				System.out.println("client is null.");
			}
			Object resultObj = client.execute("gamesim.simulateGame", params);
			if (resultObj == null) {
				System.out.println("result is null");
			}
			String result = (String) resultObj;
			System.out.println("Game simulated.  Winner is: " + result);
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Could not load SAX Driver");
		} catch (XmlRpcException xre) {
			System.out.println("XML-RPC Exception: " + xre.getMessage());
			xre.printStackTrace();
		} catch (IOException ioe) {
			System.out.println("IO Error:  " + ioe.getMessage());
		}

	}

}