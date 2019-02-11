package me.invakid.azran.server;

import me.invakid.azran.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IPPicker {
    private List<Address> addresses = new ArrayList<>();

    IPPicker(String website) throws IOException {
        refreshAddresses(website);
    }

    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    Address getRandomAddress() {
        if (addresses.isEmpty())
            return null;

        return addresses.get(Main.RAND.nextInt(addresses.size()));
    }

    private void refreshAddresses(String website) throws IOException {
        URL url = new URL(website);
        URLConnection con = url.openConnection();

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains(":")) {
                try {
                    String[] parts = line.split(":");
                    String ip = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    if (port < 0 || port > 65535) {
                        continue;
                    }

                    Address addr = new Address(ip, port);
                    addresses.add(addr);

                } catch (Exception ignored) {
                }
            }
        }
    }
}