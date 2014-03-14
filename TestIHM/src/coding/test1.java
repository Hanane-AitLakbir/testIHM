package coding;

public class test1 {

	public static void main(String[] args) {
		String string = "Dans le monde il y a 10 types de personnes, ceux qui comprennent le binaire, et les autres.";
		byte[] data = string.getBytes();
		int length = data.length;
		
		//equivalent of the two packets
		byte[] part1 = new byte[length/2];
		byte[] part2 = new byte[length - part1.length];

		System.arraycopy(data, 0, part1, 0, part1.length);
		System.arraycopy(data, part1.length, part2, 0, part2.length);
		
		System.out.println("part1 : "+new String(part1));
		System.out.println("part2 : "+new String(part2));
		
		//sum
		byte[] result = new byte[part1.length];
		for(int i=0;i<part1.length;i++){
			result[i] = (byte) (part1[i] + part2[i]);
		}
		System.out.println("result sum : " + new String(result));
		
		//get part2
		byte[] result2 = new byte[part1.length];
		for(int i=0;i<part1.length;i++){
			result2[i] = (byte) (result[i] - part1[i]);
		}
		
		System.out.println("part2 after subtraction : " + new String(result2));
		
		//get part1
		byte[] result3 = new byte[part1.length];
		for(int i=0;i<part1.length;i++){
			result3[i] = (byte) (result[i] - part2[i]);
		}
		
		System.out.println("part1 after subtraction : " + new String(result3));
		
		byte[] finalRes = new byte[result2.length + result3.length];
		for(int k = 0;k<finalRes.length;k++){
			if(k<result3.length){
				finalRes[k] = result3[k];
			}else{
				finalRes[k] = result2[k-result3.length];
			}
		}
		System.out.println("\n"+new String(finalRes));
	}

}
