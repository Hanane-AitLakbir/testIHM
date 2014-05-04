package coding;

public class CoderFactory {

	public static Coder getCoder(String name){
		if(name.equals("empty")){
			return new EmptyCoder();
		}else if(name.equals("vdm")){
			return null;
		}
		return null;
	}
}
