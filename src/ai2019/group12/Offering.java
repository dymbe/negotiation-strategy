package ai2019.group12;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.NoModel;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;
//import sun.awt.shell.Win32ShellFolder2.SystemIcon;
import genius.core.misc.*;

public class Offering extends OfferingStrategy {

	private double time_threshold = 0.95;
	private double utility_threshold = 0.95;


	
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
		return negotiationSession.getMaxBidinDomain();
	}

	@Override
	public BidDetails determineNextBid() {
		double time = negotiationSession.getTime();
		double relative_util_threshold = utility_threshold*negotiationSession.
				getMaxBidinDomain().getMyUndiscountedUtil();
	
		
		if (!(opponentModel instanceof NoModel)) {
			if (time <= time_threshold) {
				//next_bid =
				bids_above_threshold();
				
			}

			
			
		} else {
			
			
		}
		return nextBid;
		
	}
	

	public void bids_above_threshold() {
		
		Range our_range = new Range(negotiationSession.getMaxBidinDomain().getMyUndiscountedUtil()*utility_threshold, negotiationSession.getMaxBidinDomain().getMyUndiscountedUtil()+0.1) ;
		
		//List<BidDetails> possible_bids = negotiationSession.getOutcomeSpace().getBidsinRange(our_range) ;
		
		System.out.print(negotiationSession.getMaxBidinDomain().getMyUndiscountedUtil()*utility_threshold);
		System.out.print(negotiationSession.getMaxBidinDomain().getMyUndiscountedUtil());
		//System.out.print(negotiationSession.getOutcomeSpace().getBidsinRange(our_range));
		
		
	}
	
	
	@Override
	public String getName() {
		return "BraindeadCabbageOS";
	}

}