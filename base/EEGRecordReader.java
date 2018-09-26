package test.base;

/**
 * EEGデータ読み込み
 */

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLStructure;
import org.apache.commons.io.FileUtils;
import org.datavec.api.conf.Configuration;
import org.datavec.api.io.labels.PathLabelGenerator;
import org.datavec.api.records.reader.BaseRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.datavec.common.RecordConverter;
import org.datavec.image.data.ImageWritable;
import org.datavec.image.loader.BaseImageLoader;
import org.datavec.image.loader.ImageLoader;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.transform.ImageTransform;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EEGRecordReader extends BaseRecordReader {
    public static final String[] ALLOWED_FORMATS = new String[]{"txt"};
    private static String DATA = "data";

    protected Iterator<File> iter;
    protected Configuration conf;
    protected File currentFile;
    protected PathLabelGenerator labelGenerator;
    protected List<INDArray> features;
    protected List<String> labels;
    protected boolean appendLabel;
    protected List<Writable> record;
    protected boolean hitImage;
    protected int channels;
    protected boolean cropImage;
    protected ImageTransform imageTransform;
    protected NativeMatLoader matLoader;
    protected InputSplit inputSplit;
    protected Map<String, String> fileNameMap;
    protected String pattern;
    protected int patternPosition;
    protected double normalizeValue;
    public static final String HEIGHT;
    public static final String WIDTH;
    public static final String CHANNELS;
    public static final String CROP_IMAGE;
    public static final String IMAGE_LOADER;

    public EEGRecordReader() {
        this.labelGenerator = null;
        this.features = new ArrayList();
        this.labels = new ArrayList();
        this.appendLabel = false;
        this.hitImage = false;
        this.channels = 1;
        this.cropImage = false;
        this.fileNameMap = new LinkedHashMap();
        this.patternPosition = 0;
        this.normalizeValue = 0.0D;
    }

    public EEGRecordReader(int channels,PathLabelGenerator labelGenerator) {
        this.labelGenerator = null;
        this.features = new ArrayList();
        this.labels = new ArrayList();
        this.appendLabel = false;
        this.hitImage = false;
        this.channels = channels;
        this.cropImage = false;
        this.fileNameMap = new LinkedHashMap();
        this.patternPosition = 0;
        this.normalizeValue = 0.0D;
        this.labelGenerator = labelGenerator;
        this.appendLabel = labelGenerator != null;
    }

    protected boolean containsFormat(String format) {
        String[] var2 = ALLOWED_FORMATS;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String format2 = var2[var4];
            if(format.endsWith("." + format2)) {
                return true;
            }
        }

        return false;
    }

    public void initialize(InputSplit split) throws IOException {
        if(this.matLoader == null) {
            this.matLoader = new NativeMatLoader(this.channels, this.normalizeValue);
        }

        this.inputSplit = split;
        URI[] locations = split.locations();
        if(locations != null && locations.length >= 1) {
            // 対象が一つしかない場合
            Object allFiles;
            if(locations.length <= 1 && !this.containsFormat(locations[0].getPath())) {
                File var12 = new File(locations[0]);
                if(!var12.exists()) {
                    throw new IllegalArgumentException("Path " + var12.getAbsolutePath() + " does not exist!");
                }

                if(var12.isDirectory()) {
                    allFiles = FileUtils.listFiles(var12, (String[])null, true);
                } else {
                    allFiles = Collections.singletonList(var12);
                }
            } else {
                //複数対象の場合
                allFiles = new ArrayList();
                URI[] split1 = locations;
                int var5 = locations.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    URI location = split1[var6];
                    File matFile = new File(location);
                    if(!matFile.isDirectory() && this.containsFormat(matFile.getAbsolutePath())) {
                        ((Collection)allFiles).add(matFile);
                    }

                    // ラベル設定
                    if(this.appendLabel) {
                        File parentDir = matFile.getParentFile();
                        String name = parentDir.getName();
                        if(this.labelGenerator != null) {
                            name = this.labelGenerator.getLabelForPath(location).toString();
                        }

                        if(!this.labels.contains(name)) {
                            this.labels.add(name);
                        }

                        if(this.pattern != null) {
                            String label = name.split(this.pattern)[this.patternPosition];
                            this.fileNameMap.put(matFile.toString(), label);
                        }
                    }
                }
            }

            this.iter = ((Collection)allFiles).iterator();
        }

        if(split instanceof FileSplit) {
            FileSplit var13 = (FileSplit)split;
            this.labels.remove(var13.getRootDir());
        }

    }

    public void initialize(Configuration configuration, InputSplit inputSplit) throws IOException, InterruptedException {

    }

    public List<Writable> next() {
        if(this.iter != null) {
            Object ret = new ArrayList();
            File mat = (File)this.iter.next();
            this.currentFile = mat;
            if(mat.isDirectory()) {
                return this.next();
            } else {
                try {
                    this.invokeListeners(mat);
                    INDArray e =this.matLoader.asMatrix(mat);
                    ret = RecordConverter.toRecord(e);
                    if(this.appendLabel) {
                        ((List)ret).add(new IntWritable(this.labels.indexOf(this.getLabel(mat.getPath()))));
                    }
                } catch (Exception var4) {
                    var4.printStackTrace();
                }

                return (List)ret;
            }
        } else if(this.record != null) {
            this.hitImage = true;
            this.invokeListeners(this.record);
            return this.record;
        } else {
            throw new IllegalStateException("No more elements");
        }
    }

    /**
     * MatData取得
     * @param path
     */
    public static INDArray getMatrix(String path) throws Exception  {
        //ファイルを読み込み
        MatFileReader read = new MatFileReader(path);
        Map<String, MLArray> mlArrayRetrived = read.getContent();
        Iterator it = mlArrayRetrived.values().iterator();
        MLArray mlArray = null;
        while (it.hasNext()) {
            mlArray = (MLArray) it.next();
            break;
        }

        //matファイルのデータ構造を取得
        MLStructure struct = (MLStructure) mlArray;
        MLDouble data = (MLDouble) struct.getField(DATA);

        //データ構造をログで出力
        if (data != null) {
            double[][] dataD = data.getArray();

            INDArray vectorD = Nd4j.create(dataD);
            return vectorD;
        } else {
            throw new IllegalStateException("There is not Matrix in .mat file.");
        }
    }

    public boolean hasNext() {
        if(this.iter != null) {
            boolean hasNext = this.iter.hasNext();
            if(!hasNext && this.imageTransform != null) {
                this.imageTransform.transform((ImageWritable)null);
            }

            return hasNext;
        } else if(this.record != null) {
            if(this.hitImage && this.imageTransform != null) {
                this.imageTransform.transform((ImageWritable)null);
            }

            return !this.hitImage;
        } else {
            if(this.imageTransform != null) {
                this.imageTransform.transform((ImageWritable)null);
            }

            throw new IllegalStateException("Indeterminant state: record must not be null, or a file iterator must exist");
        }
    }

    public void close() throws IOException {
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Configuration getConf() {
        return this.conf;
    }

    public String getLabel(String path) {
        return this.labelGenerator != null?this.labelGenerator.getLabelForPath(path).toString():(this.fileNameMap != null && this.fileNameMap.containsKey(path)?(String)this.fileNameMap.get(path):(new File(path)).getParentFile().getName());
    }

    protected void accumulateLabel(String path) {
        String name = this.getLabel(path);
        if(!this.labels.contains(name)) {
            this.labels.add(name);
        }

    }

    public File getCurrentFile() {
        return this.currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public List<String> getLabels() {
        return this.labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void reset() {
        if(this.inputSplit == null) {
            throw new UnsupportedOperationException("Cannot reset without first initializing");
        } else {
            try {
                this.initialize(this.inputSplit);
            } catch (Exception var2) {
                throw new RuntimeException("Error during LineRecordReader reset", var2);
            }
        }
    }

    public int numLabels() {
        return this.labels.size();
    }

    public List<Writable> record(URI uri, DataInputStream dataInputStream) throws IOException {
        throw new UnsupportedOperationException("Reading Matlab data from DataInputStream: not yet implemented");
    }

    static {
        HEIGHT = NAME_SPACE + ".height";
        WIDTH = NAME_SPACE + ".width";
        CHANNELS = NAME_SPACE + ".channels";
        CROP_IMAGE = NAME_SPACE + ".cropimage";
        IMAGE_LOADER = NAME_SPACE + ".imageloader";
    }
}
