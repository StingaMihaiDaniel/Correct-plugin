package sample.plugin.correct_maven_plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Edits a .properties file
 */

@Mojo(name = "process-properties")
public class CorrectMojo extends AbstractMojo {
<<<<<<< HEAD
	//This flag is used to mark a new line
=======
>>>>>>> f35b9e14a5b7b5484274fae558afef3dbc56dd58
	private boolean newLine = true;

	private boolean right = false;
	private boolean comment = false;
<<<<<<< HEAD

=======
	
>>>>>>> f35b9e14a5b7b5484274fae558afef3dbc56dd58
	@Parameter
	private String[] inputFiles;
	@Parameter
	private String outputDirectory;
	@Parameter(defaultValue = "${basedir}")
	private String projectPath;

	private void process(InputStream in, Path outputFilePath) throws IOException {
		if (in == null) {
			getLog().error("File not found!");
			return;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			File file = outputFilePath.toFile();
			Writer writer = new FileWriter(file);
			
			int value;
			value = br.read();
			while (value != -1) {
				char ch = (char) value;
				switch (ch) {
				case '\n':
					right = false;
					comment = false;
					break;
				case ':':
					if (!comment)
						writer.append('\\');
					break;
				case '!':
				case '#':
					if (!newLine && !comment)
						writer.append('\\');

					if (newLine)
						comment = true;
					break;
				case '=':
					if (right)
						writer.append('\\');
					right = true;
					break;
				}
				writer.append(ch);
				newLine = ch == '\n';
				value = br.read();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			for (int i = 0; i < inputFiles.length; i++){
				Path filePath = Paths.get(projectPath, inputFiles[i]);
				String outputFileName = FilenameUtils.getName(filePath.toString());
				Path outputFilePath = Paths.get(outputDirectory,outputFileName);
				FileInputStream in = new FileInputStream(filePath.toString());
				getLog().debug("Processing: " + outputFileName);
				this.process(in, outputFilePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
