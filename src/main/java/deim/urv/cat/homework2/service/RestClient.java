package deim.urv.cat.homework2.service;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import deim.urv.cat.homework2.Config;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

/**
 *
 * @author JUDITH
 */
public class RestClient {
    private final Client client;
    private static final String baseURL = "http://localhost:8080/Homework1/webresources/api/v1/";

    @Inject
    public RestClient() {
        this.client = ClientBuilder.newClient();
    }
}