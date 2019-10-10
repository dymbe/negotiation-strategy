package ai2019.group12;

import java.util.Map;

import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;

public class Offering extends OfferingStrategy {

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
	}
	
	@Override
	public BidDetails determineOpeningBid() {
		return negotiationSession.getMinBidinDomain();
	}

	@Override
	public BidDetails determineNextBid() {
		return negotiationSession.getMaxBidinDomain();
	}

	@Override
	public String getName() {
		return "BraindeadCabbageOS";
	}

}