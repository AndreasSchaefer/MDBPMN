package de.schaefer.beispiel;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;

import de.schaefer.mdbpmn.MDBPMN_Framework;
import de.schaefer.mdbpmn.TargetBPMNPlatform;
import de.schaefer.mdbpmn.exceptions.InitializeException;
import de.schaefer.persistence.DAO;

@ProcessApplication("MDBPMN Example Process Application")
public class MDBPMNExampleApplication extends ServletProcessApplication {
	
	@PostDeploy
	public void initializeMDBPMN() {
		try {
		 	MDBPMN_Framework.initializeFramework(new DAO(), getClass().getClassLoader(), new ExampleValidation(), TargetBPMNPlatform.CAMUNDA);
		} catch (InitializeException e) {
			e.printStackTrace();
		}
	}
}
