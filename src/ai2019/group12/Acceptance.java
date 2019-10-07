package ai2019.group12;

import java.util.Map;

import genius.core.boaframework.AcceptanceStrategy;
import genius.core.boaframework.Actions;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;

public class Acceptance extends AcceptanceStrategy {
	
	private double timeThreshold;
	private double a;
	
	/**
	 * Empty constructor for the BOA framework.
	 */
	public Acceptance() {
	}

	public Acceptance(NegotiationSession negotiationSession, OfferingStrategy strat) {
		this.negotiationSession = negotiationSession;
		this.offeringStrategy = strat;
	}
	
	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat,
			OpponentModel opponentModel, Map<String, Double> parameters)
			throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		
		if (parameters.get("timeThreshold") != null || parameters.get("a") != null) {
			timeThreshold = parameters.get("timeThreshold");
			a = parameters.get("a");
		} else {
			timeThreshold = 0.95;
			a = 0.95;
		}
	}

	@Override
	public Actions determineAcceptability() {
		double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory()
				.getLastBidDetails().getMyUndiscountedUtil();
		double bestOpponentBidUtil = negotiationSession.getOpponentBidHistory()
				.getBestBidDetails().getMyUndiscountedUtil();
		double nextMyBidUtil = offeringStrategy.getNextBid()
				.getMyUndiscountedUtil();
		double myWorstBidUtil = negotiationSession.getOwnBidHistory()
				.getWorstBidDetails().getMyUndiscountedUtil();
		
		boolean isBetterThanNext = lastOpponentBidUtil >= nextMyBidUtil;
		boolean isBetterThanMyWorst = lastOpponentBidUtil >= myWorstBidUtil;
		boolean isGoodLastResort = negotiationSession.getTime() >= timeThreshold &&
				a * bestOpponentBidUtil < lastOpponentBidUtil;
		
		if (isBetterThanNext || isBetterThanMyWorst || isGoodLastResort) {
			return Actions.Accept;
		} else {
			return Actions.Reject;
		}
	}

	@Override
	public String getName() {
		return "BraindeadCabbage";
	}

}
