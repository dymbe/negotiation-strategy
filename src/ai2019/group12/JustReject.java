package ai2019.group12;

import java.util.Map;

import genius.core.boaframework.AcceptanceStrategy;
import genius.core.boaframework.Actions;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;

public class JustReject extends AcceptanceStrategy {
	
	/**
	 * Empty constructor for the BOA framework.
	 */
	public JustReject() {
	}

	public JustReject(NegotiationSession negotiationSession, OfferingStrategy strat) {
		this.negotiationSession = negotiationSession;
		this.offeringStrategy = strat;
	}
	
	@Override
	public void init(NegotiationSession negotiationSession, OfferingStrategy os,
			OpponentModel opponentModel, Map<String, Double> parameters)
			throws Exception {
		this.negotiationSession = negotiationSession;
		this.offeringStrategy = os;
	}

	@Override
	public Actions determineAcceptability() {
		return Actions.Reject;
	}

	@Override
	public String getName() {
		return "JustReject";
	}

}
