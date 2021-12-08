package org.springframework.samples.petclinic.owner;


import org.aspectj.lang.annotation.Before;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.utility.PetTimedCache;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PetController.class,
	includeFilters = {
		@ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = PetService.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = LoggerConfig.class, type = FilterType.ASSIGNABLE_TYPE),
		@ComponentScan.Filter(value = PetTimedCache.class, type = FilterType.ASSIGNABLE_TYPE),
	})
class PetControllerTests {

	private static final int TEST_OWNER_ID = 1;
	private static final int TEST_PET_ID = 1;
	private static final int TEST_PET_TYPE_ID = 1;

	@MockBean
	private PetRepository petrepository;
	@MockBean
	private OwnerRepository ownerrepository;
	@Autowired
	private MockMvc mockMvc;

	private Pet pet;
	private Owner owner;
	private PetType pettype;

	@BeforeEach
	void setup(){
		owner = new Owner();
		owner.setId(TEST_OWNER_ID);
		owner.setFirstName("amir");
		PetType pettype = new PetType();
		pettype.setName("cat");
		pettype.setId(TEST_PET_TYPE_ID);
		pet = new Pet();
		pet.setId(TEST_PET_ID);
		pet.setType(pettype);
		pet.setName("catty");
		pet.setBirthDate(LocalDate.now());
		pet.setOwner(owner);

		when(ownerrepository.findById(1)).thenReturn(owner);
		when(petrepository.findById(1)).thenReturn(pet);
		when(petrepository.findPetTypes()).thenReturn(Lists.newArrayList(pettype));

	}

	@AfterEach
	void teardown() {
		owner = null;
		pet = null;
		pettype = null;
	}

	@Test
	void InitCreationFormTest() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(content().contentType("text/html;charset=UTF-8"))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void ProcessCreationFormHasErrorTest() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(model().hasErrors())
			.andExpect(view().name("pets/createOrUpdatePetForm"))
			.andExpect(status().isOk());
	}

	@Test
	void ProcessCreationFormHasNoErrorTest() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
			.param("name", "catty")
			.param("type", "cat")
			.param("birthDate", pet.getBirthDate().toString()))
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void InitUpdateFormTest() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(content().contentType("text/html;charset=UTF-8"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}



	@Test
	public void ProcessUpdateFormHasNoErrorTest() throws  Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
			.param("id", "1")
			.param("name", "catty")
			.param("type", "cat")
			.param("BirthDate", pet.getBirthDate().toString()))
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(model().attributeDoesNotExist("pet"));
	}
	@Test
	public void ProcessUpdateFormHasErrorTest() throws  Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
			.param("id", "3")
			.param("name", "catty!")
			.param("type", "dog")
			.param("BirthDate", pet.getBirthDate().toString()+"1"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"))
			.andExpect(model().attributeExists("pet"));
	}
}
