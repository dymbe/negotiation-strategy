package ai2019.group12;

import java.util.List;
import java.util.Random;

import genius.core.bidding.BidDetails;
import genius.core.boaframework.OMStrategy;

public class BraindeadCabbageOMS extends OMStrategy {
	
	private double opponentUilityPercentageThreshold = 0.9;
	private Random random = new Random(1337);

	@Override
	public BidDetails getBid(List<BidDetails> bidsInRange) {
		bidsInRange.sort((bid1, bid2) -> {
			double bid1utility = model.getOpponentUtilitySpace().getUtility(bid1.getBid());
			double bid2utility = model.getOpponentUtilitySpace().getUtility(bid2.getBid());
			if (bid1utility < bid2utility) return 1;
			else return -1;
		});
		
		int toIndex = (int) (bidsInRange.size() * (1 - opponentUilityPercentageThreshold)) + 1;
		
		return bidsInRange.subList(0, toIndex).get(random.nextInt(toIndex));
	}

	@Override
	public boolean canUpdateOM() {
		return true;
	}

	@Override
	public String getName() {
		return "BraindeadCabbageOMS";
	}
	
}
