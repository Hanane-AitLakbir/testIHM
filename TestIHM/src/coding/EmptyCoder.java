package coding;

import utilities.Packet;

public class EmptyCoder implements Coder {

	@Override
	public Packet[] encode(Packet[] packets) {
		return packets;
	}

	@Override
	public Packet[] decode(Packet[] packets) {
		return packets;
	}

}
