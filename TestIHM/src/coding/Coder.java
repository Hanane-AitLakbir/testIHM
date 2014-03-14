package coding;

import utilities.Packet;

public interface Coder {

	public Packet[] encode(Packet[] packets);
	public Packet[] decode(Packet[] packets);
}
