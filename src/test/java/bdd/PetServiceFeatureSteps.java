package bdd;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.samples.petclinic.utility.PetTimedCache;

import java.time.LocalDate;

public class PetServiceFeatureSteps {


	@Autowired
	PetService petService;

	@Autowired
	PetTimedCache petTimedCache;

	@Autowired
	PetRepository petRepository;

	@Autowired
	OwnerRepository ownerRepository;


	private Pet pet;
	private Owner owner;
	private Pet foundPet;
	private Owner foundOwner;
	private Logger logger = Mockito.mock(Logger.class);


	static final String PET_NAME = "Catty";
	static final int PET_ID = 1;
	static final int OWNER_ID = 1;


	@Before("@pet_service_annotation")
	public void setup() {

		owner = new Owner();
		owner.setId(OWNER_ID);
		owner.setFirstName("Amir Hossein");
		owner.setLastName("Moradpoor");
		owner.setTelephone("00000000000");
		owner.setCity("Tehran");
		owner.setAddress("Khazaneh");

		PetType petType = new PetType();
		petType.setName("Cat");
		petType.setId(1);
		pet = new Pet();
		pet.setType(petType);
		pet.setId(PET_ID);
		pet.setName(PET_NAME);
		pet.setBirthDate(LocalDate.parse("2022-01-01"));

		petTimedCache = new PetTimedCache(petRepository);
		petService = new PetService(petTimedCache, ownerRepository, logger);

	}


	@Given("There is an owner called {string}")
	public void ThereIsAnOwnerCalled(String ownerName) {
		ownerRepository.save(owner);
	}


	@When("Performs save pet service and add a new pet to list")
	public void PerformsPetService() {
		petService.savePet(pet, owner);
	}


	@Then("The pet is saved in service")
	public void PetIsSaved(){
		Mockito.verify(logger).info("save pet {}", pet.getId());
		Assertions.assertNotNull(owner.getPets());
		Assertions.assertNotNull(petService.findPet(PET_ID));
		Assertions.assertEquals(OWNER_ID, petService.findPet(PET_ID).getOwner().getId());
	}


	@When("Perform find owner")
	public void PerformFindOwner(){
		foundOwner = petService.findOwner(OWNER_ID);
	}


	@Then("The owner is returned")
	public void OwnerRetured(){
		Mockito.verify(logger).info("find owner {}", OWNER_ID);
	}


	@Given("There is a pet called {string}")
	public void ThereIsAPetCalled(String name) {
		owner.addPet(pet);
		petTimedCache.save(pet);
	}


	@When("Perform find pet")
	public void PerformFindPet(){
		foundPet = petService.findPet(PET_ID);
	}


	@Then("The pet is returned")
	public void PetIsReturned(){
		Assertions.assertEquals(pet.getId(), foundPet.getId());
		Assertions.assertEquals(pet.getName(), foundPet.getName());
	}


	@When("Perform new pet")
	public void PerformNwePer(){
		foundPet = petService.newPet(owner);
	}


	@Then("empty pet is added to the owner")
	public void EmptyPetAddedToTheOwner(){
		Mockito.verify(logger).info("add pet for owner {}", owner.getId());
		Assertions.assertNotNull(owner.getPets());
	}

}
