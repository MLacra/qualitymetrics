package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import mapping_quality.QualityResults;

public class WriteUtils {

	static final String DEFAULT_FOLDER = "resources/top_down_scale_scenarios";
	static final String PROFILE_FILE_NAME = "profiling_input.xml";
	static final String DISTRACTION_FILE_NAME = "distractions.in";
	static final String DATASOURCE_FILE_NAME = "datasource.vada";
	static final String MATCH_FILE_NAME = "match.vada";
	static final String EXPECTED_SQL_MAPPING = "expected_mapping.sql";
	static final String SOURCE_TARGET_PAIRS_FILE_NAME = "sourceTargetPairs.vada";
	static final String DATABASE_PREFIX = "jdbc:postgresql://";
	
	static final String DATABASE_NAME = "tests_generator_chain_join";
	
	static final String DATABASE_URL = "localhost:5432/"+DATABASE_NAME;
	static final String DATABASE_URL_SPICY = "localhost:5432/"+DATABASE_NAME+"_spicy_target";
	static final String DATABASE_NAME_SPICY = DATABASE_NAME+"_spicy_target";
	static final String DATABASE_DRIVER = "JDBC_POSTGRES";
	static final String SPICY_MAPTASK = "spicy_maptask.xml";
	static String SPICY_MAPPING = "spicy_mapping";
	
	static ArrayList<String> prefixes = new ArrayList<>();

	public static void write_headers(String output_file_path, String toWrite, boolean ifAppend) {
		
		if (output_file_path==null)
			return;
		
		BufferedWriter bw = null;
		FileWriter fw = null;
		

		try {

			fw = new FileWriter(output_file_path,ifAppend);
			bw = new BufferedWriter(fw);
			bw.write(toWrite+"\n");
			bw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
    public static void write_plot_headers(String output_file_path, String toWrite, boolean ifAppend) {
		
		if (output_file_path==null)
			return;
		
		String plot_file = output_file_path+"_plot.csv";
		
		BufferedWriter bw_plot = null;
		FileWriter fw_plot = null;

		try {
			fw_plot = new FileWriter(plot_file,ifAppend);
			bw_plot = new BufferedWriter(fw_plot);
			bw_plot.write(toWrite+"\n");
			bw_plot.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (bw_plot != null)
					bw_plot.close();

				if (fw_plot != null)
					fw_plot.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void write_statistics(String output_file_path, QualityResults results, boolean ifAppend) {

		if (results==null||output_file_path==null)
			return;
		
		String plot_file = output_file_path+"_plot.csv";
		
		BufferedWriter bw_plot = null;
		FileWriter fw_plot = null;
		
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(output_file_path,ifAppend);
			bw = new BufferedWriter(fw);
			bw.write(results.toCSV()+"\n");
			bw.flush();
			
			fw_plot = new FileWriter(plot_file,ifAppend);
			bw_plot = new BufferedWriter(fw_plot);
			bw_plot.write(results.to_plot_CSV()+"\n");
			bw_plot.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();
				
				if (bw_plot != null)
					bw_plot.close();

				if (fw_plot != null)
					fw_plot.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
}
