package ai2019.group12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.BOAparameter;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OpponentModel;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Objective;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.Evaluator;
import genius.core.utility.EvaluatorDiscrete;

/**
 * BOA framework implementation of the HardHeaded Frequecy Model.
 * 
 * Default: learning coef l = 0.2; learnValueAddition v = 1.0
 * 
 * paper: https://ii.tudelft.nl/sites/default/files/boa.pdf
 */
public class BraindeadCabbageOM extends OpponentModel {
	
	// Previous Code
	/*
	 * the learning coefficient is the weight that is added each turn to the
	 * issue weights which changed. It's a trade-off between concession speed
	 * and accuracy.
	 */
	/*
	 * value which is added to a value if it is found. Determines how fast the
	 * value weights converge.
	 */
	private int amountOfIssues;

	@Override
	public void init(NegotiationSession negotiationSession,
			Map<String, Double> parameters) {
		this.negotiationSession = negotiationSession;
		
		opponentUtilitySpace = (AdditiveUtilitySpace) negotiationSession
				.getUtilitySpace().copy();
		amountOfIssues = opponentUtilitySpace.getDomain().getIssues().size();
		/*
		 * This is the value to be added to weights of unchanged issues before
		 * normalization. Also the value that is taken as the minimum possible
		 * weight, (therefore defining the maximum possible also).
		 */
		
		initializeModel();

	}
	
	private double getEpsilonNormalized() {
		return 0.3 - 0.2 * Math.pow(negotiationSession.getTime(), 2) / amountOfIssues;
	}
	
	private int getLearnValueAddition() {
		return (int) ((1 - negotiationSession.getTime()) * 3 + 1);
	}

	@Override
	public void updateModel(Bid opponentBid, double time) {
		if (negotiationSession.getOpponentBidHistory().size() < 2) {
			return;
		}
		BidDetails oppBid = negotiationSession.getOpponentBidHistory()
				.getHistory()
				.get(negotiationSession.getOpponentBidHistory().size() - 1);
		BidDetails prevOppBid = negotiationSession.getOpponentBidHistory()
				.getHistory()
				.get(negotiationSession.getOpponentBidHistory().size() - 2);
		HashMap<Integer, Integer> lastDiffSet = determineDifference(prevOppBid,
				oppBid);

		List<Double> weights = new ArrayList<>();
		double total = 0;
		// re-weighing issues while making sure that the sum remains 1
		for (Integer i : lastDiffSet.keySet()) {
			double weight = opponentUtilitySpace.getWeight(i);

			if (lastDiffSet.get(i) == 0) {
				weight = (weight + getEpsilonNormalized());
			}
			weights.add(weight);
			
			total += weight;
		}
		
		Integer index = 0;
		for (Integer i : lastDiffSet.keySet()) {
			
			double weight = weights.get(index) / total;
			Objective issue = opponentUtilitySpace.getDomain()
					.getObjectivesRoot().getObjective(i);
			opponentUtilitySpace.setWeight(issue, weight);
			index++;
		}
				
		if (!negotiationSession
				.getOpponentBidHistory()
				.getHistory()
				.subList(0, negotiationSession
						.getOpponentBidHistory()
						.getHistory()
						.size() - 2)
				.stream()
				.anyMatch(bid -> bid
						.getBid()
						.equals(negotiationSession
								.getOpponentBidHistory()
								.getLastBidDetails()
								.getBid()))) {
			try {
				for (Entry<Objective, Evaluator> e : opponentUtilitySpace
						.getEvaluators()) {
					EvaluatorDiscrete value = (EvaluatorDiscrete) e.getValue();
					IssueDiscrete issue = ((IssueDiscrete) e.getKey());
					
					/*
					 * add constant learnValueAddition to the current preference of
					 * the value to make it more important
					 */
					ValueDiscrete issuevalue = (ValueDiscrete) oppBid.getBid()
							.getValue(issue.getNumber());
					Integer eval = value.getEvaluationNotNormalized(issuevalue);
					value.setEvaluation(issuevalue, (getLearnValueAddition() + eval));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public double getBidEvaluation(Bid bid) {
		double result = 0;
		try {
			result = opponentUtilitySpace.getUtility(bid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getName() {
		return "BraindeadCabbageOM";
	}

	@Override
	public Set<BOAparameter> getParameterSpec() {
		Set<BOAparameter> set = new HashSet<BOAparameter>();
		set.add(new BOAparameter("l", 0.2,
				"The learning coefficient determines how quickly the issue weights are learned"));
		return set;
	}

	/**
	 * Init to flat weight and flat evaluation distribution
	 */
	private void initializeModel() {
		double commonWeight = 1D / amountOfIssues;

		for (Entry<Objective, Evaluator> e : opponentUtilitySpace
				.getEvaluators()) {

			opponentUtilitySpace.unlock(e.getKey());
			e.getValue().setWeight(commonWeight);
			try {
				// set all value weights to one (they are normalized when
				// calculating the utility)
				for (ValueDiscrete vd : ((IssueDiscrete) e.getKey())
						.getValues())
					((EvaluatorDiscrete) e.getValue()).setEvaluation(vd, 1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Determines the difference between bids. For each issue, it is determined
	 * if the value changed. If this is the case, a 1 is stored in a hashmap for
	 * that issue, else a 0.
	 * 
	 * @param a
	 *            bid of the opponent
	 * @param another
	 *            bid
	 * @return
	 */
	private HashMap<Integer, Integer> determineDifference(BidDetails first,
			BidDetails second) {

		HashMap<Integer, Integer> diff = new HashMap<Integer, Integer>();
		try {
			for (Issue i : opponentUtilitySpace.getDomain().getIssues()) {
				Value value1 = first.getBid().getValue(i.getNumber());
				Value value2 = second.getBid().getValue(i.getNumber());
				diff.put(i.getNumber(), (value1.equals(value2)) ? 0 : 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return diff;
	}

}