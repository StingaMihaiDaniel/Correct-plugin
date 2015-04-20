package sample.plugin.hello_maven_plugin;

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
 * Says "Hi" to the user.
 *
 */
@Mojo(name = "process-properties")
public class CorrectMojo extends AbstractMojo {
	public static String everything = null;
	private boolean newLine = true;
	private boolean right = false;
	private boolean comment = false;
	@Parameter
	private String[] inputFiles;
	@Parameter
	private String outputDirectory;
	@Parameter(defaultValue = "${basedir}")
	private String projectPath;

	public void process(InputStream in, String outputPath) throws IOException {
		if (in == null) {
			getLog().error("File not found!");
			return;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			File file = new File(outputPath);
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

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			for (int i = 0; i < inputFiles.length; i++){
				Path filePath = Paths.get(projectPath, inputFiles[i]);
				String outputFileName = FilenameUtils.getName(filePath.toString());
				Path outputFilePath = Paths.get(outputDirectory,outputFileName);
				FileInputStream in = new FileInputStream(filePath.toString());
				getLog().info(outputFileName);
				this.process(in,outputFilePath.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}