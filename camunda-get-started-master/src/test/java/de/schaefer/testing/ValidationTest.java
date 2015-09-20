package de.schaefer.testing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.schaefer.mdbpmn.ModelValidator;

public class ValidationTest {
	
	private Set<String> uniqueClasses = new HashSet<String>();

	@Test
	public void testMDBPMN() throws Exception {
		ModelValidator modelValidator = new ModelValidator(this.getClass().getClassLoader());
		System.out.println(modelValidator.getReport());
	}

}
