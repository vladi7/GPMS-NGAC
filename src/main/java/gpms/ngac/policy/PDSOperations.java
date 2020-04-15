package gpms.ngac.policy;

import gov.nist.csd.pm.epp.events.AssignToEvent;
import gov.nist.csd.pm.epp.events.DeassignFromEvent;
import gov.nist.csd.pm.epp.functions.FunctionExecutor;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.operations.OperationSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.PDP;
import gov.nist.csd.pm.pdp.decider.Decider;
import gov.nist.csd.pm.pdp.decider.PReviewDecider;
import gov.nist.csd.pm.pip.graph.Graph;
import gov.nist.csd.pm.pip.graph.GraphSerializer;
import gov.nist.csd.pm.pip.graph.MemGraph;
import gov.nist.csd.pm.pip.graph.model.nodes.Node;
import gov.nist.csd.pm.pip.graph.model.nodes.NodeType;
import gov.nist.csd.pm.pip.obligations.MemObligations;
import gov.nist.csd.pm.pip.obligations.evr.EVRException;
import gov.nist.csd.pm.pip.obligations.evr.EVRParser;
import gov.nist.csd.pm.pip.obligations.model.Obligation;
import gov.nist.csd.pm.pip.obligations.model.functions.Function;
import gov.nist.csd.pm.pip.prohibitions.MemProhibitions;
import gov.nist.csd.pm.pip.prohibitions.Prohibitions;
import gov.nist.csd.pm.pip.prohibitions.ProhibitionsSerializer;
import gov.nist.csd.pm.pip.prohibitions.model.Prohibition;

import static gov.nist.csd.pm.pip.graph.model.nodes.NodeType.O;
import static gov.nist.csd.pm.pip.graph.model.nodes.NodeType.OA;
import static gov.nist.csd.pm.pip.graph.model.nodes.NodeType.U;
import static gov.nist.csd.pm.pip.graph.model.nodes.NodeType.UA;
import static gov.nist.csd.pm.pip.graph.model.nodes.Properties.NAMESPACE_PROPERTY;

import gov.nist.csd.pm.epp.EPPOptions;

import gpms.DAL.DepartmentsPositionsCollection;
import gpms.model.GPMSCommonInfo;
import gpms.model.InvestigatorInfo;
import gpms.ngac.policy.customEvents.ApproveEvent;
import gpms.ngac.policy.customEvents.CreateoaEvent;
import gpms.ngac.policy.customEvents.DisapproveEvent;
import gpms.ngac.policy.customEvents.SubmitEvent;
import gpms.ngac.policy.customFunctions.IsNodeInListExecutor;
import gpms.ngac.policy.customFunctions.BMForExecutor;
import gpms.ngac.policy.customFunctions.ChairForExecutor;
import gpms.ngac.policy.customFunctions.ChairsForExecutor;
import gpms.ngac.policy.customFunctions.CompareNodeNamesExecutor;
import gpms.ngac.policy.customFunctions.ConcatExecutor;
import gpms.ngac.policy.customFunctions.CreateNodeExecutor1;
import gpms.ngac.policy.customFunctions.DeanForExecutor;
import gpms.ngac.policy.customFunctions.DeleteNodeExecutor;
import gpms.ngac.policy.customFunctions.EmailExecutor;
import gpms.rest.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * @author Md Nazmul Karim
 * @since May 20 2019
 * 
 *        This class is used to operate different functions over NGAC policy
 */
public class PDSOperations {

	private Graph ngacPolicy;
	public static Random rand = new Random();

	// private GpmsNgacObligations gpmsNgacObligations;

	public static HashMap<Long, Graph> proposalPolicies = new HashMap<Long, Graph>();
	public static HashMap<Long, Prohibitions> proposalProhibitions = new HashMap<Long, Prohibitions>();

	// public static Prohibitions proposalProhibitions = null;

	private static final Logger log = Logger.getLogger(PDSOperations.class.getName());

	private static NGACPolicyConfigurationLoader policyLoader;

	static Obligation obligation = null;
	static PDP pdp = null;

	public PDSOperations() {
		this.ngacPolicy = NGACPolicyConfigurationLoader.getPolicy();
		// gpmsNgacObligations = new GpmsNgacObligations();
		policyLoader = new NGACPolicyConfigurationLoader();

	}

	public PDSOperations(Graph gf) {
		this.ngacPolicy = gf;
		// gpmsNgacObligations = new GpmsNgacObligations();
	}

	public static PDP getPDP(Graph graph) throws PMException {
		DeleteNodeExecutor deleteNodeExecutor = new DeleteNodeExecutor();
		EmailExecutor emailExecutor = new EmailExecutor();
		IsAllowedToBeCoPIExecutor isAllowedToBeCoPIExecutor = new IsAllowedToBeCoPIExecutor();
		ChairForExecutor chairForExecutor = new ChairForExecutor();
		DeanForExecutor deanForExecutor = new DeanForExecutor();
		BMForExecutor bmForExecutor = new BMForExecutor();
		CreateNodeExecutor1 createNodeExecutor1 = new CreateNodeExecutor1();
		ConcatExecutor concatExecutor = new ConcatExecutor();
		ChairsForExecutor chairsForExecutor = new ChairsForExecutor();
		IsNodeInListExecutor areSomeNodesContainedInExecutor = new IsNodeInListExecutor();
		CompareNodeNamesExecutor compareNodesExecutor= new CompareNodeNamesExecutor();
		obligation = policyLoader.getObligation();
		EPPOptions eppOptions = new EPPOptions(deleteNodeExecutor, emailExecutor, chairForExecutor, deanForExecutor,bmForExecutor,isAllowedToBeCoPIExecutor, createNodeExecutor1, concatExecutor,chairsForExecutor, areSomeNodesContainedInExecutor, compareNodesExecutor);
		pdp = new PDP(new PAP(graph, new MemProhibitions(), new MemObligations()),eppOptions);
		pdp.getPAP().getObligationsPAP().add(obligation, true);

		return pdp;
	}

	public Graph getNGACPolicy() {
		return this.ngacPolicy;
	}

	public Graph getBacicNGACPolicy() {
		return policyLoader.reloadBasicConfig();
	}

	/**
	 * This function checks whether a user has permission for a task
	 * 
	 * @param userName
	 * @return true/false
	 */
	public boolean hasPermissionToCreateAProposal(String userName, Prohibitions prohibitions) {
		boolean hasPermission = true;

		HashMap map = Task.CREATE_PROPOSAL.getPermissionsSets();

		Iterator<Map.Entry<Attribute, HashSet>> itr = map.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry<Attribute, HashSet> entry = itr.next();
			log.info("Container = " + entry.getKey() + ", permission set = " + entry.getValue());
			hasPermission = UserPermissionChecker.checkPermissionAnyType(ngacPolicy, prohibitions, userName,
					U.toString(), (Attribute) entry.getKey(), new ArrayList<String>(entry.getValue()));
		}

		log.info("Create Proposal Permission : " + hasPermission);

		return hasPermission;
	}

	public boolean hasPermissionToCreateAProposal(Graph policy, GPMSCommonInfo userInfo, Prohibitions prohibitions) {
		boolean hasPermission = true;

		HashMap map = Task.CREATE_PROPOSAL.getPermissionsSets();

		Iterator<Map.Entry<Attribute, HashSet>> itr = map.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry<Attribute, HashSet> entry = itr.next();
			log.info("Container = " + entry.getKey() + ", permission set = " + entry.getValue());
			hasPermission = UserPermissionChecker.checkPermissionAnyType(policy, prohibitions, userInfo.getUserName(),
					U.toString(), (Attribute) entry.getKey(), new ArrayList<String>(entry.getValue()));
		}

		log.info("Create Proposal Permission : " + hasPermission);

		return hasPermission;
	}

	/**
	 * This function checks whether a user has permission to add another user as
	 * CoPI
	 * 
	 * @param userName             the performer
	 * @param coPIApproachableUser the intended user to be a CoPI
	 * @return true/false
	 */

	public boolean hasPermissionToAddAsCoPI(Graph policy, String userName, String coPIApproachableUser,
			Prohibitions prohibitions) {
		boolean hasPermission = true;

		HashMap map = Task.ADD_CO_PI.getPermissionsSets();

		Iterator<Map.Entry<Attribute, HashSet>> itr = map.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry<Attribute, HashSet> entry = itr.next();
			log.info("Container = " + entry.getKey() + ", permission set = " + entry.getValue());
			System.out.println("Container = " + entry.getKey() + ", permission set = " + entry.getValue());
			hasPermission = hasPermission && UserPermissionChecker.checkPermissionAnyType(policy, prohibitions, "PI",
					UA.toString(), (Attribute) entry.getKey(), new ArrayList<String>(entry.getValue()));
		}
		// try {
		// hasPermission = hasPermission && isChildrenFound(policy,
		// coPIApproachableUser, Constants.CO_PI_UA_LBL);
		// }
		// catch(PMException e){
		// e.printStackTrace();
		// }
		log.info("Add CoPI Permission : " + hasPermission);
		System.out.println("Add CoPI Permission : " + hasPermission);

		return hasPermission;
	}

	public boolean hasPermissionToDeleteCoPI(Graph policy, String userName, Prohibitions prohibitions) {
		boolean hasPermission = true;

		HashMap map = Task.DELETE_CO_PI.getPermissionsSets();

		Iterator<Map.Entry<Attribute, HashSet>> itr = map.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry<Attribute, HashSet> entry = itr.next();
			log.info("Container = " + entry.getKey() + ", permission set = " + entry.getValue());
			System.out.println("Container = " + entry.getKey() + ", permission set = " + entry.getValue());
			hasPermission = hasPermission && UserPermissionChecker.checkPermissionAnyType(policy, prohibitions, "PI",
					UA.toString(), (Attribute) entry.getKey(), new ArrayList<String>(entry.getValue()));
		}
//        try {
//        	hasPermission = hasPermission && isChildrenFound(policy, coPIApproachableUser, Constants.CO_PI_UA_LBL);
//        }
//        catch(PMException e){
//        	e.printStackTrace();
//        }
		log.info("Delete CoPI Permission : " + hasPermission);
		System.out.println("Delete CoPI Permission : " + hasPermission);

		return hasPermission;
	}

	public boolean hasPermissionToDeleteSP(Graph policy, String userName, Prohibitions prohibitions) {
		boolean hasPermission = true;

		HashMap map = Task.DELETE_SP.getPermissionsSets();

		Iterator<Map.Entry<Attribute, HashSet>> itr = map.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry<Attribute, HashSet> entry = itr.next();
			log.info("Container = " + entry.getKey() + ", permission set = " + entry.getValue());
			System.out.println("Container = " + entry.getKey() + ", permission set = " + entry.getValue());
			hasPermission = hasPermission && UserPermissionChecker.checkPermissionAnyType(policy, prohibitions, "PI",
					UA.toString(), (Attribute) entry.getKey(), new ArrayList<String>(entry.getValue()));
		}
//        try {
//        	hasPermission = hasPermission && isChildrenFound(policy, coPIApproachableUser, Constants.CO_PI_UA_LBL);
//        }
//        catch(PMException e){
//        	e.printStackTrace();
//        }
		log.info("Delete SP Permission : " + hasPermission);
		System.out.println("Delete SP Permission : " + hasPermission);

		return hasPermission;
	}

	/**
	 * This function checks whether a user has permission to add another user as SP
	 * 
	 * @param userName           the performer
	 * @param spApproachableUser the intended user to be a SP
	 * @return true/false
	 */

	public boolean hasPermissionToAddAsSP(Graph policy, String userName, String spApproachableUser,
			Prohibitions prohibitions) {
		boolean hasPermission = true;

		HashMap map = Task.ADD_SP.getPermissionsSets();

		Iterator<Map.Entry<Attribute, HashSet>> itr = map.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry<Attribute, HashSet> entry = itr.next();
			log.info("Container = " + entry.getKey() + ", permission set = " + entry.getValue());
			hasPermission = hasPermission && UserPermissionChecker.checkPermissionAnyType(policy, prohibitions, "CoPI",
					UA.toString(), (Attribute) entry.getKey(), new ArrayList<String>(entry.getValue()));
		}
//		try {
//			hasPermission = hasPermission
//					&& isChildrenFound(policy, spApproachableUser, Constants.SENIOtargetIdR_PERSON_UA_LBL);
//		} catch (PMException e) {
//			e.printStackTrace();
//		}
		log.info("Add SP Permission : " + hasPermission);

		return hasPermission;
	}

	/*
	 * public long createAProposal(String userName) throws PMException {
	 * printAccessState("Before User creates PDS", ngacPolicy); long randomId =
	 * getID(); //user creates a PDS and assigns it to Constants.PDS_ORIGINATING_OA
	 * Node pdsNode = this.ngacPolicy.createNode(randomId,
	 * createProposalId(randomId), OA, null);
	 * 
	 * long pdsOriginationOAID = getNodeID(ngacPolicy, Constants.PDS_ORIGINATING_OA,
	 * OA, null); ngacPolicy.assign(pdsNode.getID(), pdsOriginationOAID);
	 * 
	 * 
	 * long userID = getNodeID(ngacPolicy, userName, U, null);
	 * simulateAssignToEvent(ngacPolicy, userID,
	 * ngacPolicy.getNode(pdsOriginationOAID), pdsNode);
	 * 
	 * printAccessState("After User creates PDS", ngacPolicy);
	 * 
	 * return randomId; }
	 */

	public long createAProposal(String userName, String department, String email) throws PMException {

		long randomId = getID();

		try {
			String chairDept = DepartmentsPositionsCollection.adminUsers
					.get(DepartmentsPositionsCollection.departmentNames.get(department) + "CHAIR");
			System.out.println("DEPT2!"+department);
			String deanDept = DepartmentsPositionsCollection.adminUsers
					.get(DepartmentsPositionsCollection.departmentNames.get(department) + "DEAN");
			String bmDept = DepartmentsPositionsCollection.adminUsers
					.get(DepartmentsPositionsCollection.departmentNames.get(department) + "BM");
			Graph proposalPolicy = null;
			// proposalPolicy = policyLoader.createAProposalGraph(ngacPolicy); //loads
			// editing policy
			proposalPolicy = policyLoader.reloadBasicConfig();
			proposalPolicy = policyLoader.createAProposalGraph(proposalPolicy); // loads editing policy
			proposalPolicy = policyLoader.createAprovalGraph(proposalPolicy); // loads editing policy
			//System.out.println("GRAPH!!!!!!!!!!!!!"+GraphSerializer.toJson(proposalPolicy));
			Map<String, String> properties = new HashMap<String,String>();
			properties.put("workEmail", email);			
			properties.put("departmentChair", chairDept);
			properties.put("departmentDean", deanDept);
			properties.put("departmentBM", bmDept);

			proposalPolicy.updateNode(userName, properties);
			
			//System.out.println("GRAPH!!!!!!!!!!!!!"+GraphSerializer.toJson(proposalPolicy));
			//Node pdsNode = proposalPolicy.createNode("" + randomId, OA, null, Constants.PDS_ORIGINATING_OA);
			// log.info("ID:" + randomId);
			//long pdsOriginationOAID = getNodeID(proposalPolicy, Constants.PDS_ORIGINATING_OA, OA, null);
			//long userID = getNodeID(proposalPolicy, userName, U, null);
			// printAccessState("Initial configuration before op:", proposalPolicy);
			log.info("CREATE PROPOSAL: # nodes BEFORE:" + proposalPolicy.getNodes().size());
			getPDP(proposalPolicy).getEPP().processEvent(
					new CreateoaEvent(proposalPolicy.getNode(Constants.PDS_ORIGINATING_OA)), userName, "process");

			/*
			 * long COPIUAID = getNodeID(proposalPolicy, Constants.CO_PI_UA_LBL, UA, null);
			 * long COPIUID = getNodeID(proposalPolicy, "liliana", U, null);
			 * 
			 * 
			 * getPDP(proposalPolicy).getEPP().processEvent( new
			 * AssignToEvent(proposalPolicy.getNode(COPIUAID),
			 * proposalPolicy.getNode(COPIUID)), userID, getID());
			 * 
			 */

			log.info("Proposal policy saved:" + randomId + "|" + proposalPolicy.toString() + "|"
					+ proposalPolicy.getNodes().size());
			PDSOperations.proposalPolicies.put(randomId, proposalPolicy);
			// printAccessState("Initial configuration after op:", proposalPolicy);
			log.info("CREATE PROPOSAL: # nodes AFTER:" + proposalPolicy.getNodes().size());
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return randomId;
	}

	public PDP submitAProposal(String userName, String JSONGraph) throws PMException {
		List<String> list = new ArrayList<String>();
		long randomId = getID();
		Prohibitions prohibitions = new MemProhibitions();
		Graph graph = new MemGraph();
		GraphSerializer.fromJson(graph, JSONGraph);
		Graph proposalPolicy = null;
		// proposalPolicy = policyLoader.createAProposalGraph(ngacPolicy); //loads
		// editing policy
		if (graph.exists("super_pc_rep")) {
			graph.deleteNode("super_pc_rep");
        }
		proposalPolicy = policyLoader.reloadBasicConfig();
		proposalPolicy = policyLoader.createAProposalGraph(proposalPolicy); // loads editing policy
		proposalPolicy = policyLoader.createAprovalGraph(proposalPolicy); // loads editing policy
		//Node pdsNode = proposalPolicy.createNode("" + randomId, OA, null,Constants.SUBMISSION_INFO_OA_LBL);
		// log.info("ID:" + randomId);
		//long pdsOriginationOAID = getNodeID(proposalPolicy, Constants.SUBMISSION_INFO_OA_LBL, OA, null);
		//long userID = getNodeID(proposalPolicy, userName, U, null);

		// printAccessState("Initial configuration before op:", proposalPolicy);
		log.info("SUBMIT PROPOSAL: # nodes BEFORE:" + graph.getNodes().size());

		PDP pdp = getPDP(graph);
		pdp.getEPP().processEvent(new SubmitEvent(proposalPolicy.getNode(Constants.SUBMISSION_INFO_OA_LBL)), userName,
				"process");

		log.info("SUBMIT PROPOSAL: # nodes AFTER:" + graph.getNodes().size());
		list.add(GraphSerializer.toJson(pdp.getPAP().getGraphPAP()));
		list.add(ProhibitionsSerializer.toJson(pdp.getPAP().getProhibitionsPAP()));
		
		PReviewDecider decider = new PReviewDecider(pdp.getPAP().getGraphPAP(),pdp.getPAP().getProhibitionsPAP());
		System.out.println("RESULT1: "+ decider.check("Chair", "process" , "Signature-Info", "w"));
		System.out.println("RESULT2: "+ decider.check("chaircomputerscience", "process" , "Signature-Info", "w"));

		return pdp;
	}

	public static void addCoPI(String userName, String CoPINode, String CoPIUAID, Graph intialGraph) throws Exception {
		// log.info("ID:" + randomId);
		// long CoPIOAID = getNodeID(intialGraph, Constants.CO_PI_OA_LBL, OA, null);
		//long userID = getNodeID(intialGraph, userName, U, null);
		// long CoPIID = getNodeID(intialGraph, CoPINode, U, null);

		// printAccessState("Initial configuration before op:", proposalPolicy);
		log.info("ADD CoPI: # nodes BEFORE:" + intialGraph.getNodes().size());
		if (intialGraph.exists("super_pc_rep")) {
			intialGraph.deleteNode("super_pc_rep");
        }
		// intialGraph.assign(CoPINode, CoPIUAID);
		System.out.println("SIZE:"+intialGraph.getNodes().size());
		PDP pdp = getPDP(intialGraph);
		pdp.getEPP().processEvent(new AssignToEvent(intialGraph.getNode(CoPIUAID), intialGraph.getNode(CoPINode)),
				userName, "process");
		
		log.info("User Name " + intialGraph.getNode(userName).getName());
		log.info("ADD CoPI: # nodes AFTER:" + intialGraph.getNodes().size());
		log.info("CoPIU Name" + intialGraph.getNode(CoPINode).getName());
		// System.out.println(GraphSerializer.toJson(intialGraph));
		if(!intialGraph.getParents(CoPINode).contains("CoPI")) {
			throw new Exception();
		}
		for (String parent : intialGraph.getParents(CoPINode)) {
			log.info("Parents: " + intialGraph.getNode(parent).getName());
		}

		// get all of the users in the graph
	}

	public static void addSP(String userName, String SPNode, String SPUAID, Graph intialGraph) throws Exception {
		// log.info("ID:" + randomId);
		// long CoPIOAID = getNodeID(intialGraph, Constants.CO_PI_OA_LBL, OA, null);
		//long userID = getNodeID(intialGraph, userName, U, null);
		// long CoPIID = getNodeID(intialGraph, CoPINode, U, null);
		if (intialGraph.exists("super_pc_rep")) {
			intialGraph.deleteNode("super_pc_rep");
        }
		// printAccessState("Initial configuration before op:", proposalPolicy);
		log.info("ADD SP: # nodes BEFORE:" + intialGraph.getNodes().size());

		// intialGraph.assign(SPNode, SPUAID);

		PDP pdp = getPDP(intialGraph);
		pdp.getEPP().processEvent(new AssignToEvent(intialGraph.getNode(SPUAID), intialGraph.getNode(SPNode)), userName,
				"process");
		log.info("User Name " + intialGraph.getNode(userName).getName());
		log.info("ADD SP: # nodes AFTER:" + intialGraph.getNodes().size());
		log.info("CoPIU Name" + intialGraph.getNode(SPNode).getName());
		// System.out.println(GraphSerializer.toJson(intialGraph));
		if(!intialGraph.getParents(SPNode).contains("SP")) {
			throw new Exception();
		}
		for (String parent : intialGraph.getParents(SPNode)) {
			log.info("Parents: " + intialGraph.getNode(parent).getName());
		}
		for (String parent : intialGraph.getParents(SPNode)) {
			log.info("Children: " + intialGraph.getNode(parent).getName());
		}
	}

	public static void deleteCoPI(String userName, String CoPINode, String CoPIUAID, Graph intialGraph) throws PMException {

		// log.info("ID:" + randomId);
		// long CoPIOAID = getNodeID(intialGraph, Constants.CO_PI_OA_LBL, OA, null);
		long userID = 0;
		//try {
			//userID = getNodeID(intialGraph, userName, U, null);
		//} catch (PMException e) {
		//	String string = "node was not found: " + e.getMessage();
			//log.info(string);
			//return;
		//}
		// long CoPIID = getNodeID(intialGraph, CoPINode, U, null);

		// printAccessState("Initial configuration before op:", proposalPolicy);
		log.info("DELETE CoPI: # nodes BEFORE:" + intialGraph.getNodes().size());

		// intialGraph.deassign(CoPINode, CoPIUAID);
		if (intialGraph.exists("super_pc_rep")) {
			intialGraph.deleteNode("super_pc_rep");
        }
		for(String s:intialGraph.getChildren("CoPI")) {
			System.out.println("Children before: "+s);
		}
		PDP pdp = getPDP(intialGraph);
		try {
			System.out.println();
			pdp.getEPP().processEvent(
					new DeassignFromEvent(intialGraph.getNode(CoPIUAID), intialGraph.getNode(CoPINode)), userName,
					"process");
			//log.info("Found so many nodes: " + intialGraph.search("tomtom", "O", new HashMap<String, String>()).size());
		} catch (NoSuchElementException ex) {
			ex.printStackTrace();
		}
		//log.info("User Name " + intialGraph.getNode(userID).getName());
		log.info("DELETE CoPI: # nodes AFTER:" + intialGraph.getNodes().size());
		log.info("CoPIU Name" + intialGraph.getNode(CoPINode).getName());
		// System.out.println(GraphSerializer.toJson(intialGraph));
		for(String s:intialGraph.getChildren("CoPI")) {
			System.out.println("Children after: "+s);
		}
		for (String parent : intialGraph.getParents(CoPIUAID)) {
			log.info("Parents: " + intialGraph.getNode(parent).getName());
		}
		PReviewDecider decider = new PReviewDecider(pdp.getPAP().getGraphPAP(),pdp.getPAP().getProhibitionsPAP());

		//System.out.println("RESULT2: "+ decider.check("chairchemistry", "process" , "Signature-Info", "w"));
		for(String s: pdp.getPAP().getGraphPAP().getChildren("Chair")) {
			System.out.println("CHILDREN DELETE COPI"+ s);
		}
		// get all of the users in the graph
	}

	public static void deleteSP(String userName, String SPNode, String SPUAID, Graph intialGraph) throws PMException {
		// log.info("ID:" + randomId);
		// long CoPIOAID = getNodeID(intialGraph, Constants.CO_PI_OA_LBL, OA, null);
		//long userID = 0;
		//try {
			//userID = getNodeID(intialGraph, userName, U, null);
		//} catch (PMException e) {
			//String string = "node was not found: " + e.getMessage();
			//log.info(string);
			//return;
		//}
		// long CoPIID = getNodeID(intialGraph, CoPINode, U, null);

		// printAccessState("Initial configuration before op:", proposalPolicy);
		log.info("DELETE SP: # nodes BEFORE:" + intialGraph.getNodes().size());
		if (intialGraph.exists("super_pc_rep")) {
			intialGraph.deleteNode("super_pc_rep");
        }
		// intialGraph.deassign(SPNode, SPUAID);
		for(String s:intialGraph.getChildren("SP")) {
			System.out.println("Children before: "+s);
		}
		PDP pdp = getPDP(intialGraph);
		try {
			pdp.getEPP().processEvent(new DeassignFromEvent(intialGraph.getNode(SPUAID), intialGraph.getNode(SPNode)),
					userName, "process");
			//log.info(
				//	"Found so many nodes: " + intialGraph.search("liliana", "O", new HashMap<String, String>()).size());
		} catch (NoSuchElementException ex) {
			ex.printStackTrace();
		}
		for(String s:intialGraph.getChildren("SP")) {
			System.out.println("Children after: "+s);
		}
		log.info("User Name " + intialGraph.getNode(userName).getName());
		log.info("DELETE SP: # nodes AFTER:" + intialGraph.getNodes().size());
		log.info("SP Name" + intialGraph.getNode(SPNode).getName());
		// System.out.println(GraphSerializer.toJson(intialGraph));

		for (String parent : intialGraph.getParents(SPUAID)) {
			log.info("Parents: " + intialGraph.getNode(parent).getName());
		}

		// get all of the users in the graph
	}
		public static void addApprovalEntity(String ChairU, String ChairUA, Graph intialGraph) throws PMException {
			// log.info("ID:" + randomId);
			// long CoPIOAID = getNodeID(intialGraph, Constants.CO_PI_OA_LBL, OA, null);
			// long CoPIID = getNodeID(intialGraph, CoPINode, U, null);

			// printAccessState("Initial configuration before op:", proposalPolicy);
			log.info("ADD SP: # nodes BEFORE:" + intialGraph.getNodes().size());
			
			//intialGraph.assign(SPNode, SPUAID);
			Node user = intialGraph.createNode("userForChair", U, null, ChairUA);
			PDP pdp = getPDP(intialGraph);
			pdp.getEPP().processEvent(new AssignToEvent(intialGraph.getNode(ChairUA), intialGraph.getNode(ChairU)),
					"userForChair","process");
			
			intialGraph.deleteNode("userForChair");
			//log.info("User Name " + intialGraph.getNode(userID).getName());
			log.info("ADD SP: # nodes AFTER:" + intialGraph.getNodes().size());
			//log.info("CoPIU Name" + intialGraph.getNode(SPNode).getName());
			// System.out.println(GraphSerializer.toJson(intialGraph));
			
//			for (Long parent : intialGraph.getParents(SPNode)) {
//				log.info("Children: " + intialGraph.getNode(parent).getName());
//			}
		}
		public PDP chairApprove(String userName, String JSONGraph) throws PMException {
			Graph graph = new MemGraph();
			GraphSerializer.fromJson(graph, JSONGraph);
			// proposalPolicy = policyLoader.createAProposalGraph(ngacPolicy); //loads
			// editing policy
			if (graph.exists("super_pc_rep")) {
				graph.deleteNode("super_pc_rep");
	        }
			//Node pdsNode = proposalPolicy.createNode("" + randomId, OA, null,Constants.SUBMISSION_INFO_OA_LBL);
			// log.info("ID:" + randomId);
			//long pdsOriginationOAID = getNodeID(proposalPolicy, Constants.SUBMISSION_INFO_OA_LBL, OA, null);
			//long userID = getNodeID(proposalPolicy, userName, U, null);

			// printAccessState("Initial configuration before op:", proposalPolicy);
			log.info("SUBMIT PROPOSAL: # nodes BEFORE:" + graph.getNodes().size());

			PDP pdp = getPDP(graph);
			pdp.getEPP().processEvent(new ApproveEvent(graph.getNode(Constants.CHAIR_APPROVAL)), userName,
					"process");

			log.info("SUBMIT PROPOSAL: # nodes AFTER:" + graph.getNodes().size());
			
			
			
			
			PReviewDecider decider = new PReviewDecider(pdp.getPAP().getGraphPAP(),pdp.getPAP().getProhibitionsPAP());
			System.out.println("RESULT1: "+ decider.check("Chair", "process" , "Signature-Info", "w"));
			System.out.println("RESULT2: "+ decider.check("chaircomputerscience", "process" , "Signature-Info", "w"));

			return pdp;
		}
		public PDP chairDisapprove(String userName, String JSONGraph) throws PMException {
			Graph graph = new MemGraph();
			GraphSerializer.fromJson(graph, JSONGraph);
			// proposalPolicy = policyLoader.createAProposalGraph(ngacPolicy); //loads
			// editing policy
			if (graph.exists("super_pc_rep")) {
				graph.deleteNode("super_pc_rep");
	        }
			//Node pdsNode = proposalPolicy.createNode("" + randomId, OA, null,Constants.SUBMISSION_INFO_OA_LBL);
			// log.info("ID:" + randomId);
			//long pdsOriginationOAID = getNodeID(proposalPolicy, Constants.SUBMISSION_INFO_OA_LBL, OA, null);
			//long userID = getNodeID(proposalPolicy, userName, U, null);

			// printAccessState("Initial configuration before op:", proposalPolicy);
			log.info("SUBMIT PROPOSAL: # nodes BEFORE:" + graph.getNodes().size());

			PDP pdp = getPDP(graph);
			pdp.getEPP().processEvent(new DisapproveEvent(graph.getNode(Constants.CHAIR_APPROVAL)), userName,
					"process");

			log.info("SUBMIT PROPOSAL: # nodes AFTER:" + graph.getNodes().size());
			
			
			
			
			PReviewDecider decider = new PReviewDecider(pdp.getPAP().getGraphPAP(),pdp.getPAP().getProhibitionsPAP());
			System.out.println("RESULT1: "+ decider.check("Chair", "process" , "Signature-Info", "w"));
			System.out.println("RESULT2: "+ decider.check("chaircomputerscience", "process" , "Signature-Info", "w"));

			return pdp;
		}
	private String createProposalId(long id) {
		return "PDS" + id;
	}

	/**
	 * Utility method to print the current access state to the console.
	 * 
	 * @param step  the name of the step
	 * @param graph the graph to determine permissions
	 */
	public static void printAccessState(String step, Graph graph) throws PMException {
		System.out.println("############### Access state for " + step + " ###############");

		// initialize a PReviewDecider to make decisions
		PReviewDecider decider = new PReviewDecider(graph);

		// get all of the users in the graph
		Set<Node> search = graph.search(U, null);
		for (Node user : search) {
			// there is a super user that we'll ignore
			if (user.getName().equals("super")) {
				continue;
			}

			log.info(user.getName());
			// get all of the nodes accessible for the current user
//			Map<Long, Set<String>> accessibleNodes = decider.getAccessibleNodes(user.getName(), 100);
//			for (long objectID : accessibleNodes.keySet()) {
//				Node obj = graph.getNode(objectID);
//				log.info("\t" + obj.getName() + " -> " + accessibleNodes.get(objectID));
//			}
		}
		log.info("############### End Access state for " + step + "############");
	}

	public static void printGraph(Graph graph) throws PMException {
		List<Node> nodes = (List<Node>) graph.getNodes();
		System.out.println("***********Nodes:************");
		for (Node node : nodes) {
			System.out.println(node.getName());
		}

	}

	/**
	 * Method to simulate an obligation. All obligations used in this example are
	 * triggered by an "assign to" event, so we'll assume there is a child node
	 * being assigned to a target node.
	 * 
	 * @param policy     the graph
	 * @param userID     the ID of the user that triggered the event
	 * @param targetNode the node that the event happens on
	 * @throws PMException
	 */
//    private void simulateAssignToEvent(Graph policy, long userID, Node targetNode, Node childNode) throws PMException {
//        // check if the target of the event is a particular container and execute the corresponding "response"
//        if(targetNode.getID() == getNodeID(policy, Constants.PDS_ORIGINATING_OA, OA, null)) {
//            //gpmsNgacObligations.createPDS(graph, userID, childNode);
//            gpmsNgacObligations.createPDS(policy, userID, childNode);
//        }
//        else if(targetNode.getID() == getNodeID(policy, "CoPI", OA, null)) {
//        	gpmsNgacObligations.addCoPI(policy, childNode);
//        } else if(targetNode.getID() == getNodeID(policy, "SP", OA, null)) {
//        	gpmsNgacObligations.addSP(policy, childNode);
//        } else if(targetNode.getID() == getNodeID(policy, "submitted_pdss", OA, null)) {
//        	gpmsNgacObligations.submitPDS(policy, childNode);
//        } else if(targetNode.getID() == getNodeID(policy, "cs_chair_approval", OA, null) ||
//                targetNode.getID() == getNodeID(policy, "math_chair_approval", OA, null)) {
//        	gpmsNgacObligations.chairApproval(policy, childNode);
//        }  else if(targetNode.getID() == getNodeID(policy, "coen_dean_approval", OA, null) ||
//                targetNode.getID() == getNodeID(policy, "coas_dean_approval", OA, null)) {
//        	gpmsNgacObligations.deanApproval(policy, childNode);
//        }
//    }

//	public static long getNodeID(Graph graph, String name, NodeType type, Map<String, String> properties)
//			throws PMException {
//		Set<Node> search = graph.search(name, type.toString(), properties);
//		if (search.isEmpty()) {
//			throw new PMException("no node with name " + name + ", type " + type + ", and properties " + properties);
//		}
//
//		return search.iterator().next().getID();
//	}

	public static long getID() {
		return rand.nextLong();
	}

	public boolean doesPolicyBelongToNGAC(HashMap<String, String> attr) {
		if (attr.get("position.type") != null && attr.get("proposal.section").equalsIgnoreCase("Whole Proposal")
				&& attr.get("proposal.action").equalsIgnoreCase("Add"))
			return true;
		return false;
	}

	public boolean isChildrenFound(Graph policy, String name, String parent) throws PMException {
		boolean found = false;
		// get all of the users in the graph
		Node userAttNode = policy.getNode(parent);

		//System.out.println(search.size());

		//for (Node userAttNode : search) {

			Set<String> childIds = policy.getChildren(userAttNode.getName());
			log.info("No of Children Assigned on " + parent + " :" + childIds.size() + "|" + childIds);

			//long sourceNode = getNodeID(policy, name, U, null);

			log.info("We are looking for:" + name);

			if (childIds.contains(name)) {
				found = true;
				log.info("found");
			} else {
				log.info("not found");
			}
		//}
		return found;

	}

	public void testUsersAccessights_Proposal_created(Graph proposalPolicy) {
		
			//long userIdNazmul = PDSOperations.getNodeID(proposalPolicy, "nazmul", NodeType.U, null); // tanure track +
																										// cs
			//long userIdAmy = PDSOperations.getNodeID(proposalPolicy, "amy", NodeType.U, null); // adjunct
			//long userIdtom = PDSOperations.getNodeID(proposalPolicy, "tomtom", NodeType.U, null); // adjunct
			//long userIdSamer = PDSOperations.getNodeID(proposalPolicy, "samer", NodeType.U, null); // CE

			//long userIdCSChair = PDSOperations.getNodeID(proposalPolicy,
			//		DepartmentsPositionsCollection.adminUsers.get("CSCHAIR"), NodeType.U, null);

			log.info("************Start**************");
			Attribute att = new Attribute("Budget-Info", NodeType.OA);
			String[] ops = new String[] { "w" };
			boolean hasPermission = UserPermissionChecker.checkPermissionAnyType(proposalPolicy, new MemProhibitions(),
					"nazmul", "U", att, Arrays.asList(ops));
			log.info("Nazmul:Budget-Info(w):" + hasPermission);

			att = new Attribute("Budget-Info", NodeType.OA);
			ops = new String[] { "w" };
			String userName = "tomtom";
			hasPermission = UserPermissionChecker.checkPermissionAnyType(proposalPolicy, new MemProhibitions(),
					userName, "U", att, Arrays.asList(ops));
			log.info("tomtom:Budget-Info(w):" + hasPermission);

			att = new Attribute("Project-Info", NodeType.OA);
			ops = new String[] { "r" };
			userName = "tomtom";
			hasPermission = UserPermissionChecker.checkPermissionAnyType(proposalPolicy, new MemProhibitions(),
					userName, "U", att, Arrays.asList(ops));
			log.info("Project-Info(w):" + hasPermission);
			log.info("**************End************");

		
	}

	public void testUsersAccessights_Proposal_not_created() {
		
			log.info("************Start**************");
			//long userIdNazmul = PDSOperations.getNodeID(ngacPolicy, "nazmul", NodeType.U, null); // tanure track + cs
			//long userIdAmy = PDSOperations.getNodeID(ngacPolicy, "amy", NodeType.U, null); // adjunct
			Attribute att = new Attribute("PDS", NodeType.OA);
			String[] ops = new String[] { "create-oa" };
			boolean hasPermission = UserPermissionChecker.checkPermissionAnyType(ngacPolicy, new MemProhibitions(),
					"nazmul", "U", att, Arrays.asList(ops));
			log.info("Nazmul:Create Proposal:" + hasPermission);
			hasPermission = UserPermissionChecker.checkPermissionAnyType(ngacPolicy, new MemProhibitions(), "amy", "U",
					att, Arrays.asList(ops));
			log.info("Amy:Create Proposal:" + hasPermission);
			log.info("************End**************");
		
	}

}
