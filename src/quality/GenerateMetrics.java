package quality;

import mapping_quality.Quality;
import utils.Constants;

public class GenerateMetrics {

	public static void main(String[] args) {

		Quality.compute_results(
				"/Users/lara/Documents/workspace-sts-3.9.3/QualityMetrics/resources/unit_test/input.csv", 
				"/Users/lara/Documents/workspace-sts-3.9.3/QualityMetrics/resources/unit_test/output.csv");

		
	}
	
	
}
