package mapping_quality;

import utils.GenericTriple;

public class QualityResults {

	String mapping_file_name;
	private GenericTriple row_level_results;
	private GenericTriple cell_level_results;
	
	long tl_true_positives = -1;
	long tl_false_negatives = -1;
	long tl_false_positives = -1;
	
	long cl_true_positives = -1;
	long cl_true_negatives = -1;
	long cl_false_negatives = -1;
	long cl_false_positives = -1;
	
	long incomplete_tl_tp = -1;
	long incomplete_tl_fp = -1;
	long incomplete_tl_fn = -1;
	
	long mapping_size;
	
	public QualityResults(String mapping_file, GenericTriple row_level_results, GenericTriple cell_level_results) {
		super();
		this.row_level_results = row_level_results;
		this.cell_level_results = cell_level_results;
		mapping_file_name = mapping_file;
	}
	
	public void setRow_level_results(GenericTriple row_level_results) {
		this.row_level_results = row_level_results;
	}
	
	public GenericTriple getRow_level_results() {
		return row_level_results;
	}
	
	public GenericTriple<Double, Double, Double> getCell_level_results() {
		return cell_level_results;
	}
	
	public void setCell_level_results(GenericTriple cell_level_results) {
		this.cell_level_results = cell_level_results;
	}

	public String getMapping_file_name() {
		return mapping_file_name;
	}

	public void setMapping_file_name(String mapping_file_name) {
		this.mapping_file_name = mapping_file_name;
	}

	
	public static String CSV_headers(String delimiter) {
		return "MAPPING FILENAME,"
				+ "Mapping cardinality,"
				+ "TL - Precision,TL - Recall,TL - F-measure,"
				+ "TL - true positives,TL - False positives, TL - False negatives,"
				+ "TL - incomplete TPs, TL - incomplete FPs, TL - incomplete FNs,"
				+ "CL - Precision,CL - Recall,CL - F-measure,"
				+ "CL - True positives, CL - True negatives,CL - False positives,CL - False negatives";
	}
	
	public String toCSV() {
		
		double percent_itp = (tl_true_positives>0)? ((double)incomplete_tl_tp)/tl_true_positives:0;
		double percent_ifp = (tl_false_positives>0)? ((double)incomplete_tl_fp)/tl_false_positives:0;
		double percent_ifn = (tl_false_negatives>0)? ((double)incomplete_tl_fn)/tl_false_negatives:0;
		
		 return "\""+mapping_file_name+"\","
				 +mapping_size+","
				 +row_level_results+","+
				 +tl_true_positives+","
				 +tl_false_positives+","
				 +tl_false_negatives+","
				 +incomplete_tl_tp+" ("+Math.round(percent_itp*100)+"%),"
				 +incomplete_tl_fp+" ("+Math.round(percent_ifp*100)+"%),"
				 +incomplete_tl_fn+" ("+Math.round(percent_ifn*100)+"%),"
				 +cell_level_results+","
				 +cl_true_positives+","
				 +cl_true_negatives+","
				 +cl_false_positives+","
				 +cl_false_negatives;
	}
	
	public static String plot_CSV_headers() {
		return ",, complete , incomplete";
	}
	
	public String to_plot_CSV() {
		return "\""+mapping_file_name.split("/")[mapping_file_name.split("/").length-1]+"\","
				+ "True positive tuples," +(tl_true_positives-incomplete_tl_tp)+","+incomplete_tl_tp +"\n"
				+ ",False positive tuples," +(tl_false_positives-incomplete_tl_fp)+","+incomplete_tl_fp+"\n"
				+ ",False negative tuples,"+ (tl_false_negatives-incomplete_tl_fn)+","+incomplete_tl_fn;
				
	}
	
	@Override
	public String toString() {
		return "QualityResults [row_level_results=" + row_level_results + ", cell_level_results=" + cell_level_results
				+ "]";
	}

	public long getTl_true_positives() {
		return tl_true_positives;
	}

	public void setTl_true_positives(long tl_true_positives) {
		this.tl_true_positives = tl_true_positives;
	}

	public long getTl_false_negatives() {
		return tl_false_negatives;
	}

	public void setTl_false_negatives(long tl_false_negatives) {
		this.tl_false_negatives = tl_false_negatives;
	}

	public long getTl_false_positives() {
		return tl_false_positives;
	}

	public void setTl_false_positives(long tl_false_positives) {
		this.tl_false_positives = tl_false_positives;
	}

	public long getCl_true_positives() {
		return cl_true_positives;
	}

	public void setCl_true_positives(long cl_true_positives) {
		this.cl_true_positives = cl_true_positives;
	}

	public long getCl_true_negatives() {
		return cl_true_negatives;
	}

	public void setCl_true_negatives(long cl_true_negatives) {
		this.cl_true_negatives = cl_true_negatives;
	}

	public long getCl_false_negatives() {
		return cl_false_negatives;
	}

	public void setCl_false_negatives(long cl_false_negatives) {
		this.cl_false_negatives = cl_false_negatives;
	}

	public long getCl_false_positives() {
		return cl_false_positives;
	}

	public void setCl_false_positives(long cl_false_positives) {
		this.cl_false_positives = cl_false_positives;
	}

	public long getIncomplete_tl_tp() {
		return incomplete_tl_tp;
	}

	public void setIncomplete_tl_tp(long incomplete_tl_tp) {
		this.incomplete_tl_tp = incomplete_tl_tp;
	}

	public long getIncomplete_tl_fp() {
		return incomplete_tl_fp;
	}

	public void setIncomplete_tl_fp(long incomplete_tl_fp) {
		this.incomplete_tl_fp = incomplete_tl_fp;
	}

	public long getIncomplete_tl_fn() {
		return incomplete_tl_fn;
	}

	public void setIncomplete_tl_fn(long incomplete_tl_fn) {
		this.incomplete_tl_fn = incomplete_tl_fn;
	}

	public long getMapping_size() {
		return mapping_size;
	}

	public void setMapping_size(long mapping_size) {
		this.mapping_size = mapping_size;
	}
	
	
	
}
