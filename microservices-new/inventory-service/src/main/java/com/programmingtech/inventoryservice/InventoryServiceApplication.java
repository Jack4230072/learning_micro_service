package com.programmingtech.inventoryservice;

import com.programmingtech.inventoryservice.model.Inventory;
import com.programmingtech.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("iphone_15_red");
			inventory.setQuantity(0);

			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("iphone_15_red");
			inventory2.setQuantity(100);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory2);
		};
	}
}
