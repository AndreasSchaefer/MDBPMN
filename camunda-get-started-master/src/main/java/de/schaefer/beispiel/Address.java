package de.schaefer.beispiel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import de.schaefer.mdbpmn.annotations.Constraint;
import de.schaefer.mdbpmn.annotations.Label;
import de.schaefer.mdbpmn.annotations.Labels;
import de.schaefer.mdbpmn.annotations.Validation;

@Entity
public class Address {
	
	@Id
	@GeneratedValue
	int id;

	@Labels({
		@Label(language = "EN-US", value = "Street"),
		@Label(language = "DE", value = "Straﬂe")
	})
	@Validation({
		@Constraint(name = "required")
	})
	String street;
	
	@Labels({
		@Label(language = "EN-US", value = "Postcode"),
		@Label(language = "DE", value = "PLZ")
	})
	@Validation({
		@Constraint(name = "required")
	})
	String postcode;
	
	@Labels({
		@Label(language = "EN-US", value = "City"),
		@Label(language = "DE", value = "Stadt")
	})
	@Validation({
		@Constraint(name = "required")
	})
	String city;
	
	@Labels({
		@Label(language = "EN-US", value = "Country"),
		@Label(language = "DE", value = "Land")
	})
	@Validation({
		@Constraint(name = "required")
	})
	String country;
}
