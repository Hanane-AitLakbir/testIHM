package coding;

import utilities.Packet;

public interface Coder {

	public Packet[] encode(Packet[] packets);
	public Packet[] decode(Packet[] packets, int[] indexDownloadedPackets);
	//the int array is needed for decoding : it must be the no of downloaded packets 
	public String getName();
	public int getInputSize();
}
