package me.invakid.methodfinder.process;

import me.invakid.methodfinder.util.WMICUtils;

import java.util.ArrayList;
import java.util.List;

public enum ProcessManager {

    INSTANCE;

    ProcessManager() {
    }

    public List<Process> getAllProcesses() {
        List<Process> processes = new ArrayList<>();

        List<String> processData = WMICUtils.executeCommand("wmic process get name, processid");
        if (processData != null) {
            int pidIndex = processData.get(0).lastIndexOf(' ') + 1;

            processData.remove(0);
            for (String line : processData) {
                String pid = line.substring(pidIndex);
                String name = line.replace(pid, "").trim();

                processes.add(new Process(name, Integer.valueOf(pid)));
            }
        }

        return processes;
    }

}
