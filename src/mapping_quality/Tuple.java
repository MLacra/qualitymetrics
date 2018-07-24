package mapping_quality;

import java.util.HashMap;
import java.util.Set;

import utils.Constants;

public class Tuple {

	//AttributeName, value
	HashMap<String, String> values;
	Tuple ground_truth_tuple;
	
	int true_positives = -1;
	int true_negatives = -1;
	int false_negatives = -1;
	int false_positives = -1;
	
	int nulls = 0;
	int compared_gt_result;
	
	public Tuple ()
	{
		values = new HashMap<>();
		ground_truth_tuple = null;
		nulls = 0;
	}

	public HashMap<String, String> getValues() {
		return values;
	}
	
	public String getValueByAttribute(String attributeName)
	{
		for (String attribute:values.keySet())
		{
			if (attribute.equals(attributeName))
				return values.get(attribute);
		}
		return null;
	}

	public void putValue(String attribute, String val)
	{
		if (values==null)
			values = new HashMap<>();
		values.put(attribute, val);
		
		if (val==null)
			nulls++;
	}
	
	public void setValues(HashMap<String, String> values) {
		this.values = values;
		
		for (String key:values.keySet()) {
			if (values.get(key)==null)
				nulls++;
		}
	}
	
	public int getNulls() {
		return nulls;
	}

	public void setNulls(int nulls) {
		this.nulls = nulls;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		Tuple other = (Tuple) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
	
	/**
	 * Counts how many values are equal and not NULL.
	 * 1 TP = 1 correct cell value
	 * @param gt_tuple
	 * @return
	 */
	public int compute_true_positive_cells(Tuple gt_tuple) {
		
		int true_positives = 0;
		
		if (gt_tuple==null||gt_tuple.getValues().isEmpty())
			return true_positives;
		
		if (ground_truth_tuple!=null&&gt_tuple.equals(ground_truth_tuple))
			return this.true_positives;

		Set<String> gt_attribute_names = gt_tuple.getValues().keySet();
		
		for (String attribute_name: gt_attribute_names) {
			
			String gt_value = gt_tuple.getValueByAttribute(attribute_name);
			String value = (values.containsKey(attribute_name)? values.get(attribute_name):null);
			
			if (gt_value!=null&&value!=null&&gt_value.equals(value))
				true_positives++;
		}
		
		return true_positives;
	}
	
	/**
	 * Counts how many values are equal to true NULLs (NULLs expected in the ground truth).
	 * 1 TN = 1 NULL value (1 expected NULL)
	 * @param gt_tuple
	 * @return
	 */
	public int compute_true_negative_cells(Tuple gt_tuple) {
		
		int true_negatives = 0;
		
		if (gt_tuple==null||gt_tuple.getValues().isEmpty())
			return true_negatives;
		
		if (ground_truth_tuple!=null&&gt_tuple.equals(ground_truth_tuple))
			return this.true_negatives;

		Set<String> gt_attribute_names = gt_tuple.getValues().keySet();
		for (String attribute_name: gt_attribute_names) {
			String gt_value= gt_tuple.getValueByAttribute(attribute_name);
			String value = (values.containsKey(attribute_name)? values.get(attribute_name):"Not contained");
			if (value==null&&gt_value==null)
				true_negatives++;
		}
		
		return true_negatives;
		
	}
	
	/**
	 * Counts how many values are not equal to the ground truth values and are also NULLs (NULLs not expected in the ground truth).
	 * 1 FN = 1 missing value (1 NULL, but shouldn't be NULL)
	 * @param gt_tuple
	 * @return
	 */
	public int compute_false_negative_cells(Tuple gt_tuple) {
		
		int false_negatives = 0;
		
		if (gt_tuple==null||gt_tuple.getValues().isEmpty())
			return false_negatives;
		
		if (ground_truth_tuple!=null&&gt_tuple.equals(ground_truth_tuple))
			return this.false_negatives;

		Set<String> gt_attribute_names = gt_tuple.getValues().keySet();
		for (String attribute_name: gt_attribute_names) {
			
			String gt_value = gt_tuple.getValueByAttribute(attribute_name);
			String value = (values.containsKey(attribute_name)? values.get(attribute_name):"Not contained");;
			
			// gt != NULL & value == NULL
			if (gt_value!=null&&value==null)
				false_negatives++;
		}
		
		return false_negatives;
		
	}
	
	/**
	 * Counts how many values are not equal to the ground truth values and are also NULLs (NULLs not expected in the ground truth).
	 * 1 FP = 1 incorrect value (!=NULL but not as in the ground truth)
	 * @param gt_tuple
	 * @return
	 */
	public int compute_false_positive_cells(Tuple gt_tuple) {
		int false_positives = 0;

		if (gt_tuple==null||gt_tuple.getValues().isEmpty())
			return false_positives;
		
		if (ground_truth_tuple!=null&&gt_tuple.equals(ground_truth_tuple))
			return this.false_positives;

		Set<String> gt_attribute_names = gt_tuple.getValues().keySet();
		for (String attribute_name: gt_attribute_names) {
			String gt_value = gt_tuple.getValueByAttribute(attribute_name);
			String value = (values.containsKey(attribute_name)? values.get(attribute_name):null);
			
			// incorrect value (diff from gt)
			if (gt_value!=null&&value!=null&&!gt_value.equals(value)) {
				false_positives++;
			}
			else
				if (gt_value==null&&value!=null)
					false_positives++;
		}
		
		return false_positives;
	}
	
	/**
	 * 1 TP = 1 correct tuple (TP+TN in cells = arity target)
	 * 1 FN = 1 tuple with missing values (FN in cells>1)
	 * 1 FP = 1 tuple with incorrect values, which are notNULLs (FP in cells>1)
	 * 
	 * @param gt_tuple
	 * @return
	 */
	public int compare_with_ground_truth(Tuple gt_tuple) {
		
		if (gt_tuple==null)
			return -1;
		
		this.setGround_truth_tuple(gt_tuple);
		
		int tuple_TPs = 0;
		tuple_TPs = true_positives+true_negatives;
//		tuple_TPs = true_positives;
		
//		if (nulls>=(values.size()/2))
//			 tuple_TPs = true_positives;
//		else
//			if (true_positives>=true_negatives)
//				tuple_TPs = true_positives+true_negatives;
//			else
//			    tuple_TPs = true_positives;
		
		if (tuple_TPs==values.size())
			return Constants.TRUE_POSITIVE;
			
		if (false_negatives==values.size())
			return Constants.FALSE_NEGATIVE;
		
		if (false_positives==values.size())
			return Constants.FALSE_POSITIVE;
		
		//return the dominant value
//		if (tuple_TPs>=false_negatives + false_positives)
//			compared_gt_result = Constants.TRUE_POSITIVE;
//		else
//			if (false_negatives >= tuple_TPs + false_positives)
//				compared_gt_result = Constants.FALSE_NEGATIVE;
//			else
//				if (false_positives >= tuple_TPs + false_negatives)
//					compared_gt_result =  Constants.FALSE_POSITIVE;
		
		int max = Math.max(tuple_TPs, Math.max(false_negatives, false_positives));
		
		if (max == tuple_TPs)
			compared_gt_result = Constants.TRUE_POSITIVE;

		if (max == false_positives)
			compared_gt_result =  Constants.FALSE_POSITIVE;

		if (max == false_negatives)
			compared_gt_result = Constants.FALSE_NEGATIVE;
		
		return compared_gt_result;
	}

	public Tuple getGround_truth_tuple() {
		return ground_truth_tuple;
	}

	public void setGround_truth_tuple(Tuple ground_truth_tuple) {
		
		if (ground_truth_tuple==null) {
			//reset
			true_positives = -1;
			true_negatives = -1;
			false_negatives = -1;
			false_positives = -1;
			return;
		}
		
		if (this.ground_truth_tuple==null) {
			
			true_positives = compute_true_positive_cells(ground_truth_tuple);
			true_negatives = compute_true_negative_cells(ground_truth_tuple);
			false_negatives = compute_false_negative_cells(ground_truth_tuple);
			false_positives = compute_false_positive_cells(ground_truth_tuple);
			this.ground_truth_tuple = ground_truth_tuple;
		}else
			if (!this.ground_truth_tuple.equals(ground_truth_tuple)) {
				
				true_positives = compute_true_positive_cells(ground_truth_tuple);
				true_negatives = compute_true_negative_cells(ground_truth_tuple);
				false_negatives = compute_false_negative_cells(ground_truth_tuple);
				false_positives = compute_false_positive_cells(ground_truth_tuple);
				this.ground_truth_tuple = ground_truth_tuple;
			}
		
	}

	
	public int getTrue_positives() {
		return true_positives;
	}

	public void setTrue_positives(int true_positives) {
		this.true_positives = true_positives;
	}

	public int getTrue_negatives() {
		return true_negatives;
	}

	public void setTrue_negatives(int true_negatives) {
		this.true_negatives = true_negatives;
	}

	public int getFalse_negatives() {
		return false_negatives;
	}

	public void setFalse_negatives(int false_negatives) {
		this.false_negatives = false_negatives;
	}

	public int getFalse_positives() {
		return false_positives;
	}

	public void setFalse_positives(int false_positives) {
		this.false_positives = false_positives;
	}

	public int getCompared_gt_result() {
		return compared_gt_result;
	}

	public void setCompared_gt_result(int compared_gt_result) {
		this.compared_gt_result = compared_gt_result;
	}

	
	@Override
	public String toString() {
		return "Tuple [values=" + values + ",\n"+
				"ground_truth_tuple=" + ground_truth_tuple.values+
				"\ntrue_positives="
				+ true_positives + ", true_negatives=" + true_negatives + ", false_negatives=" + false_negatives
				+ ", false_positives=" + false_positives + "]";
	}

	
	
}
