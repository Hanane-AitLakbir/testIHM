package connection;

import utilities.Packet;

public interface Provider {

	public void upload(Packet packet)throws CloudNotAvailableException;
	public Packet download(String name) throws CloudNotAvailableException;
	public void connect()throws CloudNotAvailableException;
}
