package me.invakid.azran;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Detection {

    private String time, ign, ip, detectionName, detectionLine, detectedString, pinUsed, guild, channel, generatedBy;

    public Detection(String time, String ign, String ip, String detectionName, String detectionLine, String detectedString, String pinUsed, String guild, String channel, String generatedBy) {
        this.time = time;
        this.ign = ign;
        this.ip = ip;
        this.detectionName = detectionName;
        this.detectionLine = detectionLine;
        this.detectedString = detectedString;
        this.pinUsed = pinUsed;
        this.guild = guild;
        this.channel = channel;
        this.generatedBy = generatedBy;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s): %s (\"%s\" in \"%s\"), pin used: %s, generated in %s, %s by %s", time, ip, ign, detectionName, detectedString, detectionLine, pinUsed, channel, guild, generatedBy);
    }
}
