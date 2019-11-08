package gpms.ngac.policy;

import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.PDP;
import gov.nist.csd.pm.pip.graph.Graph;
import gov.nist.csd.pm.pip.graph.GraphSerializer;
import gov.nist.csd.pm.pip.graph.MemGraph;
import gov.nist.csd.pm.pip.obligations.MemObligations;
import gov.nist.csd.pm.pip.obligations.evr.EVRParser;
import gov.nist.csd.pm.pip.obligations.model.Obligation;
import gov.nist.csd.pm.pip.prohibitions.MemProhibitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.log4j.Logger;


/**
 * @author Md Nazmul Karim
 * @since May 20 2019
 * 
 * This class is used to load NGAC base configuration from a JSON file
 */
public class NGACPolicyConfigurationLoader {

	public static Random rand = new Random();

	//This graph holds NGAC policy
	private static Graph ngacPolicy;
	private static final Logger log = Logger.getLogger(NGACPolicyConfigurationLoader.class.getName());

	public void init() {
		if (ngacPolicy == null) {
			File file_super = getFileFromResources(Constants.POLICY_CONFIG_FILE_SUPER);
			File file_proposal_creation = getFileFromResources(Constants.POLICY_CONFIG_FILE_PROPOSAL_CREATION);
			File file_university_org = getFileFromResources(Constants.POLICY_CONFIG_FILE_UNIVERSITY_ORGANIZATION);
			//File file_pds = getFileFromResources(Constants.POLICY_CONFIG_FILE_PDS);
			//File file_approval = getFileFromResources(Constants.POLICY_CONFIG_FILE_APPROVAL);
			//File file_getFaculty = getFileFromResources(Constants.POLICY_CONFIG_FILE_GET_FACULTY);
			//File file_cross_policy = getFileFromResources(Constants.POLICY_CONFIG_FILE_CROSS_POLICY);
			String jsonSuper;
			String jsonProposalCreation;
			String jsonUnivOrg;
			String jsonPds;
			String jsonApproval;
			String jsonGetFaculty;
			String jsonCrossPolicy;
			try {
				jsonSuper = new String(Files.readAllBytes(Paths.get(file_super.getAbsolutePath())));
				jsonProposalCreation = new String(Files.readAllBytes(Paths.get(file_proposal_creation.getAbsolutePath())));
				jsonUnivOrg = new String(Files.readAllBytes(Paths.get(file_university_org.getAbsolutePath())));
				//jsonPds = new String(Files.readAllBytes(Paths.get(file_pds.getAbsolutePath())));
				//jsonApproval = new String(Files.readAllBytes(Paths.get(file_approval.getAbsolutePath())));
				//jsonGetFaculty = new String(Files.readAllBytes(Paths.get(file_getFaculty.getAbsolutePath())));
				//jsonCrossPolicy = new String(Files.readAllBytes(Paths.get(file_cross_policy.getAbsolutePath())));
				try {
					ngacPolicy = GraphSerializer.fromJson(new MemGraph(), jsonSuper);
					ngacPolicy = GraphSerializer.fromJson(ngacPolicy, jsonProposalCreation);
					ngacPolicy = GraphSerializer.fromJson(ngacPolicy, jsonUnivOrg);
					//ngacPolicy = GraphSerializer.fromJson(ngacPolicy, jsonPds);
					//ngacPolicy = GraphSerializer.fromJson(ngacPolicy, jsonApproval);
					//ngacPolicy = GraphSerializer.fromJson(ngacPolicy, jsonGetFaculty);
					//ngacPolicy = GraphSerializer.fromJson(ngacPolicy, jsonCrossPolicy);
				} catch (PMException e) {
					log.debug("PM Exception: InitialConfigurationLoader : while loading NGAC base configuration. "
							+ e.toString());
				}
			} catch (IOException e) {
				log.debug("I/O Exception : InitialConfigurationLoader : while loading NGAC base configuration."
						+ e.toString());
			}

			log.info("PM Configuration loaded successfully");
		} else {
			log.info("PM graph is already loaded.");
		}
	}
	
	
	/**
	 * This method takes a null policy graph and generates a new graph policy based on template
	 * @param policy
	 * @return
	 */
	public Graph createAProposalGraph(Graph policy) {
		
			File file_proposal = getFileFromResources(Constants.PDS_TEMPLATE);
			String jsonProposalPolicy = "";
			try {
				jsonProposalPolicy = new String(Files.readAllBytes(Paths.get(file_proposal.getAbsolutePath())));
				log.info("Template file content:");
				try {
					policy = GraphSerializer.fromJson(policy, jsonProposalPolicy);					
				    if(policy == null)
				    {
				    	log.info("Proposal graph is null");
				    }
				} catch (PMException e) {
					log.debug("PM Exception: createAProposalGraph : while loading PDS base configuration. "
							+ e.toString());
				}
			} catch (IOException e) {
				log.debug("I/O Exception : createAProposalGraph : while loading PDS base configuration."
						+ e.toString());
			}

		
		return policy;
	}
	
	
	public Obligation loadObligation(String path) {
		Obligation obligation = null;
		try {
			File file = getFileFromResources(path); 
			InputStream is = new FileInputStream(file);
			obligation = EVRParser.parse(is);
		
		}catch(Exception e) {
			log.info("Exception: "+e.toString());
		}
		return obligation;
	}
	
	/**
	 * @param path is the location and name to save the json policy
	 * if path is provided null or empty string it will be saved to a default location
	 * @throws PMException
	 * @throws IOException
	 */
	public void savePolicy(String path) throws PMException, IOException {
		
		String policyString = GraphSerializer.toJson(ngacPolicy);
		
		File file ;
		if(path == null || path.isEmpty()) {
			file = new File(Constants.POLICY_CONFIG_OUTPUT_FILE);
		}
		else {
			file = new File(path);
		}
		
		if (file.createNewFile()) {            
            log.info("File has been created.");
        } else {
        
            log.info("File already exists.");
        }
				
		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(file));
		writer.write(policyString);
		writer.flush();

		if(writer != null)
			writer.close();		
		
	}

	public static Graph getPolicy() {
		
		return ngacPolicy;
	}

	public void setPolicy(Graph policy) {
		this.ngacPolicy = policy;
	}

	public static long getID() {
		return rand.nextLong();
	}

	private File getFileFromResources(String fileName) {
		ClassLoader classLoader = this.getClass().getClassLoader();

		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		} else {
			return new File(resource.getFile());
		}

	}

}
