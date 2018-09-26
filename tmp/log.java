package test.tmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by CTC0138 on 2017/04/06.
 */
public class log {
    private static final Logger log = LoggerFactory.getLogger(log.class);
    public static void main(String[] args) throws Exception {
        log.info("TEST LOG!");
        log.debug("TEST");
        log.error("ERRORLOG");

        int str2s[][] = {{ 11, 12 },{ 21, 22 } };
        int strs[] = new int[4];
        System.out.println(str2s.length);

        int x =0;
        for (int i = 0; i < str2s.length; i++) {
            for (int j = 0; j < str2s[i].length; j++) {
                strs[x] = str2s[i][j];
                x++;
            }
        }
    }
}
