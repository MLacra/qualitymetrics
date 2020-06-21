package quality;

import mapping_quality.Quality;
import utils.Constants;

public class GenerateMetrics {

	public static void main(String[] args) {

		Quality.compute_results(
				"resources/unit_test/input.csv", 
				"resources/unit_test/output.csv");	
	}
	
	
}
