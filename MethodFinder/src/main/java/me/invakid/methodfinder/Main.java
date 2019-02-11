package me.invakid.methodfinder;

import me.invakid.methodfinder.process.Process;
import me.invakid.methodfinder.process.ProcessManager;
import me.invakid.methodfinder.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);

        System.out.print("Enter string to search for: ");
        String str = in.nextLine().toLowerCase();

        System.out.printf("Scanning started @ %s!%n", Utils.getTimeNow());

        List<Process> processes = ProcessManager.INSTANCE.getAllProcesses();
        System.out.println("Loaded " + processes.size() + " processes!");

        File strings2 = new File("strings2.exe");
        strings2.deleteOnExit();

        File scanResult = new File("dump.txt");
        scanResult.deleteOnExit();

        if(Utils.extractStrings2(strings2)) {
            for (Process process : processes) {
                int pid = process.getPid();

                String cmd = String.format("cmd.exe /C %s -pid %d > %s", strings2.getName(), pid, scanResult.getName());
                ProcessBuilder sProcessBuilder = new ProcessBuilder(cmd.split(" ")).directory(strings2.getParentFile());

                sProcessBuilder.start().waitFor();

                LineIterator it = FileUtils.lineIterator(scanResult);
                while(it.hasNext()) {
                    String line = it.nextLine().toLowerCase();

                    if(line.contains(str)) {
                        System.out.printf("String found in %s (PID: %d)!%n", process.getName(), pid);
                        break;
                    }
                }

                it.close();
            }
        } else {
            System.err.println("Failed to extract strings2!");
            strings2.deleteOnExit();
        }

        System.out.printf("Scanning ended @ %s!%n", Utils.getTimeNow());
    }

}
