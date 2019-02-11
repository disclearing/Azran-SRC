package me.invakid.methodfinder.process;

import lombok.Getter;

@Getter
public class Process {

    private final String name;
    private final int pid;

    Process(String name, int pid) {
        this.name = name;
        this.pid = pid;
    }
}
