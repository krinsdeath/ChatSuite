package net.krinsoft.chat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import net.krinsoft.chat.ChatCore;

/**
 *
 * @author krinsdeath
 */
public class ChatConfiguration {
    private static ChatCore plugin;

    public static void init(ChatCore p) {
        plugin = p;
    }

    public static File buildDefault(File p, String filename) {
        plugin.debug("Building file references..");
        File file = new File(p, filename);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.debug("Fetching resource... " + file.getName());
            InputStream in = ChatCore.class.getResourceAsStream("/defaults/" + file.getName());
            if (in != null) {
                plugin.debug("Resource exists as: " + file.getName());
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    byte[] buf = new byte[15];
                    int len = 0;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    plugin.log(file.getName() + " created successfully.");
                } catch (IOException e) {
                    plugin.debug(e.getLocalizedMessage());
                } finally {
                    try {
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        plugin.debug(e.getLocalizedMessage());
                    }
                }
            }
        }
        return file;
    }

}
