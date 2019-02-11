package me.invakid.azran.server.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CheatString implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String cheatName;
    public final String string;
    public final boolean caseSensitive;

    CheatString(String string, String cheatName, boolean caseSensitive) {
        this.string = string;
        this.cheatName = cheatName;
        this.caseSensitive = caseSensitive;
    }

}