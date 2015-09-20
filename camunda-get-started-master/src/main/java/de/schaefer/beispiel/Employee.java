package de.schaefer.beispiel;

import java.util.Date;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import de.schaefer.mdbpmn.annotations.Constraint;
import de.schaefer.mdbpmn.annotations.Label;
import de.schaefer.mdbpmn.annotations.Labels;
import de.schaefer.mdbpmn.annotations.MapToProcess;
import de.schaefer.mdbpmn.annotations.Validation;

@Entity
@Table(name = "Employees")
public class Employee {

	@MapToProcess
	@Id
	@GeneratedValue
	@Labels({
		@Label(language = "EN-US", value = "Identity Number of Employee"),
		@Label(language = "DE", value ="Mitarbeiter Identifikationsnummer")
	})
	int id;
	
	@Labels({
		@Label(language = "EN-US", value = "Firstname"),
		@Label(language = "DE", value = "Vorname")
	})
	@Validation({
		@Constraint(name = "minlength", config = "3"),
		@Constraint(name = "required")
	})
	String firstname;
	
	@Labels({
		@Label(language = "EN-US", value = "Surname"),
		@Label(language = "DE", value = "Nachname")
	})
	@Validation({
		@Constraint(name = "minlength", config = "3"),
		@Constraint(name = "required")
	})
	String surname;
	
	@Labels({
		@Label(language = "EN-US", value = "Holiday entitlement"),
		@Label(language = "DE", value = "Urlaubsanspruch")
	})
	@Validation({
		@Constraint(name = "min", config = "24"),
		@Constraint(name = "max", config = "40"),
		@Constraint(name = "required")
	})
	int holidayEntitlement;
	
	@OneToOne(cascade=CascadeType.ALL)
	Address adress;
	
	@Labels({
		@Label(language = "EN-US", value = "email Adress"),
		@Label(language = "DE", value = "E-Mail Adresse")
	})
	@Validation({
		@Constraint(name = "regex", config = "^[_A-Za-z0-9-\\\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
	})
	String mail;
	
	@Labels({
		@Label(language = "EN-US", value = "month Salary"),
		@Label(language = "DE", value = "monatliches Gehalt")
	})
	@Validation({
		@Constraint(name = "min", config = "1000"),
		@Constraint(name = "max", config = "5000"),
		@Constraint(name = "required")
	})
	double monthSalary;
	
	@Labels({
		@Label(language = "EN-US", value = "Branch Office"),
		@Label(language = "DE", value = "Zweigstelle")
	})
	BranchOffice branchOffice;
	
	@Labels({
		@Label(language = "EN-US", value = "Job Title"),
		@Label(language = "DE", value = "Berufsbezeichnung")
	})
	@Validation({
		@Constraint(name = "required")
	})
	String jobname;
	
	@Labels({
		@Label(language = "EN-US", value = "Qualifications"),
		@Label(language = "DE", value = "Qualifikationen")
	})
	@Validation({
		@Constraint(name = "minsize", config="1"),
		@Constraint(name = "maxsize", config="10")
	})
	@ElementCollection(fetch=FetchType.EAGER)
	private Map<String, Date> qualifications;
	
}
