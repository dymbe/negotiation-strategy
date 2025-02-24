package ai2019.group12;

import java.util.List;

import java.util.Map;
import java.util.Random;

import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.NoModel;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;
import genius.core.misc.*;
import genius.core.uncertainty.UserModel;
import genius.core.boaframework.SortedOutcomeSpace;

public class BraindeadCabbageOS extends OfferingStrategy {

	private double timeThreshold = 0.90;
	private double utilityThreshold = 0.95;
	private double finalUtilityTreshhold = 0.7;
	private Random random = new Random(1337);
	
	/**
	 * Empty constructor for the BOA framework.
	 */
	public BraindeadCabbageOS() {
	}

	public BraindeadCabbageOS(NegotiationSession negotiationSession) {
		this.negotiationSession = negotiationSession;
	}
	
	@Override
	public void init(NegotiationSession negotiationSession, OpponentModel om,
			OMStrategy omStrat, Map<String, Double> parameters)
			throws Exception {
		this.negotiationSession = negotiationSession;
		this.omStrategy = omStrat;
		SortedOutcomeSpace space = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(space);
	}
	
	@Override
	public BidDetails determineOpeningBid() {
		
		UserModel userModel = negotiationSession.getUserModel();
		
		if (userModel != null) {
			List<Bid> bidOrder = userModel.getBidRanking().getBidOrder();
			return new BidDetails(bidOrder.get(bidOrder.size()-1), 0);
		}
		
		else {
			return negotiationSession.getMaxBidinDomain();
		}	
	}
	
	
	@Override
	public BidDetails determineNextBid() {
		double time = negotiationSession.getTime();
		
		UserModel userModel = negotiationSession.getUserModel();
		
		//uncertain
		if (userModel != null) {
		
			List<Bid> bidOrder = userModel.getBidRanking().getBidOrder();
			
			int currentRound = (int) negotiationSession.getTimeline().getCurrentTime();
			
			List<Bid> goodBids = bidOrder.subList((int) (bidOrder.size() * 0.7), bidOrder.size());
			
			return new BidDetails(goodBids.get(goodBids.size()-1 - currentRound % goodBids.size() ), 0);			
		}
		
		else {
	
			if (time <= timeThreshold) {
				nextBid = getBidAboveThreshold();
			}
			else if (!(opponentModel instanceof NoModel)){
				nextBid = omStrategy.getBid(negotiationSession.getOutcomeSpace(), getRange());
			}
			else {
				nextBid = getLastMomentBid();
			}
			
			return nextBid;
		}
	}
	
	private Range getRange() {
		double timeOfLastMoment = (negotiationSession.getTime() - timeThreshold) / (1 - timeThreshold);
		double lowerBound = utilityThreshold - (utilityThreshold - finalUtilityTreshhold) * timeOfLastMoment;
		return new Range(lowerBound, 1.1);
	}
	
	private List<BidDetails> getBidOver(double lowerBound) {
		return negotiationSession
				.getOutcomeSpace()
				.getBidsinRange(new Range(lowerBound, 1.1));
	}

	private BidDetails getBidAboveThreshold() { 
		List<BidDetails> possibleBids = getBidOver(utilityThreshold);
		
		int currentRound = (int) negotiationSession.getTimeline().getCurrentTime();
		
		return possibleBids.get(currentRound % possibleBids.size());
	}
	
	public BidDetails getLastMomentBid() {
		double timeOfLastMoment = (negotiationSession.getTime() - timeThreshold) / (1 - timeThreshold);
		double lowerBound = utilityThreshold - (utilityThreshold - finalUtilityTreshhold) * timeOfLastMoment;
		
		List<BidDetails> possibleBids = getBidOver(lowerBound);
		
		return possibleBids.get(random.nextInt(possibleBids.size()));
	}
	
	@Override
	public String getName() {
		return "BraindeadCabbageOS";
	}

}