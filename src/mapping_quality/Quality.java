package mapping_quality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import utils.GenericTriple;
import utils.RelationalDatabaseUtils;
import utils.WriteUtils;

public class Quality {
	private static final Logger logger = Logger.getLogger(Quality.class.getName());

	/**
	 * This will return a triple which will contain:
	 * PRECISION, RECALL, F-MEASURE	
	 * @param input_relation
	 * @param ground_truth_relation
	 */
	public static GenericTriple<Double, Double, Double> compute_tuple_results(OutputView input_relation, OutputView ground_truth_relation) {
		
		if (input_relation==null||ground_truth_relation==null)
			return null;
		
		input_relation.setGround_truth(ground_truth_relation);
		
		//TP/(TP+FP)
		double precision = input_relation.getTuplePrecision();
		
		//TP/(TP+FN)
		double recall = input_relation.getTupleRecall();
		
		// 2/[(1/precision)+(1/recall)]
		double fmeasure = input_relation.getTupleFmeasure();
		
		GenericTriple<Double, Double, Double> result = new GenericTriple<Double, Double, Double>(precision, recall, fmeasure);
		
		return result;
	}
	
	public static GenericTriple<Double, Double, Double> compute_cell_results(OutputView input_relation, OutputView ground_truth_relation) {
		
		if (input_relation==null||ground_truth_relation==null)
			return null;
		
		input_relation.setGround_truth(ground_truth_relation);
		
		//TP/(TP+FP)
		double precision = input_relation.getCellPrecision();
		
		//TP/(TP+FN)
		double recall = input_relation.getCellRecall();
		
		// 2/[(1/precision)+(1/recall)]
		double fmeasure = input_relation.getCellFmeasure();
		
		GenericTriple<Double, Double, Double> result = new GenericTriple<Double, Double, Double>(precision, recall, fmeasure);
		
		return result;
	}
	
	public static Double compute_precision(OutputView input_relation, OutputView ground_truth_relation) {
		
		if (input_relation!=null&&ground_truth_relation!=null)
			return null;
		
		input_relation.setGround_truth(ground_truth_relation);
		
		//TP/(TP+FP)
		double precision = input_relation.getTuplePrecision();
		
		return precision;
		
	}
	
	public static Double compute_recall(OutputView input_relation, OutputView ground_truth_relation) {
		
		if (input_relation!=null&&ground_truth_relation!=null)
			return null;
		
		input_relation.setGround_truth(ground_truth_relation);
		
		//TP/(TP+FN)
		double recall = input_relation.getTupleRecall();
		
		return recall;
		
	}
	
	public static Double compute_Fmeasure(OutputView input_relation, OutputView ground_truth_relation) {
		
		if (input_relation!=null&&ground_truth_relation!=null)
			return null;
		
		input_relation.setGround_truth(ground_truth_relation);
		
		double fmeasure = input_relation.getTupleFmeasure();
		
		return fmeasure;
		
	}

	/**
	 * <pre/>
	 * Input file lines have the following format:
	 * databaseName, ground_truth_file, mapping_file, primary_key_name
	 * 
	 * ground_truth_file is expected to have a SQL mapping
	 * 
	 * mapping_file is expected to have a SQL mapping
	 * 
	 * write to output_file:
	 * mapping_file_name, PRECISION, RECALL, FMEASURE
	 * 
	 * @param input_file
	 * @param output_file
	 */
	public static void compute_results(String input_file, String output_file)
	{
		if (input_file==null)
			return;
		if (output_file==null)
			output_file = input_file+".output";
		
		File input = new File(input_file);
		
		if (!input.exists()) {
			logger.severe("Input file not found: "+input_file);
			return;
		}
		 WriteUtils.write_headers(output_file, QualityResults.CSV_headers(","), false);
		 WriteUtils.write_plot_headers(output_file, QualityResults.plot_CSV_headers(), false);
		 
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       String[] compare_mappings = line.split(",");
		      
		       
		       /* 1 - database, 
		        * 2 - ground_truth_file, 
		        * 3 - mapping_file, 
		        * 4 - primary key attribute name */
		       if (compare_mappings.length==4)
		       {
		    	   String databaseName = compare_mappings[0];
		    	   String pk_name = compare_mappings[3];
		    	   
		    	   //1 - ground truth file
		    	   String gt_query= readWholeFile(compare_mappings[1]);
		    	   ArrayList<String> attributesNames = RelationalDatabaseUtils.readAttributesNames(databaseName, gt_query);
		    	   OutputView ground_truth = RelationalDatabaseUtils.readRelation(databaseName, gt_query, pk_name, attributesNames);
		    	  
		    	   //2 - mapping file
		    	   String mapping_query= readWholeFile(compare_mappings[2]);
		    	   OutputView mappingView = new OutputView(RelationalDatabaseUtils.readRelation(databaseName, mapping_query, pk_name, attributesNames));
		    	   
		    	   //compare the ground truth to the output of the mapping
				   GenericTriple tuple_results = compute_tuple_results(mappingView, ground_truth);
				   GenericTriple cell_results = compute_cell_results(mappingView,ground_truth);
				   QualityResults results = new QualityResults(compare_mappings[2], tuple_results, cell_results);
				   
				   results.setCl_true_positives(mappingView.getCell_true_positives());
				   results.setCl_true_negatives(mappingView.getCell_true_negatives());
				   results.setCl_false_positives(mappingView.getCell_false_positives());
				   results.setCl_false_negatives(mappingView.getCell_false_negatives());
				   
				   results.setTl_true_positives(mappingView.getTuple_true_positives());
				   results.setTl_false_positives(mappingView.getTuple_false_positives());
				   results.setTl_false_negatives(mappingView.getTuple_false_negatives());
				   
				   results.setIncomplete_tl_tp(mappingView.getIncomplete_tp());
				   results.setIncomplete_tl_fp(mappingView.getIncomplete_fp());
				   results.setIncomplete_tl_fn(mappingView.getIncomplete_fn());
				   
				   results.setMapping_size(mappingView.getCardinality());
				   
		    	   WriteUtils.write_statistics(output_file, results , true);
		       }
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This works only on small files.
	 * @return
	 * @throws IOException 
	 */
	private static String readWholeFile(String filePath) throws IOException {

		String content = "";

		File mapping_file = new File(filePath);
		if (!mapping_file.exists()) {
			logger.severe("File doesn't exist: " + filePath);
			return content;
		}
		// read the whole content
		FileInputStream fis = new FileInputStream(mapping_file);
		byte[] data = new byte[(int) mapping_file.length()];
		fis.read(data);
		fis.close();

		content = new String(data, "UTF-8");

		return content;
	}
}
