/*
 * Created on Oct 30, 2009
 *
 * Copyright Stephen J. Granite, CardioVascular Research Grid (http://www.cvrgrid.org)
 *
 * The software below is licensed under Apache License, v 2.0.
 * (http://www.apache.org/licenses/LICENSE-2.0)
 *
 * @author Stephen J. Granite
 */
package edu.jhu.cvrg.zipconverter;


import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
 * This is the main class to generate GE MUSE compatible output files, 
 * along with other files.  The tool itself looks for 3 parameters:
 * 
 * @param zipFileName - chosen file name for the zip that will be generated
 * @param startPattern - text string pattern to match at the beginning of your file names
 * @param endPattern - text string pattern to match at the end of your file names
 * 
 * Once the code has those parameters, it will search for all the files in the current
 * directory that match the start and end patterns specified.  As each of the matching files is
 * parsed, the tool generates a directory corresponding to the matching file.  The tool then divides
 * the contents of the input file into 3 output files: an XML metadata file for subject information
 * and previously calculated values, a 10 second rhythm strip file that conforms to the GE MUSE output
 * format produced by the MESA ECG Reading Center at Wake Forest University and a median file that
 * conforms to the GE MUSE output format produced by the MESA ECG Reading Center at Wake Forest University.
 * After generating directories and output files for each of the matching input files, the tool creates a 
 * manifest for the zip archive that it will generate.  The tool then generates a zip archive containing 
 * the manifest and all the folders it created.  Once the zip file has been created, the tool cleans up
 * after itself, removing all the folders and files it generated for the corresponding input files.
 * 
 * The tool requires the standard Java IO and Utility libraries to function properly.
 */

public class ZipConverter {
	static final int BUFFER = 2048;
	private File fFile;
	private int iCount;
	private static StringBuffer sb = new StringBuffer();
	private String medianFileName, zipManifestFileName, zipFileName;
	private ArrayList<String> files = new ArrayList<String>();
	boolean extraLine = false;
	private String extOptions;

	/** Returns the list of files for compression. 
	 * @return - an array of fileNames to pass to the zipFile.
	 */
	public Object[] getFiles() {
		return this.files.toArray();
	}

	/** Procedure to set the files to be added to the zip archive.
	 * @param fileName - file name for the file to be placed in the zip
	 */
	public void setFiles(String fileName) {
		this.files.add(fileName);
	}

	/** Returns the zipFileName variable. 
	 * @return - file name for the zip file generated.
	 */
	public String getZipFileName() {
		return zipFileName;
	}

	/** Procedure to set the zipFileName variable.
	 * @param zipFileName - file name for the zip file generated
	 */
	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}

	/** Returns the zipManifestFileName variable.
	 * @return - file name for the zip manifest file generated.
	 */
	public String getZipManifestFileName() {
		return zipManifestFileName;
	}

	/** Procedure to set the zipManifestFileName variable.
	 * @param zipManifestFileName - file name for the manifest file generated
	 */
	public void setZipManifestFileName(String zipManifestFileName) {
		zipManifestFileName = zipManifestFileName.substring(0,zipManifestFileName.lastIndexOf("."));
		zipManifestFileName += ".csv";
		this.zipManifestFileName = zipManifestFileName;
	}

	
	/** main for GEMUSESplitter class
	 * java -jar GEMUSESplitter.jar <filename of your zip file> <string at the start of your file names> <file extension of your input files>
	 * Example: java -jar GEMUSESplitter.jar "MESA_ECGs" "JHU" "txt"
	 * @param args - parameters entered in the command line conforming to the example provided
	 */
	public static void main(String[] args) throws FileNotFoundException {
		//if (args.length > 2) {
			//zipConverter parser = new zipConverter(args[0],args[1]);
			//zipConverter parser = new zipConverter("C:\\Files\\", "C:\\Files\\zipFile.zip");
			//parser.cleanUp(args[1]);
		String test = "test";
		System.out.println(test.contains("est"));	
		auditlogger("Done.");
		//} else {
			auditlogger("java -jar GEMUSESplitter.jar <filename of your zip file> <string at the start of your file names> <file extension of your input files>");
			auditlogger("Example: java -jar GEMUSESplitter.jar \"MESA_ECGs\" \"JHU\" \"txt\"");
		//}
	}



	/** Constructor for the zipConverter class
	 * Will create a zip file containing all the files in the specified folder which have the specified extensions.
	 * @param directory - directory in which files to be zips will be found.
	 * @param zipFileName - name to give the new zip file, can include path, name and extension.
	 * @param useManifest - boolean true to generate a manifest file for the zip file.
	 * @param extensions - pipe ("|") separated list of valid file extensions, without periods.
	 */
	public ZipConverter(String directory, String zipFileName, boolean useManifest, String extensions) throws Exception {
		this.extOptions = extensions;
		
		zipConverterCommon(directory, zipFileName, useManifest);
	}
	
	/** Constructor for the zipConverter class
	 * Will create a zip file containing all the files in the specified folder which have the correct extensions.
	 * @param directory - directory in which files to be zips will be found.
	 * @param zipFileName - name to give the new zip file, can include path, name and extension.
	 * @param useManifest - boolean true to generate a manifest file for the zip file.
	 */
	public ZipConverter(String directory, String zipFileName, boolean useManifest) throws Exception {
		extOptions = "dat|ini|rdt|hea";
		
		zipConverterCommon(directory, zipFileName, useManifest);
	}

	
	/** Common code for both Constructors for the zipConverter class
	 * Will create a zip file containing all the files in the specified folder which have the specified extensions.
	 * @param directory - directory in which files to be zips will be found.
	 * @param zipFileName - name to give the new zip file, can include path, name and extension.
	 * @param useManifest - boolean true to generate a manifest file for the zip file.
	 * @param extensions - pipe ("|") separated list of valid file extensions, without periods.
	 */
	private void zipConverterCommon(String directory, String zipFileName, boolean useManifest) throws Exception {
		try {
			File dir = new File(directory);
			String[] zipFiles = dir.list();
			for (int i=0; i < zipFiles.length; i++) {
					if (zipFiles[i].substring(zipFiles[i].length() - 3, zipFiles[i].length()).toLowerCase().matches(extOptions)) {
						auditlogger("Running: "+ (String)zipFiles[i]);
						setFiles(directory + File.separator + zipFiles[i]);
					}		
			}
			setZipFileName(zipFileName);
			if (useManifest) {
				setZipManifestFileName(zipFileName);
				generateZipManifest(getFiles(), getZipManifestFileName());
				setFiles(getZipManifestFileName());
			}
			addFileToZip(getFiles(), getZipFileName());
			
		}
		catch (Exception e) {
			throw e;
			
		}
		
	}

	/** Procedure to output information extracted from the input file
	 * @param aText - String variable containing the output
	 * @param outputFileName - file name for the output file generated 
	 */
	protected void writeFile(String aText, String outputFileName){
		try {
			// Create file
			FileWriter fstream = new FileWriter(new File(outputFileName));
			BufferedWriter out1 = new BufferedWriter(fstream);
			out1.write(aText);
			out1.close();
			fstream.close();
		} catch (Exception ex){
			System.err.println("Error: " + ex.getMessage());
		}    
	}

	/** Procedure to generate a manifest file for the zip file
	 * @param objects - list of files to be zipped
	 * @param zipManifestFileName - file name for the manifest that will be generated
	 */
	protected void generateZipManifest(Object[] objects, String zipManifestFileName){

		File temp = null;
		String subjectId = null;
		try {
			for (int i=0; i < objects.length; i++) {
				//temp = new File((String) zipFileEntryNames[i]);
				String fileName = (String) objects[i];
				//String fileName = (String) zipFileEntryNames[i].lastIndexOf(File.separator);
				String nameWithoutPath = fileName.substring(fileName.lastIndexOf(File.separator) != -1 ? fileName.lastIndexOf(File.separator)+ 1 : 0,fileName.length());
				String nameWithoutExtension = nameWithoutPath.substring(0,nameWithoutPath.lastIndexOf("."));
				//String baseFileName = fFile.getName().substring(0, fFile.getName().lastIndexOf(File.separator));
				sb.append(nameWithoutExtension + "," + nameWithoutPath + "\n");
			}
			writeFile(sb.toString(),zipManifestFileName);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	/** Procedure to zip up all the output files
	 * @param objects - list of files to be zipped
	 * @param zipFileName - file name for the zip that will be generated
	 */
	protected void addFileToZip(Object[] objects, String zipFileName){

		try {
			FileOutputStream dest = new FileOutputStream(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[BUFFER];
			for (int i=0; i < objects.length; i++) {
				String filename = (String) objects[i];
				FileInputStream fi = new FileInputStream((String) filename);
				ZipEntry entry = new ZipEntry((filename.substring(filename.lastIndexOf(File.separator)+1)));
				out.putNextEntry(entry);
				int count;
				while((count = fi.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				fi.close();
			}
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	/** Procedure to echo values to the System out
	 * @param aObject - Object to be converted to a String for printing
	 */
	private static void auditlogger(Object aObject){
		System.out.println(String.valueOf(aObject));
	}



}