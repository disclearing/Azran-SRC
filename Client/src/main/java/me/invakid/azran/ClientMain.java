package me.invakid.azran;

import me.invakid.azran.scanner.Azran;

import java.io.File;
import java.util.Random;

public class ClientMain {

    public static final Random RAND = new Random();
    public static final int CODE_LENGTH = 4;
    public static final String IPS_WEBSITE = "https://drive.google.com/uc?export=download&id=1ARr3XnooIN_C53fES4Cox9sNJblgl8AK";

    public static void main(String[] args) {
        if(args.length != 2) return;

        File f = new File(args[0]);
        if(!f.exists()) return;

        new Azran(f, args[1]);
    }

}
