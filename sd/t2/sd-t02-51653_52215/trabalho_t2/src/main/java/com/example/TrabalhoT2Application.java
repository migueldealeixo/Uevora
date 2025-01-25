package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.trabalho_t2.simulator.Listener;
import com.example.trabalho_t2.simulator.Sim;

@SpringBootApplication(exclude=SecurityAutoConfiguration.class)
@EnableJpaRepositories
public class TrabalhoT2Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TrabalhoT2Application.class, args);
    }

    @Autowired
    private Listener listener;

    @Autowired
    private Sim simulator;

    public void run(String... args) throws Exception {

        new Thread(() -> {
            try {
                listener.start(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                simulator.startSimulatingAllDevices(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
 

    

}
