@pet_service_annotation
Feature: Pet Service

    Scenario: SavePet scenario
      Given There is an owner called "Amir Hossein Moradpoor"
      When Performs save pet service and add a new pet to list
      Then The pet is saved in service

    Scenario: FindOwner scenario
      Given There is an owner called "Amir Hossein Moradpoor"
      When Perform find owner
      Then The owner is returned

    Scenario: findPet scenario
      Given There is a pet called "Catty"
      When Perform find pet
      Then The pet is returned

    Scenario: NewPet scenario
      Given There is an owner called "Amir Hossein Moradpoor"
      When Perform new pet
      Then empty pet is added to the owner
