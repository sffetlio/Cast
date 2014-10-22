package cast.client.gdx.connection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cast.common.packets.Packet;

/**
 * FIFO packet queue. Used for off-loading packet sending from other threads to
 * this dedicated one. Prevent TouchUp/Down/Move from blocking while sending and
 * taking up too much time.
 * 
 * Must test more if desired result achieved. :)
 */
public class PacketQueue implements Runnable {

	private boolean stopping = false;
	private final ClientConnectionThread connection;
	private BlockingQueue<Packet> queue = new LinkedBlockingQueue<Packet>();

	public PacketQueue(ClientConnectionThread connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		while (!stopping) {
			try {
				Packet packet = queue.poll(1, TimeUnit.SECONDS);
				if (packet != null) {
					connection.write(packet.getBytes());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add packet in queue for sending,
	 * 
	 * @param packet
	 */
	public synchronized void add(Packet packet) {
		if (packet != null) {
			queue.add(packet);
		}
	}

	/**
	 * Stop thread, may take up to 1 s.
	 */
	public void stop() {
		this.stopping = true;
	}

}
