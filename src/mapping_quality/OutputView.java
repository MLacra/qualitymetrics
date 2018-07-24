package mapping_quality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import utils.Constants;

public class OutputView {

	String databaseName;
	String sqlQuery;
	
	//The first value is the PK value - or the name of the attribute close to a PK
	HashMap<String, ArrayList<Tuple>> tuples;
	
	OutputView ground_truth;
	
	Double tuplePrecision = -1d;
	Double tupleRecall = -1d;
	
	Double cellPrecision = -1d;
	Double cellRecall = -1d;
	
	Long tuple_true_positives;
	Long tuple_false_negatives;
	Long tuple_false_positives;
	
	Long cell_true_positives;
	Long cell_false_negatives;
	Long cell_false_positives;
	Long cell_true_negatives;
	
	long incomplete_tp;
	long incomplete_fn;
	long incomplete_fp;
	
	long cardinality;
	
	
	public OutputView (){
		tuples = new HashMap<>();
	}
	
	public OutputView(OutputView other) {
		super();
		if (other==null)
			return;
		this.databaseName = other.databaseName;
		this.sqlQuery = other.sqlQuery;
		this.tuples = other.tuples;
		this.cardinality = other.cardinality;
//		this.cardinality = (this.tuples.containsKey(null) ? other.cardinality-this.tuples.get(null).size():other.cardinality);
//		this.tuples.remove(null);
	}

	
	/*-----------Tuple Level -------------*/
	public Double getTuplePrecision() {
		
		if (tuplePrecision==-1)
			tuplePrecision = computeTuplePrecision();
		
		return tuplePrecision;
	}

	public Double getTupleRecall() {
		
		if (tupleRecall==-1)
			tupleRecall=computeTupleRecall();
		
		return tupleRecall;
	}

	public Double getTupleFmeasure() {
		
		if(tuplePrecision==-1)
			tuplePrecision = computeTuplePrecision();
		
		if(tupleRecall==-1)
			tupleRecall=computeTupleRecall();
		
		return (2/((1/tuplePrecision)+(1/tupleRecall)));
	}

	/*-----------Cell Level -------------*/
	public Double getCellPrecision() {
		
		if (cellPrecision==-1)
			cellPrecision = computeCellPrecision();
		
		return cellPrecision;
	}

	public Double getCellRecall() {
		
		if (cellRecall==-1)
			cellRecall=computeCellRecall();
		
		return cellRecall;
	}

	public Double getCellFmeasure() {
		
		if(cellPrecision==-1)
			cellPrecision = computeCellPrecision();
		
		if(cellRecall==-1)
			cellRecall=computeCellRecall();
		
		return (2/((1/cellPrecision)+(1/cellRecall)));
	}

	
	
	private Double computeCellPrecision() {
		
		//already computed
		if (this.cellPrecision!=-1)
				return cellPrecision;
		
		if (ground_truth==null)
			return this.cellPrecision;
		
		cellPrecision =  ((double)cell_true_positives/(cell_true_positives+cell_false_positives));
		
		return cellPrecision;
	}
	
	private Double computeTuplePrecision() {
		
		//already computed
		if (this.tuplePrecision!=-1)
				return tuplePrecision;
		
		if (ground_truth==null)
			return this.tuplePrecision;
		
		tuplePrecision =  ((double)tuple_true_positives/(tuple_true_positives+tuple_false_positives));
		
		return tuplePrecision;
	}
	
	private Double computeCellRecall() {
		
		//already computed
		if (this.cellRecall!=-1)
			return cellRecall;
		
		if (ground_truth==null)
			return cellRecall;
		
		cellRecall = (double)cell_true_positives/(cell_true_positives+cell_false_negatives);
		
		return cellRecall;
	}
	
	private Double computeTupleRecall() {
		
		//already computed
		if (this.tupleRecall!=-1)
			return tupleRecall;
		
		if (ground_truth==null)
			return tupleRecall;
		
		tupleRecall = (double)tuple_true_positives/(tuple_true_positives+tuple_false_negatives);
		
		return tupleRecall;
	}
	
	private void compute_tuple_positives_negatives() {

		if (ground_truth == null || ground_truth.getTuples() == null)
			return;

		tuple_true_positives = 0l;
		tuple_false_negatives = 0l;
		tuple_false_positives = 0l;

		HashMap<String, ArrayList<Tuple>> gt_tuples = ground_truth.getTuples();
		for (String pkey : gt_tuples.keySet()) {
			if (pkey==null)
				continue;
			
			if (tuples.containsKey(pkey)) {
				
				ArrayList<Tuple> same_key_gt_tuples = gt_tuples.get(pkey);
				ArrayList<Tuple> same_key_tuples = tuples.get(pkey);
				
				//if in the gt are more tuples than in the output mapping
				if (same_key_gt_tuples.size()>same_key_tuples.size()) {
					tuple_false_negatives+=same_key_gt_tuples.size()-same_key_tuples.size();
					System.out.println("T_FNs = "+tuple_false_negatives+" added "+(same_key_gt_tuples.size()-same_key_tuples.size()));
				}
				

				
				for (Tuple tuple : same_key_tuples) {

					Tuple gt_tuple = choose_closest_gt_tuple(same_key_gt_tuples, tuple);
					int result = tuple.compare_with_ground_truth(gt_tuple);

					switch (result) {
					case Constants.TRUE_POSITIVE:
						tuple_true_positives++;
						break;
					case Constants.FALSE_NEGATIVE:
						tuple_false_negatives++;
						break;
					case Constants.FALSE_POSITIVE:
						tuple_false_positives++;
						break;
					default:
						System.err.println("Tuple was not be labelled.");
						break;
					}
				}
			} else
				// pkey not in mapping tuples
				tuple_false_negatives++;
		}

		for (String pkey : tuples.keySet())
			if (!gt_tuples.containsKey(pkey)&& pkey!=null) {
				tuple_false_positives+= tuples.get(pkey).size();
			}
		
		
		//if the keys are null, then we cannot say that those tuples are the same so we label them as FPs or FNs
		if (tuples.containsKey(null))
			tuple_false_positives+= tuples.get(null).size();
		
		if (gt_tuples.containsKey(null))
			tuple_false_negatives+= gt_tuples.get(null).size();
		
	}
	
	private void compute_incomplete_tuples() {
		
		if (tuples==null)
			return;
		
		incomplete_tp = 0;
		incomplete_fn = 0;
		incomplete_fp = 0;
		
		Collection<ArrayList<Tuple>> all_tuples = tuples.values();
		
		for (ArrayList<Tuple> subset: all_tuples) {
			for (Tuple tuple:subset) {
				if (tuple.getCompared_gt_result()==Constants.TRUE_POSITIVE&&(tuple.getFalse_positives()>0||tuple.getFalse_negatives()>0))
				{
					System.out.println(tuple.toString());
					incomplete_tp++;	
				}
				else
					if (tuple.getCompared_gt_result()==Constants.FALSE_POSITIVE&&(tuple.getTrue_positives()>0||tuple.getTrue_negatives()>0))
						incomplete_fp++; 
					else
						if (tuple.getCompared_gt_result()==Constants.FALSE_NEGATIVE&&(tuple.getTrue_positives()>0||tuple.getTrue_negatives()>0))
							incomplete_fn++;
					
			}
		}
		
	}
	
	private void compute_cell_positives_negatives() {

		if (ground_truth == null || ground_truth.getTuples() == null)
			return;
		
		cell_true_positives = 0l;
		cell_false_negatives = 0l;
		cell_false_positives = 0l;
		cell_true_negatives = 0l;

		HashMap<String, ArrayList<Tuple>> gt_tuples = ground_truth.getTuples();
		for (String pkey : gt_tuples.keySet()) {
			
			if (pkey==null)
				continue;
			
			ArrayList<Tuple> same_key_gt_tuples = gt_tuples.get(pkey);
			
			if (tuples.containsKey(pkey)) {
				ArrayList<Tuple> same_key_tuples = tuples.get(pkey);
				
				if (same_key_gt_tuples.size()>same_key_tuples.size())
					cell_false_negatives+=(same_key_gt_tuples.size()-same_key_tuples.size())*same_key_gt_tuples.get(0).getValues().size();
				
				for (Tuple tuple : same_key_tuples) {

					Tuple gt_tuple = choose_closest_gt_tuple(same_key_gt_tuples, tuple);
					tuple.compare_with_ground_truth(gt_tuple);
					
					cell_true_positives += tuple.getTrue_positives();
					cell_false_negatives += tuple.getFalse_negatives();
					cell_false_positives += tuple.getFalse_positives();
					cell_true_negatives += tuple.getTrue_negatives();
					
				}
			} else
				// pkey not in mapping tuples
				//(how many tuples are missing)*(the number of attributes)
				cell_false_negatives+=same_key_gt_tuples.size()*same_key_gt_tuples.get(0).getValues().size();
		}

		for (String pkey : tuples.keySet())
			if (!gt_tuples.containsKey(pkey)) {
				//(how many tuples are in the output and should be missing)*(the number of attributes)
				cell_false_positives+=tuples.get(pkey).size()*tuples.get(pkey).get(0).getValues().size();
			}
		
		// if the keys are null, then we cannot say that those tuples are the same so we
		// label them as FPs or FNs
		if (tuples.containsKey(null))
			cell_false_positives += tuples.get(null).size() * tuples.get(null).get(0).getValues().size();

		if (gt_tuples.containsKey(null))
			cell_false_negatives += gt_tuples.get(null).size() * gt_tuples.get(null).get(0).getValues().size();
	}
	
	private Tuple choose_closest_gt_tuple (ArrayList<Tuple> gt_tuples, Tuple tuple) {
		Tuple gt_closest = null;
		
		if (tuple==null||gt_tuples==null||gt_tuples.isEmpty())
			return gt_closest;
		
		long max_tp_tn = -1;
		for (Tuple gt_tuple:gt_tuples) {
			
			if (gt_tuple.equals(tuple)) {
				gt_closest = gt_tuple;
				break;
			}
			
			long tp_tn = tuple.compute_true_negative_cells(gt_tuple)+ tuple.compute_true_positive_cells(gt_tuple);
			if (max_tp_tn<tp_tn) {
				gt_closest = gt_tuple;
				max_tp_tn = tp_tn;
			}
			else
				if (max_tp_tn==tp_tn)
					if (gt_tuple.getNulls()<gt_closest.getNulls())
						gt_closest = gt_tuple;
		}
		
		return gt_closest;
	}

	
	
	public OutputView getGround_truth() {
		return ground_truth;
	}

	public void setGround_truth(OutputView ground_truth) {
		
		if (ground_truth==null)
			return;
		
		if (this.ground_truth!=null) {
			if (!ground_truth.equals(this.ground_truth))
				this.ground_truth = ground_truth;
			else
				return;
		}else
			this.ground_truth = ground_truth;
		
		compute_tuple_positives_negatives();
		compute_cell_positives_negatives();
		compute_incomplete_tuples();
		tuplePrecision = computeTuplePrecision();
		tupleRecall = computeTupleRecall();
		
		cellPrecision = computeCellPrecision();
		cellRecall = computeCellRecall();
	}
	
	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	public HashMap<String, ArrayList<Tuple>> getTuples() {
		return tuples;
	}

	public void setTuples(HashMap<String, ArrayList<Tuple>> tuples) {
		this.tuples = tuples;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((databaseName == null) ? 0 : databaseName.hashCode());
		result = prime * result + ((sqlQuery == null) ? 0 : sqlQuery.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OutputView other = (OutputView) obj;
		if (databaseName == null) {
			if (other.databaseName != null)
				return false;
		} else if (!databaseName.equals(other.databaseName))
			return false;
		if (sqlQuery == null) {
			if (other.sqlQuery != null)
				return false;
		} else if (!sqlQuery.equals(other.sqlQuery))
			return false;
		return true;
	}

	public Long getTuple_true_positives() {
		return tuple_true_positives;
	}

	public void setTuple_true_positives(Long tuple_true_positives) {
		this.tuple_true_positives = tuple_true_positives;
	}

	public Long getTuple_false_negatives() {
		return tuple_false_negatives;
	}

	public void setTuple_false_negatives(Long tuple_false_negatives) {
		this.tuple_false_negatives = tuple_false_negatives;
	}

	public Long getTuple_false_positives() {
		return tuple_false_positives;
	}

	public void setTuple_false_positives(Long tuple_false_positives) {
		this.tuple_false_positives = tuple_false_positives;
	}

	public Long getCell_true_positives() {
		return cell_true_positives;
	}

	public void setCell_true_positives(Long cell_true_positives) {
		this.cell_true_positives = cell_true_positives;
	}

	public Long getCell_false_negatives() {
		return cell_false_negatives;
	}

	public void setCell_false_negatives(Long cell_false_negatives) {
		this.cell_false_negatives = cell_false_negatives;
	}

	public Long getCell_false_positives() {
		return cell_false_positives;
	}

	public void setCell_false_positives(Long cell_false_positives) {
		this.cell_false_positives = cell_false_positives;
	}

	public Long getCell_true_negatives() {
		return cell_true_negatives;
	}

	public void setCell_true_negatives(Long cell_true_negatives) {
		this.cell_true_negatives = cell_true_negatives;
	}

	
	public long getIncomplete_tp() {
		return incomplete_tp;
	}

	public void setIncomplete_tp(long incomplete_tp) {
		this.incomplete_tp = incomplete_tp;
	}

	public long getIncomplete_fn() {
		return incomplete_fn;
	}

	public void setIncomplete_fn(long incomplete_fn) {
		this.incomplete_fn = incomplete_fn;
	}

	public long getIncomplete_fp() {
		return incomplete_fp;
	}

	public void setIncomplete_fp(long incomplete_fp) {
		this.incomplete_fp = incomplete_fp;
	}

	
	public long getCardinality() {
		return cardinality;
	}
	

	public void setCardinality(long cardinality) {
		this.cardinality = cardinality;
	}
}
