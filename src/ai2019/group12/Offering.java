package ai2019.group12;

import java.util.List;
import java.util.Map;

import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.NoModel;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;
import genius.core.misc.*;
import genius.core.boaframework.SortedOutcomeSpace;

public class Offering extends OfferingStrategy {

	private double timeThreshold = 0.95;
	private double utilityThreshold = 0.95;
	
	public Offering() {
	}

	public Offering(NegotiationSession negotiationSession) {
		this.negotiationSession = negotiationSession;
	}
	
	@Override
	public void init(NegotiationSession negotiationSession, OpponentModel om,
			OMStrategy omStrat, Map<String, Double> parameters)
			throws Exception {
		this.negotiationSession = negotiationSession;
		SortedOutcomeSpace space = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(space);
	}
	
	@Override
	public BidDetails determineOpeningBid() {
		return negotiationSession.getMaxBidinDomain();
	}

	@Override
	public BidDetails determineNextBid() {
		double time = negotiationSession.getTime();
		/*double relativeUtilThreshold = utilityThreshold*negotiationSession.
				getMaxBidinDomain().getMyUndiscountedUtil(); Delete this? */
	
		if (!(opponentModel instanceof NoModel)) {
			if (time <= timeThreshold) {
				nextBid = getBidAboveThreshold();
			}
		} else {
			nextBid = getLastMomentBid();
			
		}
		return nextBid;
		
	}
	

	public BidDetails getBidAboveThreshold() {
		double lowerBound = negotiationSession.getMaxBidinDomain().getMyUndiscountedUtil() * utilityThreshold;
		double upperBound = negotiationSession.getMaxBidinDomain().getMyUndiscountedUtil() + 0.1;
		
		List<BidDetails> possibleBids = negotiationSession
				.getOutcomeSpace()
				.getBidsinRange(new Range(lowerBound, upperBound));
	
		int currentRound = (int) negotiationSession.getTimeline().getCurrentTime();
		
		return possibleBids.get(currentRound % possibleBids.size());
	}
	
	public BidDetails getLastMomentBid() {
		return null;
	}
	
	@Override
	public String getName() {
		return "BraindeadCabbageOS";
	}

}