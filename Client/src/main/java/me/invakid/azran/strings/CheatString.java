package me.invakid.azran.strings;

public class CheatString {
	//public final String cheatName;
	public final String string;
	public final boolean caseSensitive;

	public CheatString(String string, /*String cheatName, */boolean caseSensitive) {
		this.string = string;
		//this.cheatName = cheatName;
		this.caseSensitive = caseSensitive;
	}

	public CheatString(String str/*, String cheatName*/) {
		this(str, /*cheatName,*/false);
	}
}