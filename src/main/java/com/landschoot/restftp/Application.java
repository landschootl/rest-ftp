package com.landschoot.restftp;

import com.landschoot.restftp.config.ServerConfig;
import com.landschoot.restftp.controller.FileController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Application {

	public static void initDirectories(){
		File directory = new File(ServerConfig.GLOBAL_PATH);
		directory.mkdirs();
		for(int i=0; i<3; i++){
			directory = new File(ServerConfig.GLOBAL_PATH+"dossier"+i);
			directory.mkdirs();
			for(int j=0; j<3; j++){
				directory = new File(ServerConfig.GLOBAL_PATH+"dossier"+i+"/sousdossier"+j);
				directory.mkdirs();
			}
		}
	}

	public static void main(String[] args) {
		Application.initDirectories();
		SpringApplication.run(Application.class, args);
	}
}
