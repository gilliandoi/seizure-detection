package test.tmp;

import org.apache.commons.io.FileUtils;
import org.datavec.api.util.ArchiveUtils;
import org.datavec.image.transform.ImageTransform;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.Random;

/**
 * Created by CTC0138 on 2017/03/15.
 */
public abstract class BaseMatLoader implements Serializable {
    protected static final Logger log = LoggerFactory.getLogger(org.datavec.image.loader.BaseImageLoader.class);
    public static final File BASE_DIR = new File(System.getProperty("user.home"));
    public static final String[] ALLOWED_FORMATS = new String[]{"mat"};
    protected Random rng = new Random(System.currentTimeMillis());
    protected int height = -1;
    protected int width = -1;
    protected int channels = -1;
    protected boolean centerCropIfNeeded = false;
    protected double normalizeValue = 0.0D;
    protected boolean normalizeIfNeeded = false;
    protected ImageTransform imageTransform = null;

    public BaseMatLoader() {
    }

    public String[] getAllowedFormats() {
        return ALLOWED_FORMATS;
    }

    public abstract INDArray asRowVector(File var1) throws IOException;

    public abstract INDArray asRowVector(InputStream var1) throws IOException;

    public abstract INDArray asMatrix(File var1) throws IOException;

    public abstract INDArray asMatrix(InputStream var1) throws IOException;

    public static void downloadAndUntar(Map urlMap, File fullDir) {
        try {
            File e = new File(fullDir, urlMap.get("filesFilename").toString());
            if(!e.isFile()) {
                FileUtils.copyURLToFile(new URL(urlMap.get("filesURL").toString()), e);
            }

            String fileName = e.toString();
            if(fileName.endsWith(".tgz") || fileName.endsWith(".tar.gz") || fileName.endsWith(".gz") || fileName.endsWith(".zip")) {
                ArchiveUtils.unzipFileTo(e.getAbsolutePath(), fullDir.getAbsolutePath());
            }

        } catch (IOException var4) {
            throw new IllegalStateException("Unable to fetch images", var4);
        }
    }
}
