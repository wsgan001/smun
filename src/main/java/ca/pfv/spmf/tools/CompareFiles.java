package ca.pfv.spmf.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;

public class CompareFiles {

	@SuppressWarnings({ "rawtypes", "resource", "unchecked" })
	public static void main(String[] args) throws Exception{
		//String output = "/output_gsp.txt";
		//String output = "";
		String output = "/output_cm_spade.txt";
		URL url1 = CompareFiles.class.getResource(output);
		String filename1 = java.net.URLDecoder.decode(url1.getPath(),"UTF-8");
		BufferedReader reader1 = new BufferedReader(new FileReader(filename1));
		String output2 = "/output_gsp.txt";
		//String output2 = "/output_smun.txt";
		URL url2 = CompareFiles.class.getResource(output2);
		String filename2 = java.net.URLDecoder.decode(url2.getPath(),"UTF-8");
		BufferedReader reader2 = new BufferedReader(new FileReader(filename2));
		
		ArrayList a2 = new ArrayList();
		String line2;
		while (((line2 = reader2.readLine()) != null)) {
			a2.add(line2);
		}
		
		String line1;
		int count = 0;
		while (((line1 = reader1.readLine()) != null)) {
			//String newline = line1.replaceAll(" -1", "");
			String newline = line1;
			if(!a2.contains(newline)) {
				System.out.println(newline);
				count++;
			}
		}
		System.out.println("count:"+count);
		
		//Map<Integer,Integer> a = new HashMap();
		//a.con
	}

}
