package fiji.plugin.mamut;

import fiji.plugin.trackmate.Logger;
import fiji.plugin.trackmate.io.IOUtils;
import ij.IJ;
import ij.ImageJ;
import ij.plugin.PlugIn;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import loci.formats.FormatException;

import org.xml.sax.SAXException;

public class NewMamutAnnotationPlugin implements PlugIn {

	private static File file;

	@Override
	public void run(final String fileStr) {

		final Logger logger = Logger.IJ_LOGGER;

		if (null != fileStr && fileStr.length() > 0) {
			// Skip dialog
			file = new File(fileStr);

		} else {

			if (null == file) {
				final File folder = new File(System.getProperty("user.dir")).getParentFile().getParentFile();
				file = new File(folder.getPath() + File.separator + "data.xml");
			}
			file = IOUtils.askForFileForLoading(file, "Open a hdf5/xml file", IJ.getInstance(), logger);
			if (null == file) {
				return;
			}
		}

		final MaMuT mamut = new MaMuT();
		try {
			mamut.launch(file);
		} catch (final FormatException e) {
			IJ.log(e.getMessage());
			e.printStackTrace();
		} catch (final IOException e) {
			IJ.log(e.getMessage());
			e.printStackTrace();
		} catch (final ParserConfigurationException e) {
			IJ.log(e.getMessage());
			e.printStackTrace();
		} catch (final SAXException e) {
			IJ.log(e.getMessage());
			e.printStackTrace();
		} catch (final InstantiationException e) {
			IJ.log(e.getMessage());
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			IJ.log(e.getMessage());
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			IJ.log(e.getMessage());
			e.printStackTrace();
		}

	}

	public static void main(final String[] args) {
		ImageJ.main(args);

		final NewMamutAnnotationPlugin plugin = new NewMamutAnnotationPlugin();
		// plugin.run("/Users/tinevez/Desktop/Data/Mamut/parhyale-crop/parhyale-crop-2.xml");
		plugin.run( "/Users/tinevez/Desktop/Data/Mamut/parhyale/BDV130418A325_NoTempReg.xml" );
	}

}
