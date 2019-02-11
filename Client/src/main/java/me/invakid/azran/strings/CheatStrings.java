package me.invakid.azran.strings;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CheatStrings {
	private String clientName;
	private List<CheatString> cheatStrings;

	public CheatStrings(String clientName, List<CheatString> cheatStrings) {
		this.clientName = clientName;
		this.cheatStrings = cheatStrings;
	}

	public void update(String s, Map<CheatString, Integer> map) {
		for(CheatString cs : cheatStrings) {
			int frequency = 0;
			while(s.contains(cs.string)) {
				frequency++;
				int index = s.indexOf(cs.string);
				s = s.substring(0, index) + s.substring(index + cs.string.length());
			}

			//res.put(cs, frequency);

			int prevFreq = map.getOrDefault(cs, 0);
			int totalFreq = frequency + prevFreq;

			map.put(cs, totalFreq);


			/*
			if(frequency >= cs.minFrequency) {
				// Cheat detected!
				System.out.println("Detected cheat string for '" + clientName + "':  '" + cs.string + "' (" + frequency + " times)");
				stringsMatch++;
			}
			*/
		}
	}

	public String getClientName() {
		return clientName;
	}

	public List<CheatString> getCheatStrings() {
		return Collections.unmodifiableList(cheatStrings);
	}

	public void selfdestruct() {
		cheatStrings.clear();
	}

	public CheatStrings getSome(int maxStrings, Random rnd) {
		List<CheatString> res = new ArrayList<>(cheatStrings);
		while(res.size() > maxStrings) {
			res.remove(rnd.nextInt(res.size()));
		}

		return new CheatStrings(clientName, res);
	}

	public void serialize(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);

		dos.writeUTF(clientName);

		dos.writeInt(cheatStrings.size());
		for(CheatString string : cheatStrings) {
			dos.writeUTF(string.string);
			//dos.writeUTF(string.cheatName);
			dos.writeBoolean(string.caseSensitive);
		}
	}


	public static CheatStrings deserialize(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);

		String clientName = dis.readUTF();

		List<CheatString> strings = new ArrayList<>();
		int cheatStringAmount = dis.readInt();
		for(int i = 0; i < cheatStringAmount; i++) {
			String str = dis.readUTF();
			//String name = dis.readUTF();
			boolean bool = dis.readBoolean();
			strings.add(new CheatString(str/*, name*/, bool));
		}

		return new CheatStrings(clientName, strings);
	}

	public static CheatStrings fromStream(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String clientName = br.readLine();

		List<CheatString> strings = new ArrayList<>();
		String line;
		while((line = br.readLine()) != null) {
			boolean caseSensitive = false;

			if(line.length() > 2 && line.charAt(0) == '<' && line.charAt(1) == '>') {
				caseSensitive = true;
				line = line.substring(2);
			}

			//String[] arr = line.split(":::");
			//String string = arr[0];
			//String cheatName = arr[1];

			strings.add(new CheatString(/*string, cheatName*/line, caseSensitive));
		}

		return new CheatStrings(clientName, strings);
	}
}