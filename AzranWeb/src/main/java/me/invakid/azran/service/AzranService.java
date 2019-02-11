package me.invakid.azran.service;

import me.invakid.azran.dao.AzranDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AzranService {

    @Autowired
    AzranDAO dao;

    public boolean isTokenValid(String token) {
        return dao.isTokenValid(token);
    }

    public void removeToken(String token) {
        dao.removeToken(token);
    }

}
