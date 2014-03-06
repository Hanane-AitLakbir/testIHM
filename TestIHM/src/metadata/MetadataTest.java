package metadata;


public class MetadataTest {

	public void test() {
		Metadata meta = new Metadata();
		System.out.println(meta==null);
		meta.addContent("name", "toto");
		meta.addContent("checksum", "ef4562eac56");
		System.out.println(meta.browse("name"));
		meta.serialize("C:/Users/aït-lakbir/Desktop/PIPTest/meta.json");
		System.out.println("serialization OK");
		Metadata meta2 =new JSonSerializer("C:/Users/aït-lakbir/Desktop/PIPTest/meta.json").deserialize();
		System.out.println(meta2.browse("name"));
	}

}
