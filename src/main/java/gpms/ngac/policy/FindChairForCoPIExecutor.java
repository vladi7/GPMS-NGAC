package gpms.ngac.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nist.csd.pm.epp.FunctionEvaluator;
import gov.nist.csd.pm.epp.events.EventContext;
import gov.nist.csd.pm.epp.functions.FunctionExecutor;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pdp.PDP;
import gov.nist.csd.pm.pip.graph.model.nodes.Node;
import gov.nist.csd.pm.pip.obligations.model.functions.Arg;
import gov.nist.csd.pm.pip.obligations.model.functions.Function;
import static gov.nist.csd.pm.pip.graph.model.nodes.NodeType.U;

public class FindChairForCoPIExecutor implements FunctionExecutor{
	@Override
    public String getFunctionName() {
        return "findChairForCoPI";
    }

    @Override
    public int numParams() {
        return 1;
    }

    @Override
    public Node exec(EventContext eventCtx, long userID, long processID, PDP pdp, Function function, FunctionEvaluator functionEvaluator) throws PMException {
        List<Arg> args = function.getArgs();
        if (args == null || args.size() < numParams() || args.size() > numParams()) {
            throw new PMException(getFunctionName() + " expected at least two arguments (name and type) but found none");
        }

        Arg arg = args.get(0);
        Function argFunction = arg.getFunction();
        if (argFunction == null) {
            throw new PMException(getFunctionName() + " expected the first argument to be a function but it was null");
        }

        Node node = functionEvaluator.evalNode(eventCtx, userID, processID, pdp, argFunction);
      
        String departmentChair = node.getProperties().get("departmentChair");
        
        Set<Node> nodes = pdp.getPAP().getGraphPAP().search(departmentChair, "U", null);
        
        return nodes.iterator().next();
    }
}
