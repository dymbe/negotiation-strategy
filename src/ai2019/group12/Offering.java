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

	private double time_threshold = 0.95;
	private double utility_threshold = 0.95;
	private int counter = 0;
	
	public Offering() {
	}

	public Offering(NegotiationSession negotiationSession) {
		this.negotiationSession = negotiationSession;
	}
	
	@Override
	public void init(NegotiationSession negoSession, OpponentModel om,
			OMStrategy omStrat, Map<String, Double> parameters)
			throws Exception {
		this.negotiationSession = negoSession;
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
		double relative_util_threshold = utility_threshold*negotiationSession.
				getMaxBidinDomain().getMyUndiscountedUtil();
	
		
		if (!(opponentModel instanceof NoModel)) {
			if (time <= time_threshold) {
				nextBid = bids_above_threshold();
			}

			
			
		} else {
			nextBid = last_moment_bids();
			
		}
		return nextBid;
		
	}
	

	public BidDetails bids_above_threshold() {
		
		Range our_range = new Range(negotiationSession.getMaxBidinDomain().getMyUndiscountedUtil()*utility_threshold, negotiationSession.getMaxBidinDomain().getMyUndiscountedUtil()+0.1) ;
				
		System.out.println("range: " + our_range);
		
		List<BidDetails> possible_bids = negotiationSession.getOutcomeSpace().getBidsinRange(our_range);
	
		System.out.println(possible_bids.size());
		
		int round = (int) negotiationSession.getTimeline().getCurrentTime();
		
		return possible_bids.get(round % possible_bids.size());
	}
	
	public BidDetails last_moment_bids() {
		
		return null;
	}
	
	@Override
	public String getName() {
		return "BraindeadCabbageOS";
	}

}