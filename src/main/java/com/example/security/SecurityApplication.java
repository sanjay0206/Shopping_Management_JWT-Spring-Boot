package com.example.security;

import com.example.security.entities.AppUser;
import com.example.security.entities.Category;
import com.example.security.entities.Product;
import com.example.security.repositories.AppUserRepo;
import com.example.security.repositories.ProductRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

import static com.example.security.security.ApplicationUserRole.*;

@SpringBootApplication
@Configuration
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
		System.out.println("Spring security is working...");
	}

	@Bean
	CommandLineRunner commandLineRunner(AppUserRepo appUserRepo,
										ProductRepo productRepo) {
		return args -> {
			AppUser appUser1 = new AppUser("maria.jones", "maria123","maria.jones@gmail.com", USER);
			AppUser appUser2 = new AppUser("will.smith", "will123", "will.smith@gmail.com", VENDOR);
			AppUser appUser3 = new AppUser("anna.smith", "anna123", "anna.smith@gmail.com", ADMIN);
			AppUser appUser4 = new AppUser("james.bond", "james123","james.bond@gmail.com", USER);
			AppUser appUser5 = new AppUser("alex.hales", "alex123", "alex.hales@gmail.com", VENDOR);
			AppUser appUser6 = new AppUser("stuart.little", "stuart13", "stuart.little@gmail.com", VENDOR);
			AppUser appUser7 = new AppUser("ahmad.bilal", "ahmad123", "ahmad.bilal@gmail.com", USER);
			List<AppUser> appUsers = Arrays.asList(
					appUser1, appUser2, appUser3, appUser4, appUser5, appUser6, appUser7
			);
			appUserRepo.saveAll(appUsers);

			Product product1 = new Product("Mi TV 40A", Category.ELECTRONICS, 19000.00);
			Product product2 = new Product("Levis T-Shirt", Category.FASHION, 500.50);
			Product product3 = new Product("Floral Door Curtain", Category.HOME_FURNISHINGS, 1200.20);
//			Product product4 = new Product("Weight Bench 500 Flat/Incline/Decline", Category.FITNESS, 500.50);
			Product product5 = new Product("Peanut Butter", Category.GROCERIES, 1200.20);
			Product product6 = new Product("Apple Iphone 13", Category.ELECTRONICS, 19000.00);
			Product product7 = new Product("Peri Peri Mayonnaise ", Category.GROCERIES, 500.50);
			Product product8 = new Product("boAt Airpods", Category.ELECTRONICS, 1200.20);

			// Electronics
			product1.setAppUser(appUser2);
			product6.setAppUser(appUser2);
			product8.setAppUser(appUser2);

			// Fashion and home decor
			product2.setAppUser(appUser5);
			product3.setAppUser(appUser5);

			// Groceries
			product5.setAppUser(appUser6);
			product7.setAppUser(appUser6);
			List<Product> products = Arrays.asList(
					product1, product2, product3, product5, product6, product7, product8
			);
			productRepo.saveAll(products);
		};
	}

}
