package com.itahm.icmp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

abstract public class ICMPNode implements Runnable {

	private long interval = 10000;
	private int timeout = 5000;
	
	private final InetAddress target;
	private Thread thread;

	public ICMPNode(String host) throws UnknownHostException {
		target = InetAddress.getByName(host);	
	}
	
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	public void stop() {
		this.thread.interrupt();
	}
	
	@Override
	public void run() {
		while(!this.thread.isInterrupted()) {
			try {
				if (this.target.isReachable(this.timeout)) {
					onSuccess();
				}
				else {
					onFailure();
					
					continue;
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			try {
				Thread.sleep(this.interval);
			} catch (InterruptedException ie) {
				break;
			}
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		ICMPNode node = new ICMPNode("192.168.0.1") {

			@Override
			void onSuccess() {
				System.out.println("O");
			}

			@Override
			void onFailure() {
				System.out.println("X");
			}
			
		};
		
		node.setInterval(1000);
		node.start();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		node.stop();
	}
	
	abstract void onSuccess();
	abstract void onFailure();
}
